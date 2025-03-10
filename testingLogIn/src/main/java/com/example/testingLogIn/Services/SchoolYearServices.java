package com.example.testingLogIn.Services;

import com.example.testingLogIn.Models.SchoolYear;
import com.example.testingLogIn.Repositories.SchoolYearRepo;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        else{
            sy.setNotDeleted(true);
            schoolYearRepo.save(sy);
            
            new Thread(() -> {
                try {
                    Thread.sleep(499);
                } catch (InterruptedException ex) {
                    Logger.getLogger(SchoolYearServices.class.getName()).log(Level.SEVERE, null, ex);
                }
                semService.addSemesters(sy.getSchoolYear());}).start();
            
            return true;}
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
        if(syUpdated == null)
            throw new NullPointerException();
        
        boolean doesSchoolYearExist = schoolYearRepo.findAll().stream()
                                                    .filter(schoolY ->  schoolY.isNotDeleted() &&
                                                                        schoolY.getSchoolYearNum() != sy.getSchoolYearNum() &&
                                                                        schoolY.getSchoolYear().equalsIgnoreCase(sy.getSchoolYear()))
                                                    .findFirst().orElse(null) != null;
        if(doesSchoolYearExist)
            return false;
        
        syUpdated.setSchoolYear(sy.getSchoolYear());
        schoolYearRepo.save(syUpdated);

        return true;
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
                             .filter(sy -> sy.isNotDeleted() && sy.getSchoolYear().equalsIgnoreCase(schoolYear))
                             .findFirst().orElse(null) != null;
    }
}
