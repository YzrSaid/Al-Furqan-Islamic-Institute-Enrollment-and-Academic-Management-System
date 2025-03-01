package com.example.testingLogIn.Services;

import com.example.testingLogIn.Enums.EnrollmentStatus;
import com.example.testingLogIn.ModelDTO.StudentDTO;
import com.example.testingLogIn.Models.Enrollment;
import com.example.testingLogIn.Models.Section;
import com.example.testingLogIn.Models.Student;
import com.example.testingLogIn.Repositories.EnrollmentRepo;
import com.example.testingLogIn.Repositories.StudentRepo;
import com.example.testingLogIn.Repositories.sySemesterRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author magno
 */
@Service
public class EnrollmentServices {
    private final EnrollmentRepo enrollmentRepo;
    private final StudentRepo studentRepo;
    private final SectionServices sectionService;
    private final sySemesterRepo sySemRepo;

    @Autowired
    public EnrollmentServices(StudentRepo studentRepo, SectionServices sectionService, sySemesterRepo sySemRepo,EnrollmentRepo enrollmentRepo) {
        this.studentRepo = studentRepo;
        this.sectionService = sectionService;
        this.sySemRepo = sySemRepo;
        this.enrollmentRepo = enrollmentRepo;
    }
    
    public boolean addStudentToListing(StudentDTO stud){
        String sectionName = stud.getCurrentSection().substring(stud.getCurrentSection().indexOf("-")+1);
        Section section = sectionService.getSectionByName(sectionName);
        Student student = studentRepo.findByName(stud.getFirstName(), stud.getLastName());
        if(student == null)
            return false;
        
        Enrollment enroll = new Enrollment();
        enroll.setEnrollmentStatus(EnrollmentStatus.LISTING);
        enroll.setStudent(student);
        enroll.setSection(section);
        enroll.setSYSemester(sySemRepo.findCurrentActive());
        
        
        return true;
    }
}
