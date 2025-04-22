package com.example.testingLogIn.Controllers;

import com.example.testingLogIn.CustomObjects.MultipleInteger;
import com.example.testingLogIn.ModelDTO.DistributableDTO;
import com.example.testingLogIn.CustomObjects.PagedResponse;
import com.example.testingLogIn.Services.DistributableServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequestMapping("/distributable")
@Controller
public class DistributableController {
    @Autowired
    private DistributableServices distributableServices;

    @GetMapping("/all")
    public ResponseEntity<List<DistributableDTO>> getDistributable(@RequestParam(defaultValue = "true") boolean isNotDeleted){
        try{
            return new ResponseEntity<>(distributableServices.getAllDistributable(isNotDeleted),HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<DistributableDTO> getDistributable(@PathVariable("itemId") int itemId){
        return new ResponseEntity<>(distributableServices.getById(itemId),HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addNewDistributable(@RequestBody DistributableDTO newDistributable){
        distributableServices.addNewDistributable(newDistributable);
        return new ResponseEntity<>("New distributable successfully added",HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateDistributable(@RequestBody DistributableDTO newDistributable){
        distributableServices.updateDistributable(newDistributable);
        return new ResponseEntity<>("Distributable edited successfully", HttpStatus.OK);
    }

    @DeleteMapping("/delete/{itemId}")
    public ResponseEntity<String> deleteDistributable(@PathVariable("itemId") int itemId){
        distributableServices.deleteDistributable(itemId);
        return new ResponseEntity<>("Distributable edited successfully",HttpStatus.OK);
    }

    @GetMapping("/student-distribution/all")
    public ResponseEntity<PagedResponse> getStudentDistributions(@RequestParam(defaultValue = "1") int pageNo,
                                                                 @RequestParam(defaultValue = "10") int pageSize,
                                                                 @RequestParam(defaultValue = "") String student,
                                                                 @RequestParam Integer itemType,
                                                                 @RequestParam boolean isClaimed,
                                                                 @RequestParam String sortBy){
        try {
            itemType = itemType.equals(0) ? null : itemType;
            return new ResponseEntity<>(distributableServices.getStudentDistributable(pageNo, pageSize, student, isClaimed,itemType, sortBy), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/student-distribution/received/{distId}")
    public ResponseEntity<String> getStudentDistributions(@PathVariable int distId){
        try {
            distributableServices.itemDistributed(distId);
            return new ResponseEntity<>("Item distributed successfully",HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>("Server Conflict. Contact Dev",HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/student-distribution/received/multiple")
    public ResponseEntity<String> distributeMultiple(@RequestBody MultipleInteger selectedItem){
        try {
            distributableServices.multipleItemDistributed(selectedItem.getIds());
            return new ResponseEntity<>("Item distributed successfully",HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>("Server Conflict. Contact Dev",HttpStatus.CONFLICT);
        }
    }
}
