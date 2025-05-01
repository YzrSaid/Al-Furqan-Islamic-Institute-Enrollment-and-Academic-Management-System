package com.example.testingLogIn.Services;

import com.example.testingLogIn.CustomObjects.FeeReports;
import com.example.testingLogIn.CustomObjects.GradeLevelEnrolledReports;
import com.example.testingLogIn.CustomObjects.SchoolYearReports;
import com.example.testingLogIn.Models.*;
import com.example.testingLogIn.Repositories.EnrollmentRepo;
import com.example.testingLogIn.Repositories.PaymentsRecordRepo;
import com.example.testingLogIn.Repositories.SchoolYearRepo;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

/**
 *
 * @author magno
 */
@Service
public class SchoolYearServices {
    @Autowired
    private SchoolYearRepo schoolYearRepo;
    @Autowired
    private sySemesterServices semService;
    @Autowired
    private EnrollmentRepo enrollmentRepo;
    @Autowired
    private PaymentsRecordRepo paymentsRecordRepo;

    public boolean addNewSchoolYear(SchoolYear sy){
        if(doesSchoolYearExist(sy.getSchoolYear()))
            return false;
        else{
            sy.setNotDeleted(true);
            schoolYearRepo.save(sy);
            
            new Thread(() -> {
                try {
                    Thread.sleep(499);
                } catch (InterruptedException ex) {
                    Logger.getLogger(SchoolYearServices.class.getName()).log(Level.SEVERE, null, ex);
                }
                semService.addSemesters(sy.getSchoolYear());}).start();
            
            return true;}
    }
    
    public SchoolYear getSchoolYear(int syNumber){
        return schoolYearRepo.findById(syNumber).orElse(null);
    }
    
    public List<SchoolYear> getSchoolYears(){
        return schoolYearRepo.findAll().stream()
                             .filter(SchoolYear::isNotDeleted)
                             .sorted((sy1,sy2)-> sy2.getSchoolYear().compareTo(sy1.getSchoolYear()))
                             .toList();
    }
    
    public boolean updateSchoolYear(SchoolYear sy){
        SchoolYear syUpdated = schoolYearRepo.findById(sy.getSchoolYearNum()).orElse(null);
        if(syUpdated == null)
            throw new NullPointerException();
        
        boolean doesSchoolYearExist = schoolYearRepo.findAll().stream()
                                                    .filter(schoolY ->  schoolY.isNotDeleted() &&
                                                                        schoolY.getSchoolYearNum() != sy.getSchoolYearNum() &&
                                                                        schoolY.getSchoolYear().equalsIgnoreCase(sy.getSchoolYear()))
                                                    .findFirst().orElse(null) != null;
        if(doesSchoolYearExist)
            return false;
        
        syUpdated.setSchoolYear(sy.getSchoolYear());
        schoolYearRepo.save(syUpdated);

        return true;
    }
    
    public boolean deleteSchoolYear(int syNumber){
        SchoolYear syDelete = schoolYearRepo.findById(syNumber).orElse(null);
        if(syDelete != null){
            syDelete.setNotDeleted(false);
            schoolYearRepo.save(syDelete);
            return true;
        }
        return false;
    }
    
    private boolean doesSchoolYearExist(String schoolYear){
        return schoolYearRepo.findAll().stream()
                             .filter(sy -> sy.isNotDeleted() && sy.getSchoolYear().equalsIgnoreCase(schoolYear))
                             .findFirst().orElse(null) != null;
    }

    public InputStreamResource schoolYearEnrollmentReport( int syId) throws IOException {
        SchoolYear sy = schoolYearRepo.findById(syId)
                .orElseThrow(() -> new NullPointerException("School Year Not Found"));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write("Grade & Section,Student Name\n".getBytes(StandardCharsets.UTF_8));
        for(SchoolYearSemester sem : sy.getSemesterList()){
            outputStream.write((sem.toString()+"\n").getBytes(StandardCharsets.UTF_8));
            for (Enrollment enrollment : enrollmentRepo.findBySchoolYear(sem.getSySemNumber())) {
                String line = String.format("%s,%s\n",
                        enrollment.getSectionToEnroll().toString(),
                        enrollment.getStudent().getFullName()
                );
                outputStream.write(line.getBytes(StandardCharsets.UTF_8));
            }

        }
        return new InputStreamResource(new ByteArrayInputStream(outputStream.toByteArray()));
    }

    public Map<String, SchoolYearReports> schoolYearReports(int syId) throws IOException {
        SchoolYear sy = schoolYearRepo.findById(syId)
                .orElseThrow(() -> new NullPointerException("School Year Not Found"));
        Map<String,SchoolYearReports> yearlyReports = new HashMap<>();
        for(SchoolYearSemester sem : sy.getSemesterList()){
            List<FeeReports> feeReports = new ArrayList<>();
            List<GradeLevelEnrolledReports> enrolledReports = new ArrayList<>();

            //for reports per fees
            for(RequiredFees req : paymentsRecordRepo.uniquePaidFeesBySem(sem.getSySemNumber()))
                feeReports.add(new FeeReports(req.getName(),paymentsRecordRepo.totalPaidForSpecificFeeBySem(req.getId(),sem.getSySemNumber()).orElse(0D)));

            //for reports on number of enrolled students per grade level
            for(GradeLevel level : enrollmentRepo.uniqueGradeLevelsEnrolled(sem.getSySemNumber()))
                enrolledReports.add(new GradeLevelEnrolledReports(level.getLevelName(),enrollmentRepo.countSectionGradeEnrollment(null, sem.getSySemNumber(), level.getLevelId()).orElse(0)));

            yearlyReports.put(sem.toString(),new SchoolYearReports(feeReports,enrolledReports));
        }
        return yearlyReports;
    }
}
