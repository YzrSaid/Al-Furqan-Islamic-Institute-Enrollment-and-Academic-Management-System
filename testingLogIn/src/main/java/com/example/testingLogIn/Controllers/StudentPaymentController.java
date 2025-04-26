package com.example.testingLogIn.Controllers;

import com.example.testingLogIn.CustomObjects.PaymentObject;
import com.example.testingLogIn.CustomObjects.StudentPaymentForm;
import com.example.testingLogIn.CustomObjects.PagedResponse;
import com.example.testingLogIn.Repositories.PaymentTransactionRepo;
import com.example.testingLogIn.Services.PaymentRecordService;

import org.springframework.beans.factory.annotation.Autowired;
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
            System.out.println("Hiii");
            return new ResponseEntity<>(paymentService.addPaymentAutoAllocate(studentId,gradeLevel,amount,po.getFeesId()),HttpStatus.OK);
        }catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("Compilation Error. Report to SlimFordy",HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/testing/{transactionId}")
    public ResponseEntity<Object> getTransaction(@PathVariable String transactionId){
        try {
            return new ResponseEntity<>(paymentService.getTransactionById(transactionId), HttpStatus.OK);
        }catch(NullPointerException npe){
            return new ResponseEntity<>("Way sulod",HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/testing/hii")
    public ResponseEntity<String> getTransaction(){
        try {
            return new ResponseEntity<>("Suppp", HttpStatus.OK);
        }catch(NullPointerException npe){
            return new ResponseEntity<>("Way sulod",HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/transactions/all")
    public ResponseEntity<PagedResponse> getAllTransactions(@RequestParam(required = false) Integer pageNum,
                                                            @RequestParam(required = false,defaultValue = "10") Integer pageSize,
                                                            @RequestParam(required = false,defaultValue = "Transaction") String particular,
                                                            @RequestParam(required = false,defaultValue = "") String q){
        try {
            return new ResponseEntity<>(paymentService.getTransactions(pageNum, pageSize, particular, q), HttpStatus.OK);
        }catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/void_payment")
    public ResponseEntity<String> voidSelectedpayment(@RequestParam String transaction){
        try{
            boolean result = paymentService.voidThePayment(transaction);
            return new ResponseEntity<>("Payment Transaction Voided Successfully",HttpStatus.OK);
        }catch(NullPointerException npe){
            return new ResponseEntity<>("Di ko nakita ang hinahanap mo!",HttpStatus.NOT_FOUND);
        }catch(Exception e){
            return new ResponseEntity<>("Better call slimfordy",HttpStatus.CONFLICT);
        }
    }

    //NEW STUDENT PAYMENT FORM
    @GetMapping("/form/{studentId}")                                                    //if gradeLevelId = false (for all time debt) else if gradeLevelId has value, it is for the enrollment Payment form
    public ResponseEntity<?> getPaymentForm(@PathVariable int studentId,
                                            @RequestParam(required = false) Integer gradeLevelId,
                                            @RequestParam(defaultValue = "false",required = false) boolean forBreakDown,
                                            @RequestParam(defaultValue = "1",required = false) int pageNo,
                                            @RequestParam(defaultValue = "3",required = false) int pageSize,
                                            @RequestParam(defaultValue = "false", required = false) boolean forPayment){
        try{
            if(gradeLevelId != null){
                return new ResponseEntity<>(paymentService.enrollmentPaymentForm(studentId,gradeLevelId),HttpStatus.OK);
            }else{
                return new ResponseEntity<>(paymentService.getStudentPaymentForm(studentId,forBreakDown,pageNo,pageSize,forPayment),HttpStatus.OK);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

}
