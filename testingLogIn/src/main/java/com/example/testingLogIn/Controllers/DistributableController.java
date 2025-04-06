package com.example.testingLogIn.Controllers;

import com.example.testingLogIn.ModelDTO.DistributableDTO;
import com.example.testingLogIn.ModelDTO.DistributablePerStudentDTO;
import com.example.testingLogIn.Models.Distributable;
import com.example.testingLogIn.PagedResponse.StudentDistributablePage;
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
        try{
            return new ResponseEntity<>(distributableServices.getById(itemId),HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<String> addNewDistributable(@RequestBody DistributableDTO newDistributable){
        System.out.println(newDistributable);
        try{
            if(distributableServices.addNewDistributable(newDistributable))
                return new ResponseEntity<>("New distributable successfully added",HttpStatus.OK);
            else
                return new ResponseEntity<>("Distributable item already exists",HttpStatus.NOT_ACCEPTABLE);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateDistributable(@RequestBody DistributableDTO newDistributable){
        try{
            if(distributableServices.updateDistributable(newDistributable))
                return new ResponseEntity<>("Distributable edited successfully",HttpStatus.OK);
            else
                return new ResponseEntity<>("Distributable item already exists",HttpStatus.NOT_ACCEPTABLE);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @DeleteMapping("/delete/{itemId}")
    public ResponseEntity<String> deleteDistributable(@PathVariable("itemId") int itemId){
        try{
            distributableServices.deleteDistributable(itemId);
            return new ResponseEntity<>("Distributable edited successfully",HttpStatus.OK);
        }catch(NullPointerException npe){
            return new ResponseEntity<>("Distributable not found",HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/student-distribution/all")
    public ResponseEntity<StudentDistributablePage> getStudentDistributions(@RequestParam(defaultValue = "1") int pageNo,
                                                                            @RequestParam(defaultValue = "10") int pageSize){
        try {
            return new ResponseEntity<>(distributableServices.getStudentDistributable(pageNo, pageSize, null, null), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
}
