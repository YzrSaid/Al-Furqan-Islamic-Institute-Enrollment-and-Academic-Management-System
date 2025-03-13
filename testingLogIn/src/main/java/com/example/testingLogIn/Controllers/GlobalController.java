package com.example.testingLogIn.Controllers;

import com.example.testingLogIn.Enums.Semester;
import com.example.testingLogIn.Services.sySemesterServices;
import com.example.testingLogIn.WebsiteSecurityConfiguration.UserModel;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.reactive.result.view.RedirectView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalController {

    @Autowired
    private sySemesterServices semService;

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
