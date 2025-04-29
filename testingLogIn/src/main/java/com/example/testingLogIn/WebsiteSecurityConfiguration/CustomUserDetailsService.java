package com.example.testingLogIn.WebsiteSecurityConfiguration;

import com.example.testingLogIn.AssociativeModels.StudentPassword;
import com.example.testingLogIn.CustomObjects.StudentAccount;
import com.example.testingLogIn.Enums.RegistrationStatus;
import com.example.testingLogIn.Enums.Role;
import com.example.testingLogIn.ModelDTO.StudentDTO;
import com.example.testingLogIn.ModelDTO.UserDTO;
import com.example.testingLogIn.Models.AccountRegister;
import com.example.testingLogIn.Models.Student;
import com.example.testingLogIn.CustomObjects.PagedResponse;
import com.example.testingLogIn.PasswordResetPackage.AccountConfirmTokenRepo;
import com.example.testingLogIn.PasswordResetPackage.AccountConfirmationToken;
import com.example.testingLogIn.PasswordResetPackage.EmailService;
import com.example.testingLogIn.Repositories.AccountRegisterRepo;
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

import java.security.InvalidKeyException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;
    private final StudentPasswordRepo studentPasswordRepo;
    private final StudentRepo studentRepo;
    private final AccountConfirmTokenRepo confirmTokenRepo;
    private final EmailService emailService;
    private final AccountRegisterRepo accountRegisterRepo;

    @Autowired
    public CustomUserDetailsService(UserRepo userRepo, StudentPasswordRepo studentPasswordRepo, StudentRepo studentRepo, AccountConfirmTokenRepo confirmTokenRepo, EmailService emailService, AccountRegisterRepo accountRegisterRepo) {
        this.userRepo = userRepo;
        this.studentPasswordRepo = studentPasswordRepo;
        this.studentRepo = studentRepo;
        this.confirmTokenRepo = confirmTokenRepo;
        this.emailService = emailService;
        this.accountRegisterRepo = accountRegisterRepo;
    }

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

    public PagedResponse getAllUsers(String role, String name, int pageNo, int pageSize, boolean isNotRestricted) {
        Role userRole = role.equalsIgnoreCase("all") ? null :
                        role.equalsIgnoreCase("STUDENT") ? Role.STUDENT :
                        role.equalsIgnoreCase("Teacher") ? Role.TEACHER :
                         Role.ENROLLMENT_STAFF;
        Page<UserDTO> userPage = userRepo.findUsersByPageRole(userRole,NonModelServices.forLikeOperator(name),isNotRestricted,PageRequest.of(pageNo-1,pageSize))
                .map(UserModel::mapperDTO);
        return PagedResponse.builder()
                .content(userPage.getContent())
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .build();
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

    public void registerUser(AccountRegister newAccount){
        userRepo.save(AccountRegToUserModel(newAccount));
    }

//    public void registerNewUser(String token, String password) {
//        AccountConfirmationToken tokenAct = confirmTokenRepo.findByToken(token)
//                .orElseThrow(()->new NullPointerException("Invalid Token"));
//
//        AccountRegister account = tokenAct.getAccountRegister();
//        if(usernameExist(account.getUsername())) {
//            throw new IllegalArgumentException("Email Address is Already Used by an Existing User");
//        }else if(account.getStatus() == RegistrationStatus.REJECTED){
//            throw new IllegalArgumentException("Account is already rejected");
//        }else if(tokenAct.getExpiryDate().isBefore(LocalDateTime.now()))
//            throw new IllegalArgumentException("Account Confirmation Transaction is Expired. Contact the renew the account confirmation process");
//
//        userRepo.save(AccountRegToUserModel(tokenAct.getAccountRegister(), password));
//
//        CompletableFuture.runAsync(()-> {
//            emailService.registrationComplete(tokenAct.getAccountRegister().getUsername());
//            account.setStatus(RegistrationStatus.APPROVED);
//            accountRegisterRepo.save(account);
//            confirmTokenRepo.deleteUserTokens(account.getUsername());
//        });
//    }

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

    public void updateStudentAccount(StudentDTO student){
        UserModel studentUser = userRepo.findStudentAccount(student.getStudentId()).orElse(null);
        if(studentUser != null){
            studentUser.setFirstname(student.getFirstName());
            studentUser.setMiddlename(student.getMiddleName());
            studentUser.setLastname(student.getLastName());
            studentUser.setFullName(student.getFullName());
            userRepo.save(studentUser);
        }
    }

    public void updateMyAccountStaff(UserDTO updated){
        UserModel user = getCurrentlyLoggedInUser();
        if(user == null)
            throw new NullPointerException("Account not found");

        user.setFullName(updated.getFirstname()+" "+Optional.ofNullable(updated.getMiddlename()).map(s -> s+" ").orElse("")+updated.getLastname());
        user.setFirstname(updated.getFirstname());
        user.setLastname((updated.getLastname()));
        user.setMiddlename(updated.getMiddlename());
        user.setGender(updated.getGender());
        user.setBirthdate(updated.getBirthdate());
        user.setAddress(updated.getAddress());

        userRepo.save(user);
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

    public void authenticateAdminPassword(String pw) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            if(encoder.matches(pw,((UserModel)authentication.getPrincipal()).getPassword())){
                return;
            }
        }
        try {
            throw new InvalidKeyException("Invalid Password");
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public void changeUserPassword(UserModel user, String newPassword){
        user.setPassword(encoder.encode(newPassword));
        userRepo.save(user);
    }

    public StudentDTO getCurrentlyLoggedInStudent(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            int id = ((UserModel)authentication.getPrincipal()).getStudent().getStudentId();
            return studentRepo.findById(id).map(Student::DTOmapper).orElseThrow(()->new NullPointerException("Account Not Found"));
        }

        throw new NullPointerException("Account Not Found");
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
                .staffDisplayId(staffAccountgenerator(accountRegister.getRole()))
                .address(accountRegister.getAddress())
                .birthdate(accountRegister.getBirthdate())
                .gender(accountRegister.getGender())
                .username(accountRegister.getUsername())
                .password(accountRegister.getPassword())
                .build();
    }

    private String staffAccountgenerator(Role role){
        String yearRegistered = LocalDate.now().getYear()+"";
        String identifier = role.equals(Role.ENROLLMENT_STAFF) ? "ENR" :
                            "TEA";
        int roleCount = userRepo.countStaffByRoles(role).orElse(0) + 1;
        String filler = roleCount < 10 ? "00"+roleCount : roleCount < 100 ? "0"+roleCount : roleCount+"";
        return yearRegistered + identifier + roleCount;
    }
}
