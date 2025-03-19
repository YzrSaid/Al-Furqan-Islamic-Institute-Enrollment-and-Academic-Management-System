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

    //WILL BE REMOVED NEXT PAYMENT PROCESS UPDATE
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

    @GetMapping("/{condition}/{page}/{size}")
    public ResponseEntity<PagedModel<EntityModel<PaymentRecordDTO>>> testingPage(@PathVariable String condition, @PathVariable int page, @PathVariable int size){
        try{
            return new ResponseEntity<>(paymentService.testingPageable(condition,page,size), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
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

    //TO BE REMOVED
    @GetMapping("/balance/{studentId}")
    public ResponseEntity<Double> getStudentBalance(@PathVariable int studentId,
                                                    @RequestParam(required = false) Integer feeId,
                                                    @RequestParam(required = false) Integer gradeLevelId,
                                                    @RequestParam(required = false) Boolean currentSem){
        try{
            return new ResponseEntity<>(paymentService.getStudentBalance(studentId,feeId,gradeLevelId,currentSem),HttpStatus.OK);
        }catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.CONFLICT);
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

    @GetMapping("/datepaid/{condition}")
    public ResponseEntity<List<PaymentRecordDTO>> getPaymentRecordsByDateSort(@PathVariable String condition){
        try{
            return new ResponseEntity<>(paymentService.getAllTimeRecordsByDates(condition),HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/datepaid/{condition}/{pageNum}")
    public ResponseEntity<PagedModel<EntityModel<PaymentRecordDTO>>> getPaymentRecordsPage(@PathVariable String condition, @PathVariable int pageNum){
        try{
            return new ResponseEntity<>(paymentService.getAllTimeRecordsByDate(condition,pageNum),HttpStatus.OK);
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
