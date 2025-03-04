package com.example.testingLogIn.WebsiteSecurityConfiguration;

import com.example.testingLogIn.Enums.Role;
import com.example.testingLogIn.ModelDTO.UserDTO;
import com.example.testingLogIn.Models.AccountRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;

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

    public String getLoggedInUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            return currentUserName;
        }
        return null;
    }
    
    public UserModel getCurrentlyLoggedInUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
                return (UserModel)authentication.getPrincipal();
        }
        return null;
    }

    private UserModel AccountRegToUserModel(AccountRegister accountRegister) {
        return UserModel.builder()
                .isNotRestricted(true)
                .isNotDeleted(true)
                .firstname(accountRegister.getFirstname())
                .lastname(accountRegister.getLastname())
                .role(accountRegister.getRole())
                .address(accountRegister.getAddress())
                .birthdate(accountRegister.getBirthdate())
                .gender(accountRegister.getGender())
                .username(accountRegister.getUsername())
                .password(accountRegister.getPassword())
                .build();
    } 
}
