package com.example.testingLogIn.PasswordResetPackage;

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
        UserModel user = Optional.ofNullable(userRepository.findByUsername(email))
                .orElseThrow(NullPointerException::new);

        // Generate and save token
        String token = TokenGenerator.generateToken();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        tokenRepository.save(resetToken);

        // Send email
        emailService.sendPasswordResetEmail(user.getUsername(),token);

        return ResponseEntity.ok("Password reset email sent");
    }

    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String newPassword, @RequestParam String token) {
        PasswordResetToken pwToken = tokenRepository.findByToken(token).orElse(null);
        if(pwToken != null && pwToken.getExpiryDate().isAfter(LocalDateTime.now())){
            UserModel user =pwToken.getUser();
            userService.changeUserPassword(user,newPassword);
            tokenRepository.delete(pwToken);
            return new ResponseEntity<>("User password changed successfully",HttpStatus.OK);
        }
        return new ResponseEntity<>("Token not found", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/get-user")
    public UserDTO getUserInfo(@RequestParam String token){
        PasswordResetToken getUser = tokenRepository.findByToken(token).orElseThrow(NullPointerException::new);
        return getUser.getUser().mapperDTO();
    }
}
