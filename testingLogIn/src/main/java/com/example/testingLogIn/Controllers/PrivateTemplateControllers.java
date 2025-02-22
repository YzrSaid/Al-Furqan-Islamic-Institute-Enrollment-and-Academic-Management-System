package com.example.testingLogIn.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping
@Controller
public class PrivateTemplateControllers {

    @GetMapping("/home")
    public String getDashboard(){
        return "dashboard";
    }

    @GetMapping("/listing")
    public String getListing(){
        return "/transaction/listing";
    }

    @GetMapping("/grade-level-maintenance")
    public String getGradeLevelMaintenance(){
        return "/maintenance/grade-level-maintenance";
    }

    @GetMapping("/verify-accounts")
    public String getAccountVericationPage(){
        return "/accounts/verify-accounts";
    }

    @GetMapping("/my-account")
    public String getMyAccount(){
        return "/accounts/my-account";
    }

    @GetMapping("/manage-accounts")
    public String getManageAccounts(){
        return "/accounts/manage-accounts";
    }

    @GetMapping("/grade-management")
    public String getGradeManagement(){
        return "/grade-management/grade-management";
    }

    @GetMapping("/subject-maintenance")
    public String getSubjectMaintenance(){
        return "/maintenance/subject-maintenance";
    }

    @GetMapping("/teacher-maintenance")
    public String getTeacherMaintenance() {
        return "/maintenance/teacher-maintenance";
    }

    @GetMapping("/schedule-maintenance")
    public String getScheduleMaintenance(){
        return "/maintenance/schedule/schedule-maintenance";
    }

    @GetMapping("/school-year-maintenance")
    public String getSchoolYearMaintenance(){
        return "/maintenance/school-year-maintenance";
    }

    @GetMapping("/section-maintenance")
    public String getSectionMaintenance(){
        return "/maintenance/section-maintenance";
    }

    @GetMapping("/sched-board")
    public String getSchedBoard(){
        return "/maintenance/schedule/sched-board";
    }
}
