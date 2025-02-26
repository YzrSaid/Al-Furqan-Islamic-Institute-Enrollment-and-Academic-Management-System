package com.example.testingLogIn.Services;

import com.example.testingLogIn.Models.Student;
import com.example.testingLogIn.Repositories.StudentRepo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author magno
 */
@Service
public class StudentServices {
    
    @Autowired
    private StudentRepo studentRepo;
    
    public Map<String,List<Student>> addStudent(List<Student> students){
        Map<String,List<Student>> rejectedForms = new HashMap();
        List<Student> studentList = new ArrayList<>();
        
        for(Student student : students){
            if(!doesStudentNameExist(student)){
                student.setNotDeleted(true);
                student.setNew(true);
                studentRepo.save(student);}
            else{
                if(rejectedForms.containsKey("Student Full Name Already Exist"))
                    rejectedForms.get("Student Full Name Already Exist").add(student);
                else{
                    studentList.add(student);
                    rejectedForms.put("Student Full Name Already Exist", studentList);}
            }
        }
        return rejectedForms;
    }
    
    public List<Student> getAllStudent(){
        return studentRepo.findRegisteredStudents();
    }
    
    public List<Student> getNewStudents(){
        return studentRepo.findNewStudents();
    }
    
    public List<Student> getOldStudents(){
        return studentRepo.findOldStudents();
    }
    
    public Student getStudent(int studentId){
        return studentRepo.findById(studentId).orElse(null);
    }
    
    public int updateStudent(Student stud){
        Student toUpdate = getStudent(stud.getStudentId());
        if(toUpdate == null)
            return 1;                       //checks if a student with not the same ID has the same name
        else if(studentRepo.existsByNameIgnoreCaseAndNotDeleted(stud.getStudentId(),stud.getFirstName(),stud.getLastName(),stud.getMiddleName()))
            return 2;
        else{
            toUpdate.setFirstName(stud.getFirstName());
            toUpdate.setMiddleName(stud.getMiddleName());
            toUpdate.setLastName(stud.getMiddleName());
            toUpdate.setContactNum(stud.getContactNum());
            toUpdate.setMotherName(stud.getMotherName());
            toUpdate.setFatherName(stud.getFatherName());
            toUpdate.setGender(stud.getGender());
            toUpdate.setBirthdate(stud.getBirthdate());
            toUpdate.setScholar(stud.isScholar());
            toUpdate.setTransferee(stud.isTransferee());
            
            studentRepo.save(toUpdate);
            return 0;
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
    
    private boolean doesStudentNameExist(Student student){
        return studentRepo.existsByNameIgnoreCaseAndNotDeleted(
                null,
                student.getFirstName(),
                student.getLastName(),
                student.getMiddleName()
    );
    }
    
}
