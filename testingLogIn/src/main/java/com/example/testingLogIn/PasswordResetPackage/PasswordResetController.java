package com.example.testingLogIn.PasswordResetPackage;

import com.example.testingLogIn.Enums.Role;
import com.example.testingLogIn.ModelDTO.UserDTO;
import com.example.testingLogIn.WebsiteSecurityConfiguration.CustomUserDetailsService;
import com.example.testingLogIn.WebsiteSecurityConfiguration.UserModel;
import com.example.testingLogIn.WebsiteSecurityConfiguration.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/authentication")
public class PasswordResetController {

    private final UserRepo userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final CustomUserDetailsService userService;

    @Autowired
    public PasswordResetController(UserRepo userRepository, PasswordResetTokenRepository tokenRepository, EmailService emailService, CustomUserDetailsService userService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.userService = userService;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        UserModel user = userRepository.findByUsername(email);

        CompletableFuture.runAsync(()->tokenRepository.deleteSomeTokens(LocalDateTime.now()));

        if(user.getRole().equals(Role.STUDENT))
            return new ResponseEntity<>("Please contact admin for your account password",HttpStatus.NOT_ACCEPTABLE);
        if(user != null){
            String token = TokenGenerator.generateToken();
            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setToken(token);
            resetToken.setUser(user);
            resetToken.setExpiryDate(LocalDateTime.now().plusHours(24));
            tokenRepository.save(resetToken);

            emailService.sendPasswordResetEmail(user.getUsername(),token);

            return ResponseEntity.ok("Password reset email sent");
        }else{
            return new ResponseEntity<>("Username not found",HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String newPassword, @RequestParam String token) {
        try{
            CompletableFuture.runAsync(()->tokenRepository.deleteSomeTokens(LocalDateTime.now()));
            PasswordResetToken pwToken = tokenRepository.findByToken(token).orElse(null);
            if(pwToken != null && pwToken.getExpiryDate().isAfter(LocalDateTime.now())){
                UserModel user = pwToken.getUser();
                userService.changeUserPassword(user,newPassword);
                tokenRepository.deleteUserTokens(user.getStaffId());
                return new ResponseEntity<>("User password changed successfully",HttpStatus.OK);
            }
            return new ResponseEntity<>("Invalid token.", HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<>("Server error",HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/confirm-pw")
    public ResponseEntity<String> authenticatePassword(@RequestParam String pw){
        userService.authenticateAdminPassword(pw);
        return new ResponseEntity<>("Password Confirmed",HttpStatus.OK);
    }

    @GetMapping("/get-user")
    public UserDTO getUserInfo(@RequestParam String token){
        PasswordResetToken getUser = tokenRepository.findByToken(token).orElseThrow(NullPointerException::new);
        return getUser.getUser().mapperDTO();
    }
}
