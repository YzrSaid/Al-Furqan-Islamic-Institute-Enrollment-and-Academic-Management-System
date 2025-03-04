package com.example.testingLogIn.Services;

import com.example.testingLogIn.Enums.EnrollmentStatus;
import com.example.testingLogIn.ModelDTO.EnrollmentDTO;
import com.example.testingLogIn.ModelDTO.StudentDTO;
import com.example.testingLogIn.Models.Enrollment;
import com.example.testingLogIn.Models.GradeLevel;
import com.example.testingLogIn.Models.PaymentCompleteCheck;
import com.example.testingLogIn.Models.Section;
import com.example.testingLogIn.Models.Student;
import com.example.testingLogIn.Repositories.EnrollmentRepo;
import com.example.testingLogIn.Repositories.GradeLevelRepo;
import com.example.testingLogIn.Repositories.SectionRepo;
import com.example.testingLogIn.Repositories.StudentRepo;
import com.example.testingLogIn.Repositories.sySemesterRepo;
import java.util.List;
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
    private final SectionRepo sectionRepo;
    private final sySemesterRepo sySemRepo;
    private final GradeLevelRepo gradeLevelRepo;
    private final StudentSubjectGradeServices ssgService;
    private final PaymentCheckerService pcs;
    @Autowired
    public EnrollmentServices(StudentRepo studentRepo, SectionRepo sectionRepo, sySemesterRepo sySemRepo,
                                EnrollmentRepo enrollmentRepo, GradeLevelRepo gradeLevelRepo, StudentSubjectGradeServices ssgService, 
                                PaymentCheckerService pcs) {
        this.studentRepo = studentRepo;
        this.sectionRepo = sectionRepo;
        this.sySemRepo = sySemRepo;
        this.enrollmentRepo = enrollmentRepo;
        this.gradeLevelRepo = gradeLevelRepo;
        this.ssgService = ssgService;
        this.pcs=pcs;
    }
    
    public boolean addStudentToListing(StudentDTO stud){
        Student student = studentRepo.findByName(stud.getFirstName(), stud.getLastName());
        if(student == null || !student.isNotDeleted())
            throw new NullPointerException();
        else if(enrollmentRepo.studentCurrentlyEnrolled(stud.getFirstName(),
                                                            stud.getLastName(), 
                                                            sySemRepo.findCurrentActive().getSySemNumber()))
            return false;
        
        Enrollment enroll = new Enrollment();
        enroll.setEnrollmentStatus(EnrollmentStatus.LISTING);
        enroll.setStudent(student);
        enroll.setSYSemester(sySemRepo.findCurrentActive());
        enroll.setNotDeleted(true);
        enrollmentRepo.save(enroll);
        
        return true;
    }
    
    public int addToAssessment(int enrollmentId, int gradeLevelId){
        Enrollment enrollmentRecord = enrollmentRepo.findById(enrollmentId).orElse(null);
        GradeLevel gradeLevelToEnroll = gradeLevelRepo.findById(gradeLevelId).orElse(null);
        Student student = enrollmentRecord.getStudent();
        if(!enrollmentRecord.isNotDeleted())
            return 1;
        else if(gradeLevelToEnroll == null)
            return 2;
        else{
            enrollmentRecord.setEnrollmentStatus(EnrollmentStatus.ASSESSMENT);
            enrollmentRecord.setGradeLevelToEnroll(gradeLevelToEnroll);
            enrollmentRecord.setRemarks(null);
            boolean isQualified = true;
            if(!student.isNew() && gradeLevelToEnroll.getPreRequisite() != null){
                int nextLevelPreReqId = gradeLevelToEnroll.getPreRequisite().getLevelId();
                isQualified = ssgService.didStudentPassed(student.getStudentId(),
                        nextLevelPreReqId);
                if((!isQualified) && student.getCurrentGradeSection().getLevel().getLevelId() == nextLevelPreReqId){
                    enrollmentRecord.setRemarks("Congratulation! You Are Qualified To Your Current Grade");
                }else if(!isQualified)
                    enrollmentRecord.setRemarks("The Chosen Grade Level is Not The Successor of the Student's Current Grade Level. Choose another!");
                else
                    enrollmentRecord.setRemarks("Pagbayad na para maenroll naka");
            }
            enrollmentRecord.setComplete(false);
            enrollmentRecord.setQualified(isQualified);
            enrollmentRepo.save(enrollmentRecord);
            
            return 3;
        }
    }
    
    public int addToPayment(int enrollmentId, int sectionNumber){
        Enrollment enrollmentRecord = enrollmentRepo.findById(enrollmentId).orElse(null);
        Section section = sectionRepo.findById(sectionNumber).orElse(null);
        
        if(enrollmentRecord == null || !enrollmentRecord.isNotDeleted())
            return 1;
        else if(section == null)
            return 2;
        else{
            PaymentCompleteCheck checker = pcs.addIfNotExistElseUpdate(enrollmentRecord.getStudent().getStudentId(), 
                                                                    section.getLevel().getLevelId());
            
            enrollmentRecord.setPcc(checker);
            enrollmentRecord.setEnrollmentStatus(EnrollmentStatus.PAYMENT);
            enrollmentRecord.setSectionToEnroll(section);
            enrollmentRepo.save(enrollmentRecord);
            
            return 3;
        }
    }
    
    public void updatepcs(int studentId){
        Enrollment toUpdate = enrollmentRepo.getRecordByStudent(studentId, sySemRepo.findCurrentActive().getSySemNumber());
        if(toUpdate != null && toUpdate.getSectionToEnroll() != null)
            pcs.addIfNotExistElseUpdate(studentId,toUpdate.getSectionToEnroll().getLevel().getLevelId());
    }
    
    public int addToEnrolled(int enrollmentId){
        Enrollment enrollmentRecord = enrollmentRepo.findById(enrollmentId).orElse(null);
        if(enrollmentRecord == null || !enrollmentRecord.isNotDeleted())
            return 1;
        else{
            enrollmentRecord.setEnrollmentStatus(EnrollmentStatus.ENROLLED);
            enrollmentRepo.save(enrollmentRecord);
            ssgService.addStudentGrades(enrollmentRecord);
            
            Student student = enrollmentRecord.getStudent();
            student.setNew(false);
            student.setCurrentGradeSection(enrollmentRecord.getSectionToEnroll());
            studentRepo.save(student);
            
            return 2;
        }
    }
    
    public List<EnrollmentDTO> getAllEnrollment(String status){
        EnrollmentStatus enrollmentStatus = getEnrollmentStatus(status);
        return enrollmentRepo.findRecordsByStatusAndSemester(
                                                        enrollmentStatus, 
                                                        sySemRepo.findCurrentActive().getSySemNumber())
                            .stream()
                            .map(Enrollment::DTOmapper)
                            .toList();
    }
    
    private EnrollmentStatus getEnrollmentStatus(String status){
        if(status.equalsIgnoreCase("LISTING"))
            return EnrollmentStatus.LISTING;
        else if(status.equalsIgnoreCase("ASSESSMENT"))
            return EnrollmentStatus.ASSESSMENT;
        else if(status.equalsIgnoreCase("PAYMENT"))
            return EnrollmentStatus.PAYMENT;
        else
            return EnrollmentStatus.ENROLLED;
    }
}
