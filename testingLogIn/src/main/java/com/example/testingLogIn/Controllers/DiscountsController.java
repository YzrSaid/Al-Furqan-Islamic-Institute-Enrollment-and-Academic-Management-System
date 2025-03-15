package com.example.testingLogIn.Controllers;

import com.example.testingLogIn.CustomObjects.NewStudentDiscounts;
import com.example.testingLogIn.CustomObjects.StudentTotalDiscount;
import com.example.testingLogIn.Models.Discount;
import com.example.testingLogIn.Services.DiscountsServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/discounts")
@Controller
public class DiscountsController {

    @Autowired
    private DiscountsServices discountsServices;

    @PostMapping("/add")
    public ResponseEntity<String> addNewDiscount(@RequestBody Discount discount){
        try{
            if(discountsServices.addDiscount(discount))
                return new ResponseEntity<>("New Discount Added Successfully",HttpStatus.OK);
            else
                return new ResponseEntity<>("Discount Name Already Exist",HttpStatus.IM_USED);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/add/{discountId}")
    public ResponseEntity<String> addStudentDiscount(@PathVariable int discountId,@RequestBody NewStudentDiscounts students){
        try{
            if(discountsServices.addStudentDiscount(discountId,students.getStudentIds()))
                return new ResponseEntity<>("Student Discounts Added Successfully",HttpStatus.OK);
            else
                return new ResponseEntity<>("Discount Name Already Exist",HttpStatus.IM_USED);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/total/{studentId}")
    public ResponseEntity<StudentTotalDiscount> addStudentDiscount(@PathVariable int studentId){
        try{
            return new ResponseEntity<>(discountsServices.getStudentTotalDiscount(studentId),HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
}
