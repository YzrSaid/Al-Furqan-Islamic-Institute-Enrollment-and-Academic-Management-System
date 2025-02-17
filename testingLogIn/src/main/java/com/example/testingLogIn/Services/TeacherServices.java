package com.example.testingLogIn.Services;

import com.example.testingLogIn.Enums.Role;
import com.example.testingLogIn.ModelDTO.TeacherDTO;
import com.example.testingLogIn.ModelDTO.UserDTO;
import com.example.testingLogIn.Models.Teacher;
import com.example.testingLogIn.Repositories.TeacherRepo;
import com.example.testingLogIn.WebsiteSecurityConfiguration.CustomUserDetailsService;
import com.example.testingLogIn.WebsiteSecurityConfiguration.UserModel;
import com.example.testingLogIn.WebsiteSecurityConfiguration.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeacherServices{

    @Autowired
    TeacherRepo teacherRepo;
    @Autowired
    private UserRepo userRepo;

    public List<TeacherDTO> getTeacherList(){
        return teacherRepo.findAll().stream()
                          .filter(Teacher::isNotDeleted)
                          .map(teacher -> TeacherToTeacherDTO(teacher))
                          .collect(Collectors.toList());
    }

    public boolean updateTeacherInfo(TeacherDTO teacherDTO, int staffid){
        try{
            Teacher updateTeacher = teacherRepo.findById(staffid).orElse(null);

            if(updateTeacher == null)
                return false;

            updateTeacher.setAddress(teacherDTO.getAddress());
            updateTeacher.setBirthdate(teacherDTO.getBirthdate());
            updateTeacher.setContactNum(teacherDTO.getContactNum());
            updateTeacher.setGender(teacherDTO.getGender());

            teacherRepo.save(updateTeacher);

            return true;}
        catch(Exception ex){
            return false;
        }
    }
    
    public boolean deleteTeacherRecord(int staffId){
        Teacher teacher = teacherRepo.findAll().stream()
                                     .filter(t -> t.isNotDeleted() && t.getUser().getStaffId() == staffId)
                                     .findFirst().orElse(null);
        if(teacher == null)
            return false;
        else{
            teacher.setNotDeleted(false);
            teacherRepo.save(teacher);
            return true;}
    }
    
    public boolean addNewTeacherInfo(TeacherDTO teacherDTO, int staffid){
            UserModel user= userRepo.findById(staffid).orElse(null);
            if(user == null)
                return false;
            else{
            Teacher newTeacher = Teacher.builder()
                                    .user(user)
                                    .address(teacherDTO.getAddress())
                                    .birthdate(teacherDTO.getBirthdate())
                                    .contactNum(teacherDTO.getContactNum())
                                    .gender(teacherDTO.getGender())
                                    .isNotDeleted(true)
                                    .build();
            teacherRepo.save(newTeacher);

            return true;}
    }

    public List<UserDTO> notRegisteredTeacherAccounts(){
        List<Integer> registeredTeacherIds = new ArrayList<>();
        teacherRepo.findAll().forEach(current ->{
            if(current.isNotDeleted())
                registeredTeacherIds.add(current.getUser().getStaffId());
        });

        return userRepo.findAll()
               .stream()
               .filter(user -> user.getRole() == Role.TEACHER && !registeredTeacherIds.contains(user.getStaffId()))
               .map(user -> UserModelToUserDTO(user))
                .collect(Collectors.toList());
    }
    
    private TeacherDTO TeacherToTeacherDTO(Teacher teacher){
        return TeacherDTO.builder()
                         .staffId(teacher.getUser().getStaffId())
                         .address(teacher.getAddress())
                         .birthdate(teacher.getBirthdate())
                         .gender(teacher.getGender())
                         .contactNum(teacher.getContactNum())
                         .build();
    }
    
    private UserDTO UserModelToUserDTO(UserModel user){
        return UserDTO.builder()
               .firstname(user.getFirstname())
               .lastname(user.getLastname())
               .staffId(user.getStaffId())
               .role(user.getRole())
               .build();
    }  
}
