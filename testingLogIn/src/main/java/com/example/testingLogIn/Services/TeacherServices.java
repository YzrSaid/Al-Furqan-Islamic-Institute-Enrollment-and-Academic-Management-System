package com.example.testingLogIn.Services;

import com.example.testingLogIn.Models.Teacher;
import com.example.testingLogIn.Repositories.TeacherRepo;
import com.example.testingLogIn.WebsiteSecurityConfiguration.CustomUserDetailsService;
import com.example.testingLogIn.WebsiteSecurityConfiguration.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class TeacherServices{

    @Autowired
    TeacherRepo teacherRepo;
    @Autowired
    private CustomUserDetailsService userService;

    public boolean addTeacher(Teacher teacher){
        teacherRepo.save(teacher);
        return true;
    }

    public List<Teacher> getTeacherList(){
        return teacherRepo.findAll();
    }

    public List<UserModel> unverifiedTeacherAccounts(){
        List<Integer> registeredTeacherIds = new ArrayList<>();

        teacherRepo.findAll().forEach(current ->{
            registeredTeacherIds.add(current.getUser().getStaffId());
        });

        System.out.println(registeredTeacherIds.toString());

        return userService.getTeachersAccount()
               .stream()
               .filter(user -> doesNotContain(registeredTeacherIds, user.getStaffId()))
               .collect(Collectors.toList());
    }

    private boolean doesNotContain(List<Integer> idList, int id){
        return !idList.contains(id);
    }
}
