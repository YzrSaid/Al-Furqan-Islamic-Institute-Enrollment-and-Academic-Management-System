package com.example.testingLogIn.Controllers;

import com.example.testingLogIn.ModelDTO.PaymentRecordDTO;
import com.example.testingLogIn.Services.PaymentRecordService;
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
@RequestMapping("/paymentrecord")
@Controller
public class StudentPaymentController {
    @Autowired
    private PaymentRecordService paymentService;
    
    @PostMapping("/add")
    public ResponseEntity<String> addNewPaymentRecord(@RequestBody PaymentRecordDTO newRecord){
        try{
            if(paymentService.addNewRecord(newRecord))
                return new ResponseEntity<>("Payment Transaction Completed",HttpStatus.OK);
            else
                return new ResponseEntity<>("Transaction Failed. Student record not found",HttpStatus.NOT_FOUND);
        }catch(Exception e){
            return new ResponseEntity<>("Transaction Failed",HttpStatus.CONFLICT);
        }
    }
}
