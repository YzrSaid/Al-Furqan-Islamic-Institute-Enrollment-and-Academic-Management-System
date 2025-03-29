package com.example.testingLogIn.Controllers;

import com.example.testingLogIn.Models.Distributable;
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
    public ResponseEntity<List<Distributable>> getDistributable(@RequestParam(defaultValue = "true") boolean isNotDeleted){
        try{
            return new ResponseEntity<>(distributableServices.getAllDistributable(isNotDeleted),HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
}
