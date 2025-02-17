package com.example.testingLogIn.WebsiteSecurityConfiguration;

import com.example.testingLogIn.Enums.Role;
import com.example.testingLogIn.ModelDTO.UserDTO;
import com.example.testingLogIn.Models.AccountRegister;
import com.example.testingLogIn.Models.Teacher;
import com.example.testingLogIn.Repositories.TeacherRepo;
import com.example.testingLogIn.Services.TeacherServices;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.authentication.AccountStatusException;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private UserRepo userRepo;
    private TeacherRepo teacherRepo;
    
    @Autowired
    public CustomUserDetailsService(UserRepo userRepo,TeacherRepo teacherRepo) {
        this.userRepo = userRepo;
        this.teacherRepo = teacherRepo;
    }

    public UserModel getuser(int staffId){
        return userRepo.findById(staffId).orElse(null);
    }

    public List<UserDTO> getTeachersAccount(){
        List<Teacher> teachersList = teacherRepo.findAll().stream()
                          .filter(Teacher::isNotDeleted)
                          .collect(Collectors.toList());
        
        ArrayList<Integer> registeredTeacherIDs = new ArrayList<>();
        teachersList.forEach(current -> registeredTeacherIDs.add(current.getUser().getStaffId()));
        
        return userRepo.findAll()
                .stream()
                .filter(userModel -> userModel.role == Role.TEACHER && 
                        !registeredTeacherIDs.contains(userModel.getStaffId()))
                .map(user -> UserModelToUserDTO(user))
                .collect(Collectors.toList());
    }
    
    public boolean registerNewUser(AccountRegister accountRegister){
        userRepo.save(AccountRegToUserModel(accountRegister));
        return true;
    }

    public boolean usernameExist(String username){
        return userRepo.findByUsername(username) != null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserModel user = userRepo.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }else if(!user.isNotRestricted){
            throw new AccountStatusException("User is Currently Restricted") {};
        }
        return user;
    }

    public boolean isUsernameValid(String username) {
        return userRepo.findByUsername(username) != null;
    }

    
    private UserModel AccountRegToUserModel(AccountRegister accountRegister){
        return UserModel.builder()
                .firstname(accountRegister.getFirstname())
                .lastname(accountRegister.getLastname())
                .role(accountRegister.getRole())
                .username(accountRegister.getUsername())
                .password(accountRegister.getPassword())
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

