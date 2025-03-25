package com.example.testingLogIn.Services;

import com.example.testingLogIn.ModelDTO.StudentDTO;
import com.example.testingLogIn.Models.GradeLevel;
import com.example.testingLogIn.Models.Section;
import com.example.testingLogIn.Models.Student;
import com.example.testingLogIn.PagedResponse.StudentDTOPage;
import com.example.testingLogIn.Repositories.GradeLevelRepo;
import com.example.testingLogIn.Repositories.StudentRepo;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    private final TransferReqServices transReqServices;
    private final GradeLevelRepo gradeLevelRepo;

    @Autowired
    public StudentServices(StudentRepo studentRepo, SectionServices sectionServices, EnrollmentServices enrollmentService, TransferReqServices transReqServices, GradeLevelRepo gradeLevelRepo) {
        this.studentRepo = studentRepo;
        this.sectionServices = sectionServices;
        this.enrollmentService = enrollmentService;
        this.transReqServices = transReqServices;
        this.gradeLevelRepo = gradeLevelRepo;
    }

    public boolean addStudent(StudentDTO student){
        GradeLevel gradeLevel = null;
        if(student.getLastGradeLevelId() != null)
            gradeLevel = gradeLevelRepo.findById(student.getLastGradeLevelId()).orElse(null);

        String year = LocalDate.now().getYear()+"";
        StringBuilder count = new StringBuilder(studentRepo.findStudentNextId(year) + 1 + "");
        for(int i=count.length() ; i<4 ; i++){
            count.insert(0, "0");
        }
        if(doesStudentNameExist(student))
            return false;
        else{
            Student newStudent = Student.builder()
                                    .studentDisplayId(year+"-"+count)
                                    .firstName(student.getFirstName())
                                    .lastName(student.getLastName())
                                    .middleName(student.getMiddleName())
                                    .gender(student.getGender())
                                    .birthPlace(student.getBirthPlace())
                                    .birthdate(student.getBirthdate())
                                    .cellphoneNum(student.getCellphoneNum())
                                    .street(student.getAddress().getStreet())
                                    .barangay(student.getAddress().getBarangay())
                                    .city(student.getAddress().getCity())
                                    .studentBalance(0)
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
                                    .madrasaName(student.getMadrasaName())
                                    .lastGradeLevelCompleted(gradeLevel)
                                    .lastMadrasaYearCompleted(student.getLastMadrasaYearCompleted())
                                    .madrasaAddress(student.getMadrasaAddress())
                                    .build();
            Student newSavedStudent = studentRepo.save(newStudent);
            enrollmentService.addStudentToListing(newSavedStudent.getStudentId());
            if(newSavedStudent.isTransferee())
                transReqServices.addingStudentRequirements(newSavedStudent,student.getTransfereeRequirements());
            return true;
        }
    }
    
    public List<StudentDTO> getAllStudent(){
        return studentRepo.findByIsNotDeletedTrue().stream()
                            .map(Student::DTOmapper)
                            .toList();
    }
    
    public List<StudentDTO> getNewStudents(){
        return studentRepo.findByIsNotDeletedTrueAndIsNewTrue().stream()
                            .map(Student::DTOmapper)
                            .toList();
    }
    
    public List<StudentDTO> getOldStudents(){
        return studentRepo.findByIsNotDeletedTrueAndIsNewFalse().stream()
                            .map(Student::DTOmapper)
                            .toList();
    }
    
    public List<StudentDTO> getTransfereeStudents(){
        return studentRepo.findByIsNotDeletedTrueAndIsTransfereeTrue().stream()
                            .map(Student::DTOmapper)
                            .toList();
    }
    
    public Student getStudent(int studentId){
        return studentRepo.findById(studentId).orElse(null);
    }
    
    public boolean updateStudent(StudentDTO stud){
        String sectionName = stud.getCurrentGradeSection().substring(stud.getCurrentGradeSection().indexOf("-")+1);
        GradeLevel gradeLevel = gradeLevelRepo.findById(stud.getLastGradeLevelId()).orElse(null);
        Section section = sectionServices.getSectionByName(sectionName);
        Student toUpdate = getStudent(stud.getStudentId());
        if(toUpdate == null)
            throw new NullPointerException();                    //checks if a student with not the same ID has the same name
        else if(studentRepo.existsByNameIgnoreCaseAndNotDeleted(stud.getStudentId(),stud.getFirstName(),stud.getLastName(),stud.getMiddleName()))
            return false;
        else{
            toUpdate.setFirstName(stud.getFirstName());
            toUpdate.setLastName(stud.getLastName());
            toUpdate.setMiddleName(stud.getMiddleName());
            toUpdate.setCellphoneNum(stud.getCellphoneNum());
            toUpdate.setGender(stud.getGender());
            toUpdate.setStreet(stud.getAddress().getStreet());
            toUpdate.setBarangay(stud.getAddress().getBarangay());
            toUpdate.setCity(stud.getAddress().getCity());
            toUpdate.setBirthdate(stud.getBirthdate());
            toUpdate.setBirthPlace(stud.getBirthPlace());
            toUpdate.setCurrentGradeSection(section);
            
            toUpdate.setMotherName(stud.getMotherName());
            toUpdate.setMotherOccupation(stud.getMotherOccupation());
            toUpdate.setFatherName(stud.getFatherName());
            toUpdate.setMotherOccupation(stud.getMotherOccupation());
            toUpdate.setGuardianName(stud.getGuardianName());
            toUpdate.setGuardianAddress(stud.getGuardianAddress());
            toUpdate.setGuardianContactNum(stud.getGuardianContactNum());
            
            toUpdate.setScholar(stud.isScholar());
            toUpdate.setTransferee(stud.isTransferee());
            toUpdate.setMadrasaName(stud.getMadrasaName());
            toUpdate.setMadrasaAddress(stud.getMadrasaAddress());
            toUpdate.setLastGradeLevelCompleted(gradeLevel);
            toUpdate.setLastMadrasaYearCompleted(stud.getLastGradeLevelCompleted());
            
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
                student.getLastName(),
                student.getMiddleName());
    }

    public StudentDTOPage getStudentPage(String studentType, String condition, int pageNo, int pageSize){
        Sort sort = condition.equalsIgnoreCase("asc") ?
                Sort.by(Sort.Order.asc("studentDisplayId")) :
                Sort.by(Sort.Order.desc("studentDisplayId"));

        Page<StudentDTO> studentPage = null;
        if(studentType.equalsIgnoreCase("new"))
            studentPage = studentRepo.findByIsNewTrue(PageRequest.of(pageNo-1,pageSize,sort)).map(Student::DTOmapper);
        else
            studentPage = studentRepo.findByIsNewFalse(PageRequest.of(pageNo-1,pageSize,sort)).map(Student::DTOmapper);

        return StudentDTOPage.builder()
                .content(studentPage.getContent())
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalElements(studentPage.getTotalElements())
                .totalPages(studentPage.getTotalPages())
                .isLast(studentPage.isLast())
                .build();
    }

    public StudentDTOPage getStudentByNameOrDisplayId(String word, int pageNo, int pageSize){
        Page<StudentDTO> studentPage = studentRepo.findByStudentDisplayIdOrName(word,PageRequest.of(pageNo-1,pageSize))
                                                                                .map(Student::DTOmapper);
        return StudentDTOPage.builder()
                            .content(studentPage.getContent())
                            .pageNo(pageNo)
                            .pageSize(pageSize)
                            .totalElements(studentPage.getNumberOfElements())
                            .totalPages(studentPage.getTotalPages())
                            .isLast(studentPage.isLast())
                            .build();
    }


}