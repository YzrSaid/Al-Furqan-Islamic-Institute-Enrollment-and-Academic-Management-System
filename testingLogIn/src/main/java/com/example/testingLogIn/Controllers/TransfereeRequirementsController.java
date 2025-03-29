package com.example.testingLogIn.Controllers;

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
            int result = transferReqServices.addNewRequirement(reqName);
            if(result == 1)
                return new ResponseEntity<>("Requirement name already exists",HttpStatus.NOT_ACCEPTABLE);
            return new ResponseEntity<>("New transferee requirement successfully added",HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("Server conflict",HttpStatus.CONFLICT);
        }
    }
    @PutMapping("/update/{requirementId}")
    public ResponseEntity<String> updateRequirementName(@PathVariable int requirementId,
                                                        @RequestParam(defaultValue = "") String newName){
        if(newName == null || newName.equalsIgnoreCase("".trim()))
            return new ResponseEntity<>("Invalid requirement name",HttpStatus.NOT_ACCEPTABLE);
        try{
            if(transferReqServices.updateName(requirementId,newName))
                return new ResponseEntity<>("Transferee requirement updated successfully",HttpStatus.OK);

            return new ResponseEntity<>("Another transferee requirements is using the name \"?\"".replace("?",newName),HttpStatus.NOT_ACCEPTABLE);
        }catch (NullPointerException npe){
            return new ResponseEntity<>("The transferee requirement to be updated was not found. Try again",HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<>("Server conflict. Contact Inoori Network",HttpStatus.CONFLICT);
        }
    }
    @DeleteMapping("/delete/{requirementId}")
    public ResponseEntity<String> deleteRequirement(@PathVariable int requirementId){
        try{
                transferReqServices.deleteRequirement(requirementId);
                return new ResponseEntity<>("Transferee requirement deleted successfully",HttpStatus.OK);
        }catch (NullPointerException npe){
            return new ResponseEntity<>("The transferee requirement to be deleted was not found. Try again",HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<>("Server conflict. Contact Inoori Network",HttpStatus.CONFLICT);
        }
    }
    @PostMapping("/add/student-requirements")
    public ResponseEntity<String> addStudentTransfereeRequirements(@RequestBody StudentRequirementsSubmission submitted){
        if(submitted == null)
            return new ResponseEntity<>("Submitted an invalid information",HttpStatus.NOT_ACCEPTABLE);
        try{
            transferReqServices.addingStudentRequirements(submitted.getStudentId(),submitted.getRequirementIds());
            return new ResponseEntity<>("Student transferee requirements successfully added",HttpStatus.OK);
        }catch (NullPointerException npe){
            return new ResponseEntity<>("Student record not found",HttpStatus.NOT_FOUND);
        }
        catch (Exception e){
            return new ResponseEntity<>("Server error. Contact Inoori Network",HttpStatus.CONFLICT);
        }
    }
}
