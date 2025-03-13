package com.example.testingLogIn.Controllers;

import com.example.testingLogIn.ModelDTO.RequiredPaymentsDTO;
import com.example.testingLogIn.ModelDTO.TotalPaid;
import com.example.testingLogIn.Repositories.PaymentsRecordRepo;
import com.example.testingLogIn.Services.StudentFeesListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/student-fees")
@Controller
public class StudentFeesListController {

    @Autowired
    private StudentFeesListService studFees;
    @Autowired
    private PaymentsRecordRepo paidRepo;
    
    @GetMapping("/{studentId}")
    public ResponseEntity<List<RequiredPaymentsDTO>> getStudentFeesRecord(@PathVariable int studentId,
                                                                          @RequestParam(required = false) Integer semId){
        try {
            return new ResponseEntity<>(studFees.feesList(studentId, semId), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
    
    @GetMapping("/total/{studentId}")
    public ResponseEntity<List<TotalPaid>> getTotalPaid(@PathVariable int studentId, @RequestParam(required = false) Integer semId){
        try {
            return new ResponseEntity<>(paidRepo.totalPaidPerFee(studentId), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
}
