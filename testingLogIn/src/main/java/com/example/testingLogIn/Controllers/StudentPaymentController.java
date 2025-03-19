package com.example.testingLogIn.Controllers;

import com.example.testingLogIn.CustomObjects.PaymentObject;
import com.example.testingLogIn.CustomObjects.StudentPaymentForm;
import com.example.testingLogIn.ModelDTO.PaymentRecordDTO;
import com.example.testingLogIn.ModelDTO.PaymentTransactionDTO;
import com.example.testingLogIn.Models.PaymentTransaction;
import com.example.testingLogIn.Repositories.PaymentTransactionRepo;
import com.example.testingLogIn.Services.PaymentRecordService;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author magno
 */
@RequestMapping("/paymentrecord")
@Controller
public class StudentPaymentController{
    @Autowired
    private PaymentRecordService paymentService;
    @Autowired
    private PaymentTransactionRepo transactionRepo;

    //WILL BE USED FOR NEW PROCESS
    @PostMapping("/add/all/{studentId}/{amount}")
    public ResponseEntity<Object> addPayment(@PathVariable int studentId,
                                             @RequestParam(required = false) Integer gradeLevel,//gradeLevel with value is for enrollment payment part
                                             @PathVariable double amount,
                                             @RequestBody PaymentObject po){ //requires {"feesId"[1,2]} JSON body, 1 and 2 are feesId(example onlyyy)
        try{
            return new ResponseEntity<>(paymentService.addPaymentAutoAllocate(studentId,gradeLevel,amount,po.getFeesId()),HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>("Compilation Error. Report to SlimFordy",HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/testing/{transactionId}")
    public ResponseEntity<Object> getTransaction(@PathVariable String transactionId){
        try {
            return new ResponseEntity<>(Objects.requireNonNull(transactionRepo.findById(transactionId).map(PaymentTransaction::DTOmapper).orElse(null)), HttpStatus.OK);
        }catch(NullPointerException npe){
            return new ResponseEntity<>("Way sulod",HttpStatus.NOT_FOUND);
        }
    }

    //NEW STUDENT PAYMENT FORM
    @GetMapping("/form/{studentId}")                                                    //if gradeLevelId = false (for all time debt) else if gradeLevelId has value, it is for the enrollment Payment form
    public ResponseEntity<StudentPaymentForm> getPaymentForm(@PathVariable int studentId,@RequestParam(required = false) Integer gradeLevelId){
        try{
            return new ResponseEntity<>(paymentService.getStudentPaymentForm(studentId,gradeLevelId),HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
