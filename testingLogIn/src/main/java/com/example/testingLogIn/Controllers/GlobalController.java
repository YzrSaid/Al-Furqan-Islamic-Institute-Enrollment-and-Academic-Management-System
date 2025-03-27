package com.example.testingLogIn.Controllers;

import com.example.testingLogIn.Enums.Semester;
import com.example.testingLogIn.Services.sySemesterServices;
import com.example.testingLogIn.WebsiteSecurityConfiguration.UserModel;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.reactive.result.view.RedirectView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RequestMapping
@Controller
@ControllerAdvice
public class GlobalController {
    @Value("${images.external-dir:./external-images}")
    private String externalImageDir;

    @Autowired
    private sySemesterServices semService;

    @GetMapping("/website-logo")
    public ResponseEntity<byte[]> getDynamicImage() throws IOException {
        Path imagePath = Paths.get(externalImageDir, "al-furqanlogo.jpg");
        if (!Files.exists(imagePath)) {
            return ResponseEntity.notFound().build();
        }

        byte[] imageBytes = Files.readAllBytes(imagePath);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(imageBytes);
        }

    @ModelAttribute("userRole")
    public String getUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserModel user) {
            return user.getRole().toString();
        }

        return "GUEST";
    }

    @ModelAttribute("userFullName")
    public String getUserFullName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserModel user) {
            return user.getFirstname() + " " + user.getLastname();
        }
        return "UNKNOWN";
    }

    @ModelAttribute("userFirstName")
    public String getUserFirstName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserModel user) {
            return user.getFirstname(); 
        }
        return "UNKNOWN";
    }

    @ModelAttribute("currentSY")
    public String currentSchoolYear() {
        try {
            return semService.getCurrentActive().getSchoolYear().getSchoolYear();
        } catch (Exception e) {
            return "NOT FOUND";
        }
    }

    @ModelAttribute("currentSem")
    public String currentSem() {
        try {
            return semService.getCurrentActive().getSem() == Semester.First ? "First" : "Second";
        } catch (Exception e) {
            return "NOT FOUND";
        }
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public RedirectView handleNoHandlerFoundException(NoHandlerFoundException ex,
            RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", "The requested resource was not found.");
        return new RedirectView("/home"); // Redirect to a custom error page
    }
}
