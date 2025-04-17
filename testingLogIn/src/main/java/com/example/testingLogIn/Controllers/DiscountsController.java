package com.example.testingLogIn.Controllers;

import com.example.testingLogIn.CustomObjects.MultipleInteger;
import com.example.testingLogIn.CustomObjects.StudentTotalDiscount;
import com.example.testingLogIn.Models.Discount;
import com.example.testingLogIn.CustomObjects.PagedResponse;
import com.example.testingLogIn.Services.DiscountsServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

//    @PostMapping("/add-student-discount/{discountId}")
//    public ResponseEntity<String> addStudentDiscount(@PathVariable int discountId,@RequestBody MultipleInteger students){
//        try{
//            if(discountsServices.addStudentDiscount(discountId,students.getIds()))
//                return new ResponseEntity<>("Student Discounts Added Successfully",HttpStatus.OK);
//            else
//                return new ResponseEntity<>("Discount Name Already Exist",HttpStatus.IM_USED);
//        }catch (Exception e){
//            e.printStackTrace();
//            return new ResponseEntity<>(HttpStatus.CONFLICT);
//        }
//    }

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

    @GetMapping("/records")
    public ResponseEntity<PagedResponse> getStudentDiscounts(   @RequestParam(defaultValue = "1", required = false) int pageNo,
                                                                @RequestParam(defaultValue = "10", required = false) int pageSize,
                                                                @RequestParam(defaultValue = "true",required = false) boolean isAvailed,
                                                                @RequestParam(defaultValue = "", required = false) String search,
                                                                @RequestParam(defaultValue = "0", required = false) Integer discountId){
        try{
            discountId = discountId.equals(0) ? null : discountId;
            return new ResponseEntity<>(discountsServices.getDiscountRecords(pageNo, pageSize, discountId, search, isAvailed),HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/add-student-discounts/{studentId}")
    public ResponseEntity<String> addStudentDiscounts(@PathVariable int studentId, @RequestBody MultipleInteger ids){
        try{
            discountsServices.addStudentDiscounts(studentId,ids.getIds());
            return new ResponseEntity<>("Student Discounts Updated Successfully",HttpStatus.OK);
        }catch (NullPointerException npe){
            npe.printStackTrace();
            return new ResponseEntity<>("Student record not found.",HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<>("Server Issue. Try again later.",HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/add-student-discount/{discountId}/{studentId}")
    public ResponseEntity<String> addStudentsDiscount(@PathVariable int discountId,
                                                      @PathVariable int studentId){
        try{
            List<Integer> discIds = new ArrayList<>();
            discIds.add(discountId);
            discountsServices.addStudentDiscounts(studentId,discIds);
            return new ResponseEntity<>("Discounts successfully added to student records",HttpStatus.OK);
        }catch (NullPointerException npe){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/add-student-discounts/{discountId}")
    public ResponseEntity<String> addStudentsDiscount(@PathVariable int discountId,
                                                      @RequestBody MultipleInteger studentIds){
        try{
            discountsServices.addStudentDiscount(discountId,studentIds.getIds());
            return new ResponseEntity<>("Discounts successfully added to student records",HttpStatus.OK);
        }catch (NullPointerException npe){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/remove-student-discount/{discId}")
    public ResponseEntity<String> removeStudentDiscount(@PathVariable int discId){
        try{
            discountsServices.removeStudentDiscount(discId);
            return new ResponseEntity<>("Discount successfully removed from student",HttpStatus.OK);
        }catch (NullPointerException e){
            return new ResponseEntity<>("Student discount record not found",HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/remove-student-discounts")
    public ResponseEntity<String> removeStudentsDiscount(@RequestBody MultipleInteger connectionIds){
        try{
            discountsServices.removeStudentsDiscount(connectionIds.getIds());
            return new ResponseEntity<>("Discounts successfully removed from students",HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
}
