package com.example.testingLogIn.Controllers;

import com.example.testingLogIn.Enums.Role;
import com.example.testingLogIn.WebsiteSecurityConfiguration.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping
@Controller
public class PrivateTemplateControllers {

    private final CustomUserDetailsService userDetailsService;
    @Autowired
    public PrivateTemplateControllers(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;}

    @GetMapping("/h?me")
    public String getDashboard() {
        if(userDetailsService.getCurrentlyLoggedInUser().getRole() != Role.STUDENT)
            return "dashboard";

        return "student-account/home";
    }

    // These are for the student account

    @GetMapping("/personal-profile")
    public String getStudentAccountPersonalProfile() {
        return "student-account/personal-profile";
    }
    @GetMapping("/grades")
    public String getStudentAccountGrades() {
        return "student-account/grades";
    }

    @GetMapping("/class-schedule")
    public String getStudentAccountSchedule() {
        return "student-account/class-schedule";
    }

    @GetMapping("/balance")
    public String getStudentBalanceView() {
        return "student-account/student-balance";
    }

    @GetMapping("/resources")
    public String getStudentResourcesView() {
        return "student-account/resources";
    }

    @GetMapping("/accounts/create-user")
    public String getCreateUser() {
        return "settings/create-user";
    }

    @GetMapping("settings/school-settings")
    public String getSchoolSetting() {
        return "settings/school-settings";
    }

    @GetMapping("enrollment/listing")
    public String getListing() {
        return "/enrollment/listing";
    }

    @GetMapping("/enrollment/assessment")
    public String getAssessment() {
        return "/enrollment/assessment";
    }

    @GetMapping("/enrollment/payment")
    public String getPayment() {
        return "/enrollment/payment";
    }

    @GetMapping("/enrollment/payment/{enrollmentId}")
    public String getPaymentwithId() {
        return "/enrollment/payment";
    }

    @GetMapping("/enrollment/enrolled")
    public String getEnrolled() {
        return "/enrollment/enrolled";
    }

    // these are for transaction pages

    @GetMapping("/transaction/payments")
    public String getPayments() {
        return "/transaction/payments";
    }

    @GetMapping("/transaction/academic-resources")
    public String getAcademicResources() {
        return "/transaction/academic-resources";
    }

    // these are for maintenance page

    @GetMapping("/maintenance/grade-level")
    public String getGradeLevelMaintenance() {
        return "/maintenance/grade-level-maintenance";
    }

    @GetMapping("/maintenance/teacher")
    public String getTeacherMaintenance() {
        return "/maintenance/teacher-maintenance";
    }

    @GetMapping("/maintenance/school-year")
    public String getSchoolYearMaintenance() {
        return "/maintenance/school-year-maintenance";
    }

    @GetMapping("/maintenance/section")
    public String getSectionMaintenance() {
        return "/maintenance/section-maintenance";
    }

    @GetMapping("/maintenance/subjects")
    public String getNewSubjectMaintenance() {
        return "/maintenance/subjects";
    }

    @GetMapping("/maintenance/subjects/{levelName}")
    public String getSubjectMaintenance() {
        return "/maintenance/subject-maintenance";
    }

    @GetMapping("/maintenance/fees-management")
    public String getFeesManagement() {
        return "/maintenance/fees-management";
    }

    @GetMapping("/maintenance/distributable-management")
    public String getDistributableManagement() {
        return "/maintenance/distributable-management";
    }

    @GetMapping("/maintenance/scholarship")
    public String getScholarship() {
        return "/maintenance/scholarship-maintenance";
    }

    
    // these are for reports pages

    @GetMapping("/reports/academic-resources")
    public String getAcadResourcesReports() {
        return "/reports/acad-resources-reports";
    }

    @GetMapping("/reports/academic-resources-type")
    public String getAcadResourcesReportsType() {
        return "/reports/acad-resources-type";
    }

    @GetMapping("/reports/payment")
    public String getPaymentReports() {
        return "/reports/payment-reports";
    }

    @GetMapping("/reports/payment/{studentName}")
    public String getPaymentReportsByStudent() {
        return "/reports/payment-reports";
    }

    @GetMapping("/reports/student")
    public String getStudentReports() {
        return "/reports/student-reports";
    }

    @GetMapping("/reports/student/{studentName}")
    public String getStudentReportSpecific() {
        return "/reports/student-report-specific";
    }

    @GetMapping("/reports/user-and-staff")
    public String getTeacherReports() {
        return "/reports/user-and-staff-reports";
    }

    @GetMapping("/reports/user-and-staff/{studentName}")
    public String getTeacherReportsSpecific() {
        return "/reports/user-and-staff-specific";
    }

    @GetMapping("/reports/scholarship")
    public String getDiscountReports() {
        return "/reports/discount-reports";
    }

    // these are for accounts pages
    @GetMapping("/accounts/student-accounts")
    public String getStudentAccountsPage() {
        return "/accounts/student-accounts";
    }

    @GetMapping("/accounts/verify-accounts")
    public String getAccountVerificationPage() {
        return "/accounts/verify-accounts";
    }

    @GetMapping("/accounts/my-account")
    public String getMyAccount() {
        return "/accounts/my-account";
    }

    @GetMapping("/accounts/manage-accounts")
    public String getManageAccounts() {
        return "/accounts/manage-accounts";
    }

    // these are for grade management pages

    @GetMapping("/grade-management")
    public String getGradeManagement() {
        if(userDetailsService.getCurrentlyLoggedInUser().getRole() == Role.ADMIN)
            return "/grade-management/grade-management-admin";

        return "/grade-management/teacher/grade-management-teacher";
    }

    @GetMapping("/grade-management/sections/{subjectId}")
    public String getGradeManagementSections() {
        return "/grade-management/teacher/view-sections";
    }

    @GetMapping("/grade-management/subject/{sectionId}/{subjectId}")
    public String getGradeManagementPerSubject() {
        if(userDetailsService.getCurrentlyLoggedInUser().getRole() == Role.ADMIN)
            return "/grade-management/grade-per-subject";

        return "/grade-management/teacher/grade-per-subject";
    }

    @GetMapping("/grade-management/class/{sectionId}")
    public String getGradeManagementPerClass() {
        return "/grade-management/grade-per-class copy";
    }

    @GetMapping("/class-list")
    public String getClassList() {
        return "/classlist/class-list";
    }

    @GetMapping("/class-list/students/{sectionName}")
    public String getClassStudentsList() {
        return "/classlist/class-students";
    }

    // these are for schedule pages
    @GetMapping("/schedule")
    public String getSchedule() {
        if(userDetailsService.getCurrentlyLoggedInUser().getRole() == Role.ADMIN)
            return "/schedule/schedule";

        return "/schedule/TeacherSched/sched-board-teacher";
    }

    @GetMapping("/schedule/sched-board/{secNumber}")
    public String getSchedBoard() {
        return "/schedule/sched-board";
    }

    @GetMapping("/reports/enrollment")
    public String enrollmentStatistics(){
        return "/statistics/enrolled-statistics";
    }

    @GetMapping("/statistics/graduates")
    public String graduatetStatistics(){
        return "/statistics/graduates-records";
    }

    @GetMapping("/statistics/passing-records")
    public String passingStatistics(){
        return "/statistics/passing-records";
    }
}