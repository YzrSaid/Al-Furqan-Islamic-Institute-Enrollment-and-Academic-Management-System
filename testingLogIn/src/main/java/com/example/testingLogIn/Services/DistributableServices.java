package com.example.testingLogIn.Services;

import com.example.testingLogIn.AssociativeModels.DistributablesPerGrade;
import com.example.testingLogIn.Models.Distributable;
import com.example.testingLogIn.Models.GradeLevel;
import com.example.testingLogIn.Repositories.DistributableRepo;
import com.example.testingLogIn.Repositories.GradeLevelRepo;
import com.example.testingLogIn.Repositories.GradesDistributableRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class DistributableServices {

    @Autowired
    private DistributableRepo distributableRepo;
    @Autowired
    private GradesDistributableRepo gradesDistributableRepo;
    @Autowired
    private GradeLevelRepo gradeLevelRepo;

    public Distributable getByName(String distributableName){
        return distributableRepo.findByName(NonModelServices.forLikeOperator(distributableName)).orElse(null);
    }

    public boolean addNewDistributable(String itemName, List<GradeLevel> gradeLevels, List<Integer> levelIds){
        if(getByName(itemName) != null)
            return false;

        Distributable item = distributableRepo.save(new Distributable(itemName));
        Runnable toRun;
        if(gradeLevels != null){
            toRun = new Runnable() {
                @Override
                public void run() {
                    gradeLevels.forEach(gradeLevel -> {
                        gradesDistributableRepo.save(new DistributablesPerGrade(item,gradeLevel));
                    });
                }
            };
        }else{
            toRun = new Runnable() {
                @Override
                public void run() {
                    gradeLevelRepo.findAll().stream()
                        .filter(gradeLevel -> gradeLevel.isNotDeleted() &&
                                levelIds.contains(gradeLevel.getLevelId()))
                        .forEach(gradeLevel -> {
                            gradesDistributableRepo.save(new DistributablesPerGrade(item,gradeLevel));
                        });
                }
            };
        }
        CompletableFuture.runAsync(toRun);
        return true;
    }

    public List<Distributable> getAllDistributable(boolean isNotDeleted){
        if(isNotDeleted)
            return distributableRepo.findByIsNotDeletedTrue().stream()
                    .filter(dis ->
                            dis.getGradeList().stream().filter(DistributablesPerGrade::isNotDeleted).isParallel())
                    .toList();

        return distributableRepo.findByIsNotDeletedFalse().stream()
                .filter(dis ->
                        dis.getGradeList().stream().filter(DistributablesPerGrade::isNotDeleted).isParallel())
                .toList();
    }
}
