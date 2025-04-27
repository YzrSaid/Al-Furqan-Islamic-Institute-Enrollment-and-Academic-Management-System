package com.example.testingLogIn.Services;

import com.example.testingLogIn.CustomObjects.PagedResponse;
import com.example.testingLogIn.Enums.StudentStatus;
import com.example.testingLogIn.ModelDTO.StudentDTO;
import com.example.testingLogIn.Models.GradeLevel;
import com.example.testingLogIn.Models.Student;
import com.example.testingLogIn.Repositories.GradeLevelRepo;
import com.example.testingLogIn.Repositories.StudentRepo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.example.testingLogIn.WebsiteSecurityConfiguration.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final DiscountsServices discountsServices;
    private final CustomUserDetailsService userService;

    @Autowired
    public StudentServices(StudentRepo studentRepo, SectionServices sectionServices, EnrollmentServices enrollmentService, TransferReqServices transReqServices, GradeLevelRepo gradeLevelRepo, DiscountsServices discountsServices, CustomUserDetailsService userService) {
        this.studentRepo = studentRepo;
        this.sectionServices = sectionServices;
        this.enrollmentService = enrollmentService;
        this.transReqServices = transReqServices;
        this.gradeLevelRepo = gradeLevelRepo;
        this.discountsServices = discountsServices;
        this.userService = userService;
    }

    public StudentDTO getStudentBtName(String studentName){
        studentName = NonModelServices.forLikeOperator(studentName);
        return studentRepo.findByName(studentName).map(Student::DTOmapper).orElseThrow(NullPointerException::new);
    }

    @CacheEvict(value = "enrollmentPage",allEntries = true)
    public boolean addStudent(StudentDTO student){
        String fullName = student.getFirstName()+" "+ Optional.ofNullable(student.getMiddleName()).map(mn -> mn+" ").orElse(" ")+student.getLastName();
        GradeLevel gradeLevel = null;
        if(student.getLastGradeLevelId() != null)
            gradeLevel = gradeLevelRepo.findById(student.getLastGradeLevelId()).orElse(null);

        String year = LocalDate.now().getYear()+"";
        StringBuilder count = new StringBuilder(studentRepo.findStudentNextId(year) + 1 + "");
        for(int i=count.length() ; i<4 ; i++){
            count.insert(0, "0");
        }
        if(studentRepo.existsByNameIgnoreCaseAndNotDeleted(null,fullName))
            return false;
        else{
            Student newStudent = Student.builder()
                                    .studentDisplayId(year+"-"+count)
                                    .firstName(student.getFirstName())
                                    .lastName(student.getLastName())
                                    .middleName(student.getMiddleName())
                                    .fullName(student.getFirstName()+" "+ Optional.ofNullable(student.getMiddleName()).map(mn -> mn+" ").orElse(" ")+student.getLastName())
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
                                    .guardianOccupation(student.getGuardianOccupation())

                                    .status(StudentStatus.NEW)
                                    .isNew(true)
                                    .isNotDeleted(true)
                                    .isScholar(!student.getDiscountsAvailed().isEmpty())
                                    .isTransferee(student.isTransferee())
                                    .madrasaName(student.getMadrasaName())
                                    .lastGradeLevelCompleted(gradeLevel)
                                    .lastMadrasaYearCompleted(student.getLastMadrasaYearCompleted())
                                    .madrasaAddress(student.getMadrasaAddress())
                                    .build();
            Student newSavedStudent = studentRepo.save(newStudent);
            enrollmentService.addStudentToListing(null,newSavedStudent);
            if(!student.getDiscountsAvailed().isEmpty())
                CompletableFuture.runAsync(() -> discountsServices.addStudentDiscounts(newSavedStudent.getStudentId(),student.getDiscountsAvailed()));
            if(newSavedStudent.isTransferee())
                CompletableFuture.runAsync(() ->transReqServices.addingStudentRequirements(newSavedStudent.getStudentId(),student.getTransfereeRequirements()));
            return true;
        }
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
    
    public StudentDTO getStudent(int studentId){
        return studentRepo.findById(studentId).map(Student::DTOmapper).orElseThrow(NullPointerException::new);
    }
    @CacheEvict(value = "enrollmentPage",allEntries = true)
    public String updateStudent(int studentId, StudentDTO stud){
        String fullName = stud.getFirstName()+" "+ Optional.ofNullable(stud.getMiddleName()).map(mn -> mn+" ").orElse(" ")+stud.getLastName();
        Student toUpdate = studentRepo.findById(studentId).orElseThrow(()->new NullPointerException("Student record not found"));
        if(studentRepo.existsByNameIgnoreCaseAndNotDeleted(studentId,fullName.toLowerCase()))//checks if a student with not the same ID has the same name
            throw new IllegalArgumentException("Student Full Name Already Exists");

        toUpdate.setFirstName(stud.getFirstName());
        toUpdate.setLastName(stud.getLastName());
        toUpdate.setMiddleName(stud.getMiddleName());
        toUpdate.setFullName(fullName);
        toUpdate.setCellphoneNum(stud.getCellphoneNum());
        toUpdate.setGender(stud.getGender());
        toUpdate.setStreet(stud.getAddress().getStreet());
        toUpdate.setBarangay(stud.getAddress().getBarangay());
        toUpdate.setCity(stud.getAddress().getCity());
        toUpdate.setBirthdate(stud.getBirthdate());
        toUpdate.setBirthPlace(stud.getBirthPlace());

        toUpdate.setMotherName(stud.getMotherName());
        toUpdate.setMotherOccupation(stud.getMotherOccupation());
        toUpdate.setFatherName(stud.getFatherName());
        toUpdate.setMotherOccupation(stud.getMotherOccupation());
        toUpdate.setGuardianName(stud.getGuardianName());
        toUpdate.setGuardianAddress(stud.getGuardianAddress());
        toUpdate.setGuardianContactNum(stud.getGuardianContactNum());
        toUpdate.setGuardianOccupation(stud.getGuardianOccupation());

        toUpdate.setScholar(stud.isScholar());
        toUpdate.setTransferee(stud.isTransferee());
        toUpdate.setMadrasaName(stud.getMadrasaName());
        toUpdate.setMadrasaAddress(stud.getMadrasaAddress());
        toUpdate.setLastGradeLevelCompleted(gradeLevelRepo.findById(stud.getLastGradeLevelId()).orElse(null));
        toUpdate.setLastMadrasaYearCompleted(stud.getLastGradeLevelCompleted());
        toUpdate.setLastMadrasaYearCompleted(stud.getLastMadrasaYearCompleted());
        studentRepo.save(toUpdate);
        userService.updateStudentAccount(toUpdate.DTOmapper());

        return fullName;
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

    public PagedResponse getStudentByNameOrDisplayId(String word,String sortBy, int pageNo, int pageSize, Boolean isFullyPaid){
        word = NonModelServices.forLikeOperator(word);
        Pageable pageable = PageRequest.of(pageNo-1,pageSize,orderBy(sortBy));
        Page<StudentDTO> studentPage;

        if(sortBy.equalsIgnoreCase("gradelevel"))
            studentPage = studentRepo.findByStudentHandlerDisplayIdOrName(word,pageable,isFullyPaid)
                    .map(Student::DTOmapper);
        else
            studentPage = studentRepo.findByStudentDisplayIdOrName(word,pageable,isFullyPaid)
                    .map(Student::DTOmapper);

        return PagedResponse.builder()
                            .content(studentPage.getContent())
                            .pageNo(pageNo)
                            .pageSize(pageSize)
                            .totalElements(studentPage.getTotalElements())
                            .totalPages(studentPage.getTotalPages())
                            .isLast(studentPage.isLast())
                            .build();
    }

    private Sort orderBy(String condition){
        if(condition.equalsIgnoreCase("balance"))
            return Sort.by(Sort.Order.desc("s.studentBalance"));
        else if(condition.equalsIgnoreCase("gradelevel"))
            return Sort.by(Sort.Order.asc("s.currentGradeSection.level.levelName"));
        else if(condition.equalsIgnoreCase("studentname"))
            return Sort.by(Sort.Order.asc("s.fullName"));
        else
            return Sort.by(Sort.Order.asc("s.status"));
    }

}