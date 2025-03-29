package com.example.testingLogIn.Controllers;

import com.example.testingLogIn.ModelDTO.GradeLevelToRequiredPaymentDTO;
import com.example.testingLogIn.ModelDTO.RequiredPaymentsDTO;
import com.example.testingLogIn.Services.RequiredPaymentsServices;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/payments")
@Controller
public class RequiredPaymentController {
    private final RequiredPaymentsServices reqPaymentService;

    public RequiredPaymentController(RequiredPaymentsServices reqPaymentService) {
        this.reqPaymentService = reqPaymentService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addRequiredPayments(@RequestBody RequiredPaymentsDTO reqPayments) {
        try {
            if (reqPaymentService.addNewPayments(reqPayments) == 0)
                return new ResponseEntity<>("New payment added successfully", HttpStatus.OK);
            if (reqPaymentService.addNewPayments(reqPayments) == 1)
                return new ResponseEntity<>("New payment added successfully\n"+
                        "The Distributable name \"" + reqPayments.getName() + "\" already exists.", HttpStatus.OK);
            else
                return new ResponseEntity<>("The payment name \"" + reqPayments.getName() + "\" already exists.",
                        HttpStatus.CONFLICT);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Process failed", HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<RequiredPaymentsDTO>> getAllReqPayments() {
        try {
            return new ResponseEntity<>(reqPaymentService.getAllPayments(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/grade/{gradeLevelId}")
    public ResponseEntity<List<GradeLevelToRequiredPaymentDTO>> getAllReqPaymentsByGradeLevel(@PathVariable int gradeLevelId) {
        try {
            List<GradeLevelToRequiredPaymentDTO> toreturn = reqPaymentService.getPaymentsByGradeLevel(gradeLevelId);
            if (!toreturn.isEmpty())
                return new ResponseEntity<>(toreturn, HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/update/{feeId}")
    public ResponseEntity<String> updatedSelectedPayment(@PathVariable int feeId,
            @RequestBody RequiredPaymentsDTO updated) {
        try {
            if (reqPaymentService.updatePayment(feeId, updated))
                return new ResponseEntity<>("Required payment updated successfully", HttpStatus.OK);
            else
                return new ResponseEntity<>("Updating payment record failed as payment name not found",
                        HttpStatus.NOT_IMPLEMENTED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Process Failed", HttpStatus.CONFLICT);
        }
    }

    @DeleteMapping("/delete/{paymentName}")
    public ResponseEntity<String> deleteRequiredPayment(@PathVariable String paymentName) {
        try {
            reqPaymentService.deleteRequiredPayment(paymentName);
            return new ResponseEntity<>("Payment \"" + paymentName + "\" deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Process Failed", HttpStatus.CONFLICT);
        }
    }
}
