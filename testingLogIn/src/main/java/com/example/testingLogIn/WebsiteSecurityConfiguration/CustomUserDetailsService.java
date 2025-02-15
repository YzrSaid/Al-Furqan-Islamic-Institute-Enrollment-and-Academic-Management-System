package com.example.testingLogIn.WebsiteSecurityConfiguration;

import com.example.testingLogIn.Models.AccountRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepo userRepo;

    @Autowired
    public CustomUserDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
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
    
}

