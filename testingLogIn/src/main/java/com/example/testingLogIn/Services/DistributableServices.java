package com.example.testingLogIn.Services;

import com.example.testingLogIn.AssociativeModels.DistributablesPerGrade;
import com.example.testingLogIn.AssociativeModels.DistributablesPerStudent;
import com.example.testingLogIn.ModelDTO.DistributableDTO;
import com.example.testingLogIn.Models.*;
import com.example.testingLogIn.CustomObjects.PagedResponse;
import com.example.testingLogIn.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.naming.NameAlreadyBoundException;
import java.time.LocalDate;
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
        return distributableRepo.findByName(distributableName.toLowerCase()).orElse(null);
    }
    public DistributableDTO getById(int itemId){
        return distributableRepo.findById(itemId).map(Distributable::DTOmapper)
                .orElseThrow(()->new NullPointerException("Distributable record not found"));
    }
    public boolean addNewDistributable(DistributableDTO item) {
        assert item != null;
        if(getByName(item.getItemName()) != null)
            throw new IllegalArgumentException("Distributable Name item already exists");

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

        gradeLevelRepo.findAll().stream()
        .filter(gradeLevel -> gradeLevel.isNotDeleted() &&
            item.getGradeLevelIds().contains(gradeLevel.getLevelId())
        ).forEach(gradeLevel -> {
            DistributablesPerGrade gradeDispo = gradesDistributableRepo.save(new DistributablesPerGrade(newItem,gradeLevel));
            if(newItem.isCurrentlyActive() && currentSemId > 0)
                studentList.put(gradeDispo,enrollmentRepo.getCurrentlyEnrolledToGrade(gradeLevel.getLevelId(),currentSemId));
        });
        CompletableFuture.runAsync(()->{
            if(newItem.isCurrentlyActive() && currentSemId > 0 && !studentList.isEmpty()){
                for(DistributablesPerGrade itemPerGrade : studentList.keySet())
                    studentList.get(itemPerGrade).forEach(student ->
                            studentDistributables.add(DistributablesPerStudent.build(itemPerGrade,student,currentSem)));

                disStudRepo.saveAll(studentDistributables);
            }
        });
        return true;
    }

    public void updateDistributable(DistributableDTO updatedItem){
        Distributable toUpdate = distributableRepo.findById(updatedItem.getItemId())
                .orElseThrow(() -> new NullPointerException("Distributable record not found"));
        Distributable existingItem = getByName(updatedItem.getItemName());
        if(existingItem != null && existingItem.getItemId() != toUpdate.getItemId())
            throw new IllegalArgumentException("Distributable Name item already exists");

        toUpdate.setItemName(updatedItem.getItemName());
        SchoolYearSemester currentSem = semesterServices.getCurrentActive();
        int currentSemId = Optional.ofNullable(currentSem).map(SchoolYearSemester::getSySemNumber).orElse(0);
        List<DistributablesPerGrade> gradeExistingDistributable = gradesDistributableRepo.findByDistributableItem(toUpdate.getItemId());
        List<DistributablesPerGrade> validGradeDists = new ArrayList<>();

        gradeExistingDistributable.forEach(gradeDis ->{
            gradeDis.setNotDeleted(true);
            if(!updatedItem.getGradeLevelIds().contains(gradeDis.getGradeLevel().getLevelId()))
                gradeDis.setNotDeleted(false);

            if(gradeDis.isNotDeleted() && updatedItem.isCurrentlyActive())
                validGradeDists.add(gradeDis);

            CompletableFuture.runAsync(()->disStudRepo.updateStudentToReceive(gradeDis.getId(), currentSemId, gradeDis.isNotDeleted()));
            updatedItem.getGradeLevelIds().remove((Integer)gradeDis.getGradeLevel().getLevelId());
        });

        gradesDistributableRepo.saveAll(gradeExistingDistributable);

        gradesDistributableRepo.saveAll(gradeExistingDistributable);
        List<DistributablesPerGrade> newGradesDistributable = new ArrayList<>();
        for(Integer levelId : updatedItem.getGradeLevelIds()){
            gradeLevelRepo.findById(levelId).ifPresent(gradeLevel -> newGradesDistributable.add(new DistributablesPerGrade(toUpdate, gradeLevel)));}

        Iterable<DistributablesPerGrade> toIterate = gradesDistributableRepo.saveAll(newGradesDistributable);
        if(updatedItem.isCurrentlyActive() && currentSemId > 0)
            CompletableFuture.runAsync(()->{
               List<DistributablesPerStudent> studentDistribution = new ArrayList<>();
               toIterate.forEach(gradeDis->
                   disStudRepo.findEnrolledStudentsNoRecord(gradeDis.getGradeLevel().getLevelId(),currentSemId, gradeDis.getId())
                           .forEach(stud -> studentDistribution.add(DistributablesPerStudent.build(gradeDis,stud,currentSem))));
               validGradeDists.forEach(gradeDis ->
                       disStudRepo.findEnrolledStudentsNoRecord(gradeDis.getGradeLevel().getLevelId(),currentSemId, gradeDis.getId())
                               .forEach(stud -> studentDistribution.add(DistributablesPerStudent.build(gradeDis,stud,currentSem))));
               disStudRepo.saveAll(studentDistribution);
            });

        toUpdate.setCurrentlyActive(updatedItem.isCurrentlyActive());
        distributableRepo.save(toUpdate);
    }

    public List<DistributableDTO> getAllDistributable(boolean isNotDeleted){
        if(isNotDeleted)
            return distributableRepo.findByIsNotDeletedTrue().stream()
                    .map(Distributable::DTOmapper).toList();

        return distributableRepo.findByIsNotDeletedFalse().stream()
                .map(Distributable::DTOmapper).toList();
    }

    public void deleteDistributable(int itemId){
        Distributable item = distributableRepo.findById(itemId).orElseThrow(()->new NullPointerException("Distributable not found"));
        int currentSem = Optional.ofNullable(semesterServices.getCurrentActive()).map(SchoolYearSemester::getSySemNumber).orElse(0);
        CompletableFuture.runAsync(()->{
           gradesDistributableRepo.findByDistributableItem(itemId)
                   .forEach(gDis ->{
                       gDis.setNotDeleted(false);
                       disStudRepo.setAsDeleted(gDis.getId(),currentSem);
                       gradesDistributableRepo.save(gDis);
                   });
        });
        item.setNotDeleted(false);
        distributableRepo.save(item);
    }

    // for distributing student learning materials
    public void setStudentItemToReceive(Enrollment enrollment){
        gradesDistributableRepo.findByDistributableByGradeLevel(enrollment.getGradeLevelToEnroll().getLevelId())
                .forEach(gradeDis -> disStudRepo.save(DistributablesPerStudent.build(gradeDis,enrollment.getStudent(),enrollment.getSYSemester())));
    }

    public PagedResponse getStudentDistributable(int pageNo, int pageSize, String student, Boolean isClaimed, Integer item, String sortByGrade){
        student = NonModelServices.forLikeOperator(student);
        Pageable pageRequest;
        if(sortByGrade.equalsIgnoreCase("none"))
            pageRequest = PageRequest.of(pageNo-1,pageSize);
        else
            pageRequest = sortByGrade.equalsIgnoreCase("asc") ?
                    PageRequest.of(pageNo-1, pageSize, Sort.by(Sort.Order.asc("d.item.gradeLevel"))) :
                    PageRequest.of(pageNo-1, pageSize, Sort.by(Sort.Order.desc("d.item.gradeLevel")));

        Page<DistributablesPerStudent> studentPage = disStudRepo.getStudentDistPage(student,isClaimed,item,pageRequest);
        return PagedResponse.builder()
                .content(studentPage.getContent().stream().map(DistributablesPerStudent::DTOmapper).collect(Collectors.toList()))
                .pageNo(studentPage.getNumber())
                .pageSize(studentPage.getSize())
                .totalElements(studentPage.getTotalElements())
                .totalPages(studentPage.getTotalPages())
                .isLast(studentPage.isLast())
                .build();
    }

    public void itemDistributed(int distributionId){
        DistributablesPerStudent studentItem = disStudRepo.findById(distributionId)
                .orElseThrow(()->new NullPointerException("Distribution record not found"));
        studentItem.setReceived(true);
        studentItem.setDateReceived(LocalDate.now());
        disStudRepo.save(studentItem);
    }
    public void multipleItemDistributed(List<Integer> distributionIds){
            List<DistributablesPerStudent> studentDistributions = new ArrayList<>();
            distributionIds.forEach(distributionId ->{
                DistributablesPerStudent studentItem = disStudRepo.findById(distributionId)
                        .orElseThrow(()->new NullPointerException("Distribution record not found"));
                studentItem.setReceived(true);
                studentItem.setDateReceived(LocalDate.now());
                studentDistributions.add(studentItem);
            });
            disStudRepo.saveAll(studentDistributions);
    }
}
