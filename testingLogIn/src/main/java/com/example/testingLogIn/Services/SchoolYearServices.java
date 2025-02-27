package com.example.testingLogIn.Services;

import com.example.testingLogIn.Models.SchoolYear;
import com.example.testingLogIn.Repositories.SchoolYearRepo;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 *
 * @author magno
 */
@Service
public class SchoolYearServices {
    @Autowired
    private SchoolYearRepo schoolYearRepo;
    @Autowired
    private sySemesterServices semService;
    
    public boolean addNewSchoolYear(SchoolYear sy){
        if(doesSchoolYearExist(sy.getSchoolYear()))
            return false;
        else
            sy.setNotDeleted(true);
            schoolYearRepo.save(sy);
        return true;
    }
    
    public SchoolYear getSchoolYear(int syNumber){
        return schoolYearRepo.findById(syNumber).orElse(null);
    }
    
    public List<SchoolYear> getSchoolYears(){
        return schoolYearRepo.findAll().stream()
                             .filter(SchoolYear::isNotDeleted)
                             .sorted((sy1,sy2)-> sy2.getSchoolYear().compareTo(sy1.getSchoolYear()))
                             .toList();
    }
    
    public boolean updateSchoolYear(SchoolYear sy){
        SchoolYear syUpdated = schoolYearRepo.findById(sy.getSchoolYearNum()).orElse(null);
        if(syUpdated != null){
            syUpdated.setActive(sy.isActive());
            syUpdated.setSchoolYear(sy.getSchoolYear());
            schoolYearRepo.save(syUpdated);
            
            return true;
        }
        return false;
    }
    
    //activate school year
    public int activateSY(int syNumber){
        SchoolYear sy = schoolYearRepo.findById(syNumber).orElse(null);
        
        if(sy == null)
            throw new NullPointerException();
        else if(!semService.hasFirstSem(sy)){
            semService.addNewSemester(sy, 1);
            return 1;
        }else{
            semService.addNewSemester(sy, 2);
            return 2;
        }
        
    }
    
    public boolean deleteSchoolYear(int syNumber){
        SchoolYear syDelete = schoolYearRepo.findById(syNumber).orElse(null);
        if(syDelete != null){
            syDelete.setNotDeleted(false);
            schoolYearRepo.save(syDelete);
            return true;
        }
        return false;
    }
    
    private boolean doesSchoolYearExist(String schoolYear){
        return schoolYearRepo.findAll().stream()
                             .filter(sy -> sy.isNotDeleted() && sy.getSchoolYear().equals(schoolYear))
                             .findFirst().orElse(null) != null;
    }
}
