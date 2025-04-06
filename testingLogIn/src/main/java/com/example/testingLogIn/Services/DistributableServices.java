package com.example.testingLogIn.Services;

import com.example.testingLogIn.AssociativeModels.DistributablesPerGrade;
import com.example.testingLogIn.AssociativeModels.DistributablesPerStudent;
import com.example.testingLogIn.ModelDTO.DistributableDTO;
import com.example.testingLogIn.Models.Distributable;
import com.example.testingLogIn.Models.GradeLevel;
import com.example.testingLogIn.Models.SchoolYearSemester;
import com.example.testingLogIn.Models.Student;
import com.example.testingLogIn.Repositories.*;
import io.netty.util.concurrent.CompleteFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.runAsync;

@Service
public class DistributableServices {

    @Autowired
    private DistributableRepo distributableRepo;
    @Autowired
    private GradesDistributableRepo gradesDistributableRepo;
    @Autowired
    private GradeLevelRepo gradeLevelRepo;
    @Autowired
    private EnrollmentRepo enrollmentRepo;
    @Autowired
    private sySemesterServices semesterServices;
    @Autowired
    private DistributablePerStudent disStudRepo;

    public Distributable getByName(String distributableName){
        return distributableRepo.findByName(NonModelServices.forLikeOperator(distributableName)).orElse(null);
    }
    public DistributableDTO getByid(int itemId){
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
        List<Student> studentList = new ArrayList<>();

        Runnable gradeLevelDistributable = () -> {
            gradeLevelRepo.findAll().stream()
            .filter(gradeLevel -> gradeLevel.isNotDeleted() &&
                item.getGradeLevelIds().contains(gradeLevel.getLevelId())
            ).forEach(gradeLevel -> {
                gradesDistributableRepo.save(new DistributablesPerGrade(newItem,gradeLevel));
                if(currentSemId != 0)
                    studentList.addAll(enrollmentRepo.getCurrentlyEnrolledToGrade(gradeLevel.getLevelId(),currentSemId));
            });
            if(newItem.isCurrentlyActive() && currentSemId != 0 && !studentList.isEmpty()){
                System.out.println("Will add students");
                studentList.forEach(student ->
                        studentDistributables.add(DistributablesPerStudent.build(newItem,student,currentSem)));
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

        List<Student> studentList = new ArrayList<>();
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
                    studentList.addAll(enrollmentRepo.getCurrentlyEnrolledToGrade(gradeDis.getGradeLevel().getLevelId(),currentSemId));
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
                        studentList.addAll(enrollmentRepo.getCurrentlyEnrolledToGrade(gradelvl.getGradeLevel().getLevelId(),currentSemId)));

                studentList.forEach(student -> {
                    studentDistributable.add(DistributablesPerStudent.build(toUpdate,student,currentSem));
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
}
