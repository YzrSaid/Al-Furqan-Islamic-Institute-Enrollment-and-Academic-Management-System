package com.example.testingLogIn.CountersRepositories;

import com.example.testingLogIn.StatisticsModel.SectionStudentCountRepo;
import com.example.testingLogIn.StatisticsModel.SectionStudentCount;
import com.example.testingLogIn.Models.Enrollment;
import com.example.testingLogIn.Models.SchoolYearSemester;
import com.example.testingLogIn.Models.Section;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SectionStudentCountServices {
    @Autowired
    private SectionStudentCountRepo sectionStudentCountRepo;

    public void updateSectionIfExist(Enrollment enrollment){
        Section section = enrollment.getSectionToEnroll();
        SchoolYearSemester sem = enrollment.getSYSemester();
        SectionStudentCount ssc = sectionStudentCountRepo.findBySectionAndSemester(section.getNumber(), sem.getSySemNumber()).orElse(null);
        if(ssc == null)
            sectionStudentCountRepo.save(SectionStudentCount.builder()
                            .section(section)
                            .sySem(sem)
                            .studentCount(1)
                            .build());
        else{
            ssc.setStudentCount(ssc.getStudentCount()+1);
            sectionStudentCountRepo.save(ssc);
        }
    }

    public SectionStudentCount findSectionCount(int sectionId, int semId){
        return sectionStudentCountRepo.findBySectionAndSemester(sectionId,semId).orElse(null);
    }
}
