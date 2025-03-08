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
    @GetMapping("")
    public String getDashboardzz(){
        return "dashboard";
    }

    @GetMapping("settings/create-user")
    public String getCreateUser(){
        return "settings/create-user";
    }

    // these are for enrollment pages

    @GetMapping("enrollment/listing")
    public String getListing(){
        return "/enrollment/listing";
    }

    @GetMapping("/enrollment/assessment")
    public String getAssessment(){
        return "/enrollment/assessment";
    }

    
    @GetMapping("/enrollment/payment")
    public String getPayment(){
        return "/enrollment/payment";
    }
    
    @GetMapping("/enrollment/payment/{enrollmentId}")
    public String getPaymentwithId(){
        return "/enrollment/payment";
    }
    
    @GetMapping("/enrollment/enrolled")
    public String getEnrolled(){
        return "/enrollment/enrolled";
    }


    // these are for transaction pages
   
    @GetMapping("/transaction/tuition")
    public String getTuition(){
        return "/transaction/tuition";
    }

    @GetMapping("/transaction/academic-resources")
    public String getAcademicResources(){
        return "/transaction/academic-resources";
    }

    // these are for maintenance page

    @GetMapping("/maintenance/grade-level")
    public String getGradeLevelMaintenance(){
        return "/maintenance/grade-level-maintenance";
    }

    
    @GetMapping("/maintenance/teacher")
    public String getTeacherMaintenance() {
        return "/maintenance/teacher-maintenance";
    }

    @GetMapping("/maintenance/school-year")
    public String getSchoolYearMaintenance(){
        return "/maintenance/school-year-maintenance";
    }

    @GetMapping("/maintenance/section")
    public String getSectionMaintenance(){
        return "/maintenance/section-maintenance";
    }

    @GetMapping("/maintenance/subject")
    public String getSubjectMaintenance(){
        return "/maintenance/subject-maintenance";
    }

    @GetMapping("/maintenance/fees-management")
    public String getFeesManagement(){
        return "/maintenance/fees-management";
    }


    // these are for reports pages

    @GetMapping("/reports/academic-resources")
    public String getAcadResourcesReports(){
        return "/reports/acad-resources-reports";
    }

    @GetMapping("/reports/academic-resources-type")
    public String getAcadResourcesReportsType(){
        return "/reports/acad-resources-type";
    }
    
    @GetMapping("/reports/payment")
    public String getPaymentReports(){
        return "/reports/payment-reports";
    }
    
    @GetMapping("/reports/payment/{studentName}")
    public String getPaymentReportsByStudent(){
        return "/reports/payment-reports";
    }
    
    @GetMapping("/reports/student")
    public String getStudentReports(){
        return "/reports/student-reports";
    }

    @GetMapping("/reports/teacher")
    public String getTeacherReports(){
        return "/reports/teacher-reports";
    }

    // these are for accounts pages

    @GetMapping("/accounts/verify-accounts")
    public String getAccountVerificationPage(){
        return "/accounts/verify-accounts";
    }

    @GetMapping("/accounts/my-account")
    public String getMyAccount(){
        return "/accounts/my-account";
    }

    @GetMapping("/accounts/manage-accounts")
    public String getManageAccounts(){
        return "/accounts/manage-accounts";
    }

    // these are for grade management pages

    @GetMapping("/grade-management")
    public String getGradeManagement(){
        return "/grade-management/grade-management";
    }

    @GetMapping("/grade-management/subject/{sectionId}/{subjectId}")
    public String getGradeManagementPerSubject(){
        return "/grade-management/grade-per-subject";
    }
    
    @GetMapping("/grade-management/class/{sectionId}")
    public String getGradeManagementPerClass(){
        return "/grade-management/grade-per-class";
    }

    // these are for schedule pages

    @GetMapping("/schedule")
    public String getSchedule(){
        return "/schedule/schedule";
    }
    
    @GetMapping("/schedule/sched-board/{secNumber}")
    public String getSchedBoard(){
        return "/schedule/sched-board";
    }
}
