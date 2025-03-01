package com.example.testingLogIn.Controllers;

import com.example.testingLogIn.ModelDTO.EnrollmentDTO;
import com.example.testingLogIn.ModelDTO.StudentDTO;
import com.example.testingLogIn.Services.EnrollmentServices;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
/**
 *
 * @author magno
 */
@RequestMapping("/enrollment")
@Controller
public class EnrollmentController {
    
    @Autowired
    private EnrollmentServices enrollmentService;
    
    @PostMapping("/add/listing")
    public ResponseEntity<String> addStudentToListing(@RequestBody StudentDTO student){
        try{
            if(enrollmentService.addStudentToListing(student))
                return new ResponseEntity<>("Student Successfully Added To Listing",HttpStatus.OK);
            else
                return new ResponseEntity<>("Student Is Already In Enrollment Process",HttpStatus.CONFLICT);
        }catch(NullPointerException npe){
            return new ResponseEntity<>("Student Record Not Found",HttpStatus.NOT_FOUND);
        }catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("Transaction Failed",HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/all/{status}")
    public ResponseEntity<List<EnrollmentDTO>> getEnrollmentRecordsByStatus(@PathVariable String status){
        try{
            return new ResponseEntity<>(enrollmentService.getAllEnrollment(status),HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
    
    @PutMapping("/add/assessment/{enrollmentId}/{gradeLevelId}")
    public ResponseEntity<String> proceedToAssessment(@PathVariable int enrollmentId, @PathVariable int gradeLevelId){
        try{
            int result = enrollmentService.addToAssessment(enrollmentId, gradeLevelId);

            return switch (result) {
                case 1 -> new ResponseEntity<>("Enrollment Record Not Found",HttpStatus.NOT_FOUND);
                case 2 -> new ResponseEntity<>("Grade Level Record Not Found",HttpStatus.NOT_FOUND);
                default -> new ResponseEntity<>("Enrollment Record Successfully Moved To Assessment",HttpStatus.OK);
            };
        }catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("Transaction Failed. Try Again.",HttpStatus.CONFLICT);
        }
    }
    
    @PutMapping("/add/payment/{enrollmentId}/{sectionNumber}")
    public ResponseEntity<String> proceedToPayment(@PathVariable int enrollmentId, @PathVariable int sectionNumber){
        try{
            int result = enrollmentService.addToPayment(enrollmentId, sectionNumber);

            return switch (result) {
                case 1 -> new ResponseEntity<>("Enrollment Record Not Found",HttpStatus.NOT_FOUND);
                case 2 -> new ResponseEntity<>("Section Record Not Found",HttpStatus.NOT_FOUND);
                default -> new ResponseEntity<>("Enrollment Record Successfully Moved To Payment",HttpStatus.OK);
            };
        }catch(Exception e){
            return new ResponseEntity<>("Transaction Failed. Try Again.",HttpStatus.CONFLICT);
        }
    }
    
    @PutMapping("/add/enrolled/{enrollmentId}")
    public ResponseEntity<String> proceedToEnrolled(@PathVariable int enrollmentId){
        try{
            int result = enrollmentService.addToEnrolled(enrollmentId);

            return switch (result) {
                case 1 -> new ResponseEntity<>("Enrollment Record Not Found",HttpStatus.NOT_FOUND);
                default -> new ResponseEntity<>("Student Successfully Enrolled",HttpStatus.OK);
            };
        }catch(Exception e){
            return new ResponseEntity<>("Transaction Failed. Try Again.",HttpStatus.CONFLICT);
        }
    }
    
}
