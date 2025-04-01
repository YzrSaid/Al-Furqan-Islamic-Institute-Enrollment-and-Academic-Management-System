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

import java.util.List;

@RequestMapping("/discounts")
@Controller
public class DiscountsController {

    @Autowired
    private DiscountsServices discountsServices;

    @GetMapping("/all")
    public ResponseEntity<List<Discount>> getDiscounts(@RequestParam(defaultValue = "true") boolean isNotDeleted){
        try{
            return new ResponseEntity<>(discountsServices.getDiscountsList(isNotDeleted),HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

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

    @PostMapping("/add-student-discount/{discountId}")
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

    @DeleteMapping("/delete/{discountId}")
    public ResponseEntity<String> deleteDiscount(@PathVariable int discountId){
        try {
            discountsServices.deleteDiscount(discountId);
            return new ResponseEntity<>("Discount deleted successfully", HttpStatus.OK);
        }catch(NullPointerException npe){
            return new ResponseEntity<>("Discount not found",HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<>("Server conflict",HttpStatus.CONFLICT);
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
