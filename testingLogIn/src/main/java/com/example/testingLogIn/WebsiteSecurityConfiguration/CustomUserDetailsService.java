package com.example.testingLogIn.WebsiteSecurityConfiguration;

import com.example.testingLogIn.AssociativeModels.StudentPassword;
import com.example.testingLogIn.CustomObjects.StudentAccount;
import com.example.testingLogIn.Enums.Role;
import com.example.testingLogIn.ModelDTO.StudentDTO;
import com.example.testingLogIn.ModelDTO.UserDTO;
import com.example.testingLogIn.Models.AccountRegister;
import com.example.testingLogIn.Models.Student;
import com.example.testingLogIn.CustomObjects.PagedResponse;
import com.example.testingLogIn.Repositories.StudentRepo;
import com.example.testingLogIn.Services.NonModelServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private StudentPasswordRepo studentPasswordRepo;
    @Autowired
    private StudentRepo studentRepo;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(11);

    public boolean restrictUser(int id) {
        try {
            UserModel user = getuser(id);
            if (user == null)
                return false;
            else {
                user.setNotRestricted(false);
                userRepo.save(user);
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public boolean unrestrictUser(int id) {
        try {
            UserModel user = getuser(id);
            if (user == null)
                return false;
            else {
                user.setNotRestricted(true);
                userRepo.save(user);
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public PagedResponse getStudentAccounts(int pageNo,int pageSize, String search){
        search = NonModelServices.forLikeOperator(search);
        Page<StudentAccount> studentAccounts = userRepo.findStudentAccounts(search, PageRequest.of(pageNo-1,pageSize));
        return  PagedResponse.builder()
                .content(studentAccounts.getContent())
                .pageNo(studentAccounts.getNumber())
                .pageSize(studentAccounts.getSize())
                .totalElements(studentAccounts.getTotalElements())
                .totalPages(studentAccounts.getTotalPages())
                .isLast(studentAccounts.isLast())
                .build();
    }

    public List<UserDTO> getAllUsers() {
        return userRepo.findAll().stream()
                .map(UserModel::mapperDTO)
                .toList();
    }

    public UserModel getuser(int staffId) {
        return userRepo.findById(staffId).orElse(null);
    }

    public List<UserDTO> getTeachersAccount(){
        return userRepo.findAll().stream()
                          .filter(user -> user.isNotDeleted() && user.getRole().equals(Role.TEACHER))
                          .map(UserModel::mapperDTO)
                          .toList();
    }

    public boolean registerNewUser(AccountRegister accountRegister) {
        userRepo.save(AccountRegToUserModel(accountRegister));
        return true;
    }

    public void registerStudent(Student student){
        CompletableFuture.runAsync(()->{
            String userName = (student.getStudentDisplayId().replace("-",""))+"@alfurqan";
            String password = NonModelServices.generate(8);
            studentPasswordRepo.save(new StudentPassword(student,password));
            userRepo.save(UserModel.builder()
                    .username(userName)
                    .firstname(student.getFirstName())
                    .fullName(student.getFullName())
                    .password(encoder.encode(password))
                    .isNotDeleted(true)
                    .isNotRestricted(true)
                    .role(Role.STUDENT)
                    .student(student)
                    .build());
        });
    }

    public boolean usernameExist(String username) {
        return userRepo.findByUsername(username) != null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserModel user = userRepo.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        } else if (!user.isNotRestricted()) {
            throw new AccountStatusException("User is Currently Restricted") {
            };
        }
        return user;
    }

    public boolean isUsernameValid(String username) {
        return userRepo.findByUsername(username) != null;
    }

    public UserModel getCurrentlyLoggedInUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
                return (UserModel)authentication.getPrincipal();
        }
        return null;
    }

    public void changeUserPassword(UserModel user, String newPassword){
        user.setPassword(encoder.encode(newPassword));
        userRepo.save(user);
    }

    public StudentDTO getCurrentlyLoggedInStudent(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            int id = ((UserModel)authentication.getPrincipal()).getStudent().getStudentId();
            return studentRepo.findById(id).map(Student::DTOmapper).orElseThrow(NullPointerException::new);
        }
        return null;
    }
    
    public UserDTO getCurrentlyLoggedInUserDTO(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
                return ((UserModel)authentication.getPrincipal()).mapperDTO();
        }
        return null;
    }

    private UserModel AccountRegToUserModel(AccountRegister accountRegister) {
        String fullName = accountRegister.getFirstname()+" "+ Optional.ofNullable(accountRegister.getMiddlename()).map(s-> s+" ").orElse("") + accountRegister.getLastname();
        return UserModel.builder()
                .isNotRestricted(true)
                .isNotDeleted(true)
                .firstname(accountRegister.getFirstname())
                .lastname(accountRegister.getLastname())
                .middlename(accountRegister.getMiddlename())
                .fullName(fullName)
                .role(accountRegister.getRole())
                .address(accountRegister.getAddress())
                .birthdate(accountRegister.getBirthdate())
                .gender(accountRegister.getGender())
                .username(accountRegister.getUsername())
                .password(encoder.encode(accountRegister.getPassword()))
                .build();
    }
}
