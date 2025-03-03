package com.example.testingLogIn.Services;

import com.example.testingLogIn.ModelDTO.StudentDTO;
import com.example.testingLogIn.Models.GradeLevel;
import com.example.testingLogIn.Models.Section;
import com.example.testingLogIn.Models.Student;
import com.example.testingLogIn.Repositories.StudentRepo;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author magno
 */
@Service
public class StudentServices {
    
    private final StudentRepo studentRepo;
    private final SectionServices sectionServices;
    private final EnrollmentServices enrollmentService;

    @Autowired
    public StudentServices(StudentRepo studentRepo, SectionServices sectionServices,EnrollmentServices enrollmentService) {
        this.studentRepo = studentRepo;
        this.sectionServices = sectionServices;
        this.enrollmentService = enrollmentService;
    }
    
    
    
    public boolean addStudent(StudentDTO student){
        if(doesStudentNameExist(student))
            return false;
        else{
            Student newStudent = Student.builder()
                                    .firstName(student.getFirstName())
                                    .lastName(student.getLastName())
                                    .middleName(student.getMiddleName())
                                    .gender(student.getGender())
                                    .birthdate(student.getBirthdate())
                                    .cellphoneNum(student.getCellphoneNum())
                                    .address(student.getAddress())
                                    .currentGradeSection(null)
                                    .motherName(student.getMotherName())
                                    .motherOccupation(student.getMotherOccupation())
                                    .fatherName(student.getFatherName())
                                    .fatherOccupation(student.getFatherOccupation())
                                    .guardianName(student.getGuardianName())
                                    .guardianAddress(student.getGuardianAddress())
                                    .guardianContactNum(student.getGuardianContactNum())
                                    .isNew(true)
                                    .isNotDeleted(true)
                                    .isScholar(student.isScholar())
                                    .isTransferee(student.isTransferee())
                                    .build();
            studentRepo.save(newStudent);
            System.out.println("Added successfully");
            enrollmentService.addStudentToListing(student);
            return true;
        }
    }
    
    public List<StudentDTO> getAllStudent(){
        return studentRepo.findRegisteredStudents().stream()
                            .map(Student::DTOmapper)
                            .toList();
    }
    
    public List<StudentDTO> getNewStudents(){
        return studentRepo.findNewStudents().stream()
                            .map(Student::DTOmapper)
                            .toList();
    }
    
    public List<StudentDTO> getOldStudents(){
        return studentRepo.findOldStudents().stream()
                            .map(Student::DTOmapper)
                            .toList();
    }
    
    public List<StudentDTO> getTransfereeStudents(){
        return studentRepo.findTransfereeStudents().stream()
                            .map(Student::DTOmapper)
                            .toList();
    }
    
    public Student getStudent(int studentId){
        return studentRepo.findById(studentId).orElse(null);
    }
    
    public boolean updateStudent(StudentDTO stud){
        String sectionName = stud.getCurrentGradeSection().substring(stud.getCurrentGradeSection().indexOf("-")+1);
        Section section = sectionServices.getSectionByName(sectionName);
        Student toUpdate = getStudent(stud.getStudentId());
        if(toUpdate == null)
            throw new NullPointerException();                    //checks if a student with not the same ID has the same name
        else if(studentRepo.existsByNameIgnoreCaseAndNotDeleted(stud.getStudentId(),stud.getFirstName(),stud.getLastName()))
            return false;
        else{
            toUpdate.setFirstName(stud.getFirstName());
            toUpdate.setLastName(stud.getLastName());
            toUpdate.setCellphoneNum(stud.getCellphoneNum());
            toUpdate.setGender(stud.getGender());
            toUpdate.setBirthdate(stud.getBirthdate());
            toUpdate.setCurrentGradeSection(section);
            
            toUpdate.setGuardianName(stud.getGuardianName());
            toUpdate.setGuardianAddress(stud.getGuardianAddress());
            toUpdate.setGuardianContactNum(stud.getGuardianContactNum());
            
            toUpdate.setScholar(stud.isScholar());
            toUpdate.setTransferee(stud.isTransferee());
            
            studentRepo.save(toUpdate);
            return true;
        }
    }
            
    public boolean deleteStudent(int studentId){
        Student stud = studentRepo.findById(studentId).orElse(null);
        if(stud != null){
            stud.setNotDeleted(false);
            studentRepo.save(stud);
            return true;
        }
        return false;
    }
    
    private boolean doesStudentNameExist(StudentDTO student){
        return studentRepo.existsByNameIgnoreCaseAndNotDeleted(
                null,
                student.getFirstName(),
                student.getLastName()
    );
    }
    
}