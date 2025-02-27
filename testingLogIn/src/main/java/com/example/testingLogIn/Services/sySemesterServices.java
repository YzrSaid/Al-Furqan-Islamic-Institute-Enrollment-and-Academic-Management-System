package com.example.testingLogIn.Services;

import com.example.testingLogIn.Enums.Semester;
import com.example.testingLogIn.Models.SchoolYear;
import com.example.testingLogIn.Models.SchoolYearSemester;
import com.example.testingLogIn.Repositories.SchoolYearRepo;
import com.example.testingLogIn.Repositories.sySemesterRepo;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 *
 * @author magno
 */
@Service
public class sySemesterServices {
    @Autowired
    private sySemesterRepo semesterRepo;
    @Autowired
    private SchoolYearRepo syRepo;
    
    public void addSemesters(String schoolYearName){
        SchoolYear sy = syRepo.getByName(schoolYearName);
        if(sy != null){
            int count = 0;
            while(count<2){
                Semester sem = count++ == 1? Semester.First : Semester.Second;
                
                SchoolYearSemester sySem = new SchoolYearSemester();
                sySem.setNotDeleted(true);
                sySem.setActive(false);
                sySem.setSchoolYear(sy);
                sySem.setSem(sem);
                
                semesterRepo.save(sySem);
            }
        }
    }
    
    public List<SchoolYearSemester> getAllSemesters(){
        return semesterRepo.findAll().stream()
                           .filter(SchoolYearSemester::isNotDeleted)
                           .toList();
    }
    
    private void disableAllActive(){
        semesterRepo.findAll().forEach(sem -> {
            sem.setActive(false);
            semesterRepo.save(sem);});
    }
    
}
