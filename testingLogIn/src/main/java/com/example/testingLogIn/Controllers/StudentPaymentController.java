package com.example.testingLogIn.Controllers;

import com.example.testingLogIn.ModelDTO.PaymentRecordDTO;
import com.example.testingLogIn.Services.PaymentRecordService;
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
@RequestMapping("/paymentrecord")
@Controller
public class StudentPaymentController{
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
    
    @GetMapping("/sem/{semId}")
    public ResponseEntity<List<PaymentRecordDTO>> getPaymentRecordsBySem(@PathVariable int semId){
        try{
            return new ResponseEntity<>(paymentService.getAllPaymentRecordsBySem(semId),HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<PaymentRecordDTO>> getAllPaymentRecords(){
        try{
            return new ResponseEntity<>(paymentService.getAllPaymentRecords(),HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
    
    @GetMapping("/datepaid/{condition}")
    public ResponseEntity<List<PaymentRecordDTO>> getPaymentRecordsByDateSort(@PathVariable String condition){
        try{
            return new ResponseEntity<>(paymentService.getAllTimeRecordsByDate(condition),HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
    
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<PaymentRecordDTO>> getStudentPaymentRecords(@PathVariable int studentId){
        try{
            return new ResponseEntity<>(paymentService.getAllStudentPaymentRecords(studentId),HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
    
    @GetMapping("/student/id/{studentId}")
    public ResponseEntity<List<PaymentRecordDTO>> getByStudentNamePaymentRecords(@PathVariable String studentId){
        try{
            return new ResponseEntity<>(paymentService.getAllStudentPaymentRecordsByName(studentId),HttpStatus.OK);
        }catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
    
    @PutMapping("/edit/{recordId}/{newFeeId}")
    public ResponseEntity<String> updateRecord(@PathVariable int recordId,@PathVariable int newFeeId){
        try{
            if(paymentService.editRecord(recordId, newFeeId))
                return new ResponseEntity<>("Payment record edited successfully",HttpStatus.OK);
            else
                return new ResponseEntity<>("Payment record not found",HttpStatus.NOT_FOUND);
        }catch(Exception e){
            return new ResponseEntity<>("Transaction Failed",HttpStatus.CONFLICT);
        }
    }
}
