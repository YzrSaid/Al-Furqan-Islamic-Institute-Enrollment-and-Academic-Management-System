package com.example.testingLogIn.Services;

import com.example.testingLogIn.Models.GradeLevel;
import com.example.testingLogIn.Repositories.GradeLevelRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GradeLevelServices {

    @Autowired
    GradeLevelRepo gradeLevelRepo;

    public boolean addNewGradeLevel(GradeLevel gradeLevel){
        gradeLevelRepo.save(gradeLevel);
        return true;
    }

    public List<GradeLevel> getAllGradeLevels(){
        return gradeLevelRepo.findAll();
    }

    public boolean doesLevelExist(String levelname) {
        for(GradeLevel Current : gradeLevelRepo.findAll()){
            if(Current.getLevelName().equals(levelname)){
                return true;
            }
        }
        return false;
    }

    public List<GradeLevel> getAll(){
        return gradeLevelRepo.findAll();
    }
}
