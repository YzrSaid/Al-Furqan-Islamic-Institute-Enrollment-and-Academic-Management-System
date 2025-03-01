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
        String sectionName = student.getCurrentSection().substring(student.getCurrentSection().indexOf("-")+1);
        Section section = sectionServices.getSectionByName(sectionName);
        if(doesStudentNameExist(student))
            return false;
        else if(!student.getCurrentSection().equalsIgnoreCase("none") && section == null)
            throw new NullPointerException();
        else{
            Student newStudent = Student.builder()
                                    .firstName(student.getFirstName())
                                    .lastName(student.getLastName())
                                    .gender(student.getGender())
                                    .birthdate(student.getBirthdate())
                                    .cellphoneNum(student.getContactNum())
                                    .address(student.getAddress())
                                    .currentGradeSection(section)
                                    .contactPerson(student.getContactPersonName())
                                    .contactPersonAddress(student.getContactPersonName())
                                    .contactPersonNumber(student.getContactPersonCellphone())
                                    .isNew(true)
                                    .isNotDeleted(true)
                                    .isScholar(student.isScholar())
                                    .isTransferee(student.isTransferee())
                                    .build();
            studentRepo.save(newStudent);
            
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
        String sectionName = stud.getCurrentSection().substring(stud.getCurrentSection().indexOf("-")+1);
        Section section = sectionServices.getSectionByName(sectionName);
        Student toUpdate = getStudent(stud.getStudentId());
        if(toUpdate == null)
            throw new NullPointerException();                    //checks if a student with not the same ID has the same name
        else if(studentRepo.existsByNameIgnoreCaseAndNotDeleted(stud.getStudentId(),stud.getFirstName(),stud.getLastName()))
            return false;
        else{
            toUpdate.setFirstName(stud.getFirstName());
            toUpdate.setLastName(stud.getLastName());
            toUpdate.setCellphoneNum(stud.getContactNum());
            toUpdate.setGender(stud.getGender());
            toUpdate.setBirthdate(stud.getBirthdate());
            toUpdate.setCurrentGradeSection(section);
            
            toUpdate.setContactPerson(stud.getContactPersonName());
            toUpdate.setContactPersonAddress(stud.getContactPersonAddress());
            toUpdate.setContactPersonNumber(stud.getContactPersonCellphone());
            
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
