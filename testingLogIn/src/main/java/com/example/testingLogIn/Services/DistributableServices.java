package com.example.testingLogIn.Services;

import com.example.testingLogIn.AssociativeModels.DistributablesPerGrade;
import com.example.testingLogIn.AssociativeModels.DistributablesPerStudent;
import com.example.testingLogIn.ModelDTO.DistributableDTO;
import com.example.testingLogIn.Models.*;
import com.example.testingLogIn.PagedResponse.StudentDistributablePage;
import com.example.testingLogIn.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.util.concurrent.CompletableFuture.runAsync;

@Service
public class DistributableServices {

    private final DistributableRepo distributableRepo;
    private final GradesDistributableRepo gradesDistributableRepo;
    private final GradeLevelRepo gradeLevelRepo;
    private final EnrollmentRepo enrollmentRepo;
    private final sySemesterServices semesterServices;
    private final DistributablePerStudentRepo disStudRepo;

    @Autowired
    public DistributableServices(DistributableRepo distributableRepo, GradesDistributableRepo gradesDistributableRepo, GradeLevelRepo gradeLevelRepo, EnrollmentRepo enrollmentRepo, sySemesterServices semesterServices, DistributablePerStudentRepo disStudRepo) {
        this.distributableRepo = distributableRepo;
        this.gradesDistributableRepo = gradesDistributableRepo;
        this.gradeLevelRepo = gradeLevelRepo;
        this.enrollmentRepo = enrollmentRepo;
        this.semesterServices = semesterServices;
        this.disStudRepo = disStudRepo;
    }

    public Distributable getByName(String distributableName){
        return distributableRepo.findByName(NonModelServices.forLikeOperator(distributableName)).orElse(null);
    }
    public DistributableDTO getById(int itemId){
        return distributableRepo.findById(itemId).map(Distributable::DTOmapper).orElse(null);
    }
    public boolean addNewDistributable(DistributableDTO item) {
        assert item != null;
        if(getByName(item.getItemName()) != null)
            return false;

        Distributable newDistributable = new Distributable(
                    item.getItemName(),
                    item.isCurrentlyActive()
            );

        // Save and return result
        distributableRepo.save(newDistributable);

        Distributable newItem = distributableRepo.save(newDistributable);
        SchoolYearSemester currentSem = semesterServices.getCurrentActive();
        List<DistributablesPerStudent> studentDistributables = new ArrayList<>();
        int currentSemId = Optional.ofNullable(currentSem).map(SchoolYearSemester::getSySemNumber).orElse(0);
        Map<DistributablesPerGrade,List<Student>> studentList = new HashMap<>();

        Runnable gradeLevelDistributable = () -> {
            gradeLevelRepo.findAll().stream()
            .filter(gradeLevel -> gradeLevel.isNotDeleted() &&
                item.getGradeLevelIds().contains(gradeLevel.getLevelId())
            ).forEach(gradeLevel -> {
                DistributablesPerGrade gradeDispo = gradesDistributableRepo.save(new DistributablesPerGrade(newItem,gradeLevel));
                if(newItem.isCurrentlyActive() && currentSemId != 0)
                    studentList.put(gradeDispo,enrollmentRepo.getCurrentlyEnrolledToGrade(gradeLevel.getLevelId(),currentSemId));
            });
            if(newItem.isCurrentlyActive() && currentSemId != 0 && !studentList.isEmpty()){
                for(DistributablesPerGrade itemPerGrade : studentList.keySet())
                    studentList.get(itemPerGrade).forEach(student ->
                            studentDistributables.add(DistributablesPerStudent.build(itemPerGrade,student,currentSem)));

                disStudRepo.saveAll(studentDistributables);
            }
        };
        runAsync(gradeLevelDistributable);
        return true;
    }

    public boolean updateDistributable(DistributableDTO updatedItem){
        Distributable toUpdate = distributableRepo.findById(updatedItem.getItemId()).orElseThrow(NullPointerException::new);
        Distributable existingItem = getByName(updatedItem.getItemName());
        if(existingItem != null && existingItem.getItemId() != toUpdate.getItemId())
            return false;

        Map<DistributablesPerGrade,List<Student>> studentList = new HashMap<>();
        toUpdate.setItemName(updatedItem.getItemName());
        SchoolYearSemester currentSem = semesterServices.getCurrentActive();
        int currentSemId = Optional.ofNullable(currentSem).map(SchoolYearSemester::getSySemNumber).orElse(0);

        Runnable updateGradeDistributables = () -> {
            List<DistributablesPerGrade> gradeExistingDistributable = gradesDistributableRepo.findByDistributableItem(toUpdate.getItemId());
            gradeExistingDistributable.forEach(gradeDis ->{
                gradeDis.setNotDeleted(true);
                if(!updatedItem.getGradeLevelIds().contains(gradeDis.getGradeLevel().getLevelId()))
                    gradeDis.setNotDeleted(false);

                if(!toUpdate.isCurrentlyActive() && updatedItem.isCurrentlyActive()){
                    studentList.put(gradeDis,enrollmentRepo.getCurrentlyEnrolledToGrade(gradeDis.getGradeLevel().getLevelId(),currentSemId));
                }
                updatedItem.getGradeLevelIds().remove((Integer)gradeDis.getGradeLevel().getLevelId());
            });
            gradesDistributableRepo.saveAll(gradeExistingDistributable);
            List<DistributablesPerGrade> newGradesDistributable = new ArrayList<>();
            for(Integer levelId : updatedItem.getGradeLevelIds()){
                gradeLevelRepo.findById(levelId).ifPresent(gradeLevel -> newGradesDistributable.add(new DistributablesPerGrade(toUpdate, gradeLevel)));}

            Iterable<DistributablesPerGrade> toIterate = gradesDistributableRepo.saveAll(newGradesDistributable);
            if(updatedItem.isCurrentlyActive()){
                List<DistributablesPerStudent> studentDistributable = new ArrayList<>();
                toIterate.forEach(gradelvl ->
                        studentList.put(gradelvl,enrollmentRepo.getCurrentlyEnrolledToGrade(gradelvl.getGradeLevel().getLevelId(),currentSemId)));

                for(DistributablesPerGrade gradeDis : studentList.keySet())
                    studentList.get(gradeDis).forEach(student -> {
                        studentDistributable.add(DistributablesPerStudent.build(gradeDis,student,currentSem));
                    });
                disStudRepo.saveAll(studentDistributable);
            }
        };
        CompletableFuture<Void> runThis = CompletableFuture.runAsync(updateGradeDistributables);
        CompletableFuture.allOf(runThis).join();
        toUpdate.setCurrentlyActive(updatedItem.isCurrentlyActive());
        distributableRepo.save(toUpdate);
        return true;
    }

    public List<DistributableDTO> getAllDistributable(boolean isNotDeleted){
        if(isNotDeleted)
            return distributableRepo.findByIsNotDeletedTrue().stream()
                    .map(Distributable::DTOmapper).toList();

        return distributableRepo.findByIsNotDeletedFalse().stream()
                .map(Distributable::DTOmapper).toList();
    }

    public boolean deleteDistributable(int itemId){
        Distributable item = distributableRepo.findById(itemId).orElseThrow(NullPointerException::new);
        item.setNotDeleted(false);
        distributableRepo.save(item);
        return true;
    }

    // for distributing student learning materials
    public void setStudentItemToReceive(Enrollment enrollment){
        gradesDistributableRepo.findByDistributableByGradeLevel(enrollment.getGradeLevelToEnroll().getLevelId())
                .forEach(gradeDis -> disStudRepo.save(DistributablesPerStudent.build(gradeDis,enrollment.getStudent(),enrollment.getSYSemester())));
    }

    public StudentDistributablePage getStudentDistributable(int pageNo, int pageSize, String student, String viewBy){
        Pageable pageRequest = PageRequest.of(pageNo-1,pageSize);
        Page<DistributablesPerStudent> studentPage = disStudRepo.getStudentDistPage(pageRequest);
        return StudentDistributablePage.builder()
                .content(studentPage.getContent().stream().map(DistributablesPerStudent::DTOmapper).collect(Collectors.toList()))
                .pageNo(studentPage.getNumber())
                .pageSize(studentPage.getSize())
                .totalElements(studentPage.getTotalPages())
                .totalPages(studentPage.getTotalPages())
                .isLast(studentPage.isLast())
                .build();
    }
}
