package com.example.testingLogIn.Services;

import com.example.testingLogIn.Enums.Gender;
import com.example.testingLogIn.Enums.Role;
import com.example.testingLogIn.ModelDTO.TeacherDTO;
import com.example.testingLogIn.ModelDTO.UserDTO;
import com.example.testingLogIn.Models.Teacher;
import com.example.testingLogIn.Repositories.TeacherRepo;
import com.example.testingLogIn.WebsiteSecurityConfiguration.UserModel;
import com.example.testingLogIn.WebsiteSecurityConfiguration.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class TeacherServices{

    @Autowired
    TeacherRepo teacherRepo;
    @Autowired
    private UserRepo userRepo;
    
    public TeacherDTO getTeacher(int staffId){
        return teacherRepo.findAll().stream()
                   .filter(teacher -> teacher.isNotDeleted() && teacher.getUser().getStaffId()==staffId)
                   .map(teacher -> TeacherToTeacherDTO(teacher))
                   .findFirst().orElse(null);
    }

    public List<TeacherDTO> getTeacherList(){
        return teacherRepo.findAll().stream()
                          .filter(Teacher::isNotDeleted)
                          .map(teacher -> TeacherToTeacherDTO(teacher))
                          .collect(Collectors.toList());
    }

    public boolean updateTeacherInfo(TeacherDTO teacherDTO, int staffId){
            if(!isActive(staffId))
                return false;
                
            Teacher updateTeacher = teacherRepo.findAll().stream()
                                     .filter(t -> t.isNotDeleted() && t.getUser().getStaffId() == staffId)
                                     .findFirst().orElse(null);

            updateTeacher.setAddress(teacherDTO.getAddress());
            updateTeacher.setBirthdate(teacherDTO.getBirthdate());
            updateTeacher.setContactNum(teacherDTO.getContactNum());
            updateTeacher.setGender(getGenderByString(teacherDTO.getGender()));

            teacherRepo.save(updateTeacher);

            return true;
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
    
    public boolean addNewTeacherInfo(TeacherDTO teacherDTO){
            UserModel user= userRepo.findById(teacherDTO.getStaffId()).orElse(null);
            if(user == null)
                throw new MatchException("Wala nakita", new Throwable());
            
            if(isActive(teacherDTO.getStaffId()))
                return false;
            else{
            Teacher newTeacher = Teacher.builder()
                                    .user(user)
                                    .address(teacherDTO.getAddress())
                                    .birthdate(teacherDTO.getBirthdate())
                                    .contactNum(teacherDTO.getContactNum())
                                    .gender(getGenderByString(teacherDTO.getGender()))
                                    .isNotDeleted(true)
                                    .build();
            teacherRepo.save(newTeacher);

            return true;}
    }

    private boolean isActive(int staffid){
        try{
        return teacherRepo.findAll().stream()
                          .filter(teacher -> teacher.getUser().getStaffId() == staffid && 
                                  teacher.isNotDeleted())
                          .findFirst().get()
                          != null;
        }catch(NoSuchElementException noee){
            return false;
        }
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
                         .fullname(teacher.getUser().getFirstname()+" "+teacher.getUser().getLastname())
                         .address(teacher.getAddress())
                         .birthdate(teacher.getBirthdate())
                         .gender(teacher.getGender().toString())
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
    
    private Gender getGenderByString(String gender){
        if(Gender.FEMALE.toString().equals(gender.toUpperCase()))
            return Gender.FEMALE;
        
        return Gender.MALE;
    }
}
