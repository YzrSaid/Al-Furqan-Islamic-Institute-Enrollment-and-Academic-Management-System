package com.example.testingLogIn.Services;

import com.example.testingLogIn.ModelDTO.TeacherDTO;
import com.example.testingLogIn.ModelDTO.UserDTO;
import com.example.testingLogIn.Models.Teacher;
import com.example.testingLogIn.Repositories.TeacherRepo;
import com.example.testingLogIn.WebsiteSecurityConfiguration.CustomUserDetailsService;
import com.example.testingLogIn.WebsiteSecurityConfiguration.UserModel;
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
    private CustomUserDetailsService userService;

    public boolean addTeacher(Teacher teacher){
        try{
            teacherRepo.save(teacher);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    public List<Teacher> getTeacherList(){
        return teacherRepo.findAll().stream()
                          .filter(Teacher::isNotDeleted)
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
    
    public boolean addNewTeacherInfo(TeacherDTO teacherDTO, int staffid){
        try{
            Teacher newTeacher = Teacher.builder()
                                    .user(userService.getuser(staffid))
                                    .address(teacherDTO.getAddress())
                                    .birthdate(teacherDTO.getBirthdate())
                                    .contactNum(teacherDTO.getContactNum())
                                    .gender(teacherDTO.getGender())
                                    .isNotDeleted(true)
                                    .build();
            teacherRepo.save(newTeacher);

            return true;}
        catch(Exception ex){
            return false;
        }
    }

    public List<UserDTO> unverifiedTeacherAccounts(){
        List<Integer> registeredTeacherIds = new ArrayList<>();
        teacherRepo.findAll().forEach(current ->{
            if(current.isNotDeleted())
                registeredTeacherIds.add(current.getUser().getStaffId());
        });

        return userService.getTeachersAccount()
               .stream()
               .filter(user -> doesNotContain(registeredTeacherIds, user.getStaffId()))
               .collect(Collectors.toList());
    }

    private boolean doesNotContain(List<Integer> idList, int id){
        return !idList.contains(id);
    }
}
