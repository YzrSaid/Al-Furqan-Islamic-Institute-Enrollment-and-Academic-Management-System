package com.example.testingLogIn.Controllers;

import com.example.testingLogIn.CustomObjects.MultipleInteger;
import com.example.testingLogIn.Models.TransfereeRequirements;
import com.example.testingLogIn.Services.TransferReqServices;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
class StudentRequirementsSubmission{
    private int studentId;
    private List<Integer> requirementIds;
}
@RequestMapping("/trans-req")
@Controller
public class TransfereeRequirementsController {
    @Autowired
    private TransferReqServices transferReqServices;

    @GetMapping("/all")
    public ResponseEntity<?> getAllTransfereeRequirements(@RequestParam(required = false,defaultValue = "true") boolean isNotDeleted){
        try{
            return new ResponseEntity<>(transferReqServices.getAllRequirements(isNotDeleted),HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>("Server Error",HttpStatus.CONFLICT);
        }
    }
    @PostMapping("/add")
    public ResponseEntity<String> addNewRequirements(@RequestParam(defaultValue = "") String reqName){
        if(reqName == null || reqName.equalsIgnoreCase(""))
            return new ResponseEntity<>("Requirement name cannot be empty",HttpStatus.NOT_ACCEPTABLE);
        try{
            transferReqServices.addNewRequirement(reqName);
            return new ResponseEntity<>("New transferee requirement successfully added",HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>("Server error",HttpStatus.CONFLICT);
        }
    }
    @PutMapping("/update/{requirementId}")
    public ResponseEntity<String> updateRequirementName(@PathVariable int requirementId,
                                                        @RequestParam(defaultValue = "") String newName){
        if(newName == null || newName.equalsIgnoreCase("".trim()))
            throw new IllegalArgumentException("Invalid requirement name");
        try{
            transferReqServices.updateName(requirementId,newName);
            return new ResponseEntity<>("Transferee requirement updated successfully",HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>("Server conflict",HttpStatus.CONFLICT);
        }
    }
    @DeleteMapping("/delete/{requirementId}")
    public ResponseEntity<String> deleteRequirement(@PathVariable int requirementId){
        try{
            transferReqServices.deleteRequirement(requirementId);
            return new ResponseEntity<>("Transferee requirement deleted successfully",HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>("Server error",HttpStatus.CONFLICT);
        }
    }
    @PostMapping("/add/student-requirements/{studentId}")
    public ResponseEntity<String> addStudentTransfereeRequirements(@PathVariable int studentId,@RequestBody MultipleInteger ids){
        try{
            transferReqServices.addingStudentRequirements(studentId,ids.getIds());
            return new ResponseEntity<>("Student transferee requirements updated successfully",HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>("Server error",HttpStatus.CONFLICT);
        }
    }
}
