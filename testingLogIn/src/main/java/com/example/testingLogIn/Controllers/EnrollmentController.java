package com.example.testingLogIn.Controllers;

import com.example.testingLogIn.ModelDTO.StudentDTO;
import com.example.testingLogIn.Services.EnrollmentServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
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
            return new ResponseEntity<>("Transaction Failed",HttpStatus.BAD_REQUEST);
        }
    }
    
}
