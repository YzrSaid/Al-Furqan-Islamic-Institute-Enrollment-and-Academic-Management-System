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
    private SchoolYearRepo SYRepo;
    
    public boolean addNewSemester(SchoolYear sy,int sem){
        SchoolYear schoolYear = SYRepo.findById(sy.getSchoolYearNum()).orElse(null);
        
        if(schoolYear == null)
            return false;
        
        SchoolYearSemester semester = new SchoolYearSemester();
        semester.setSchoolYear(schoolYear);
        if(sem == 1)
            semester.setSem(Semester.First);
        else
            semester.setSem(Semester.Second);
        
        semester.setActive(true);
        semester.setNotDeleted(true);
        
        semesterRepo.disableAllActive();
        semesterRepo.save(semester);
        return true;
    }
    
    public List<SchoolYearSemester> getAllSemesters(){
        return semesterRepo.findAll().stream()
                           .filter(SchoolYearSemester::isNotDeleted)
                           .toList();
    }
    
    public boolean hasFirstSem(SchoolYear sy){
        return semesterRepo.findAll().stream()
                      .filter(sem ->sem.getSchoolYear().getSchoolYearNum() == sy.getSchoolYearNum() &&
                                    sem.isNotDeleted() &&
                                    sem.getSem() == Semester.First)
                        .findFirst().orElse(null) != null;
    }
    
}
