package com.example.testingLogIn.Controllers;

import com.example.testingLogIn.ModelDTO.SectionDTO;
import com.example.testingLogIn.ModelDTO.UserDTO;
import com.example.testingLogIn.Models.Section;
import com.example.testingLogIn.Services.SectionServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author magno
 */
@RequestMapping("/section")
@Controller
public class SectionController {

    @Autowired
    private SectionServices sectionService;

    @GetMapping("/gradeLevel/{gradeLevel}")
    public ResponseEntity<List<SectionDTO>> getSectionByGradeLevel(@PathVariable String gradeLevel) {
        return new ResponseEntity<>(sectionService.getSectionsByLevel(gradeLevel), HttpStatus.OK);
    }

    @GetMapping("/adviserids")
    public ResponseEntity<List<UserDTO>> getAdviserIds() {
        try {
            return new ResponseEntity<>(sectionService.getNoAdvisoryTeachers(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<SectionDTO>> getAllSections() {
        System.out.println("Someone fetched");
        try {
            System.out.println("Sending to someone...");
            return new ResponseEntity<>(sectionService.getAllSections(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/{sectionNumber}")
    public ResponseEntity<SectionDTO> getSection(@PathVariable int sectionNumber) {
        try {
            SectionDTO section = sectionService.getSection(sectionNumber);

            if (section != null)
                return new ResponseEntity<>(section, HttpStatus.FOUND);
            else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
    @GetMapping("/name/{sectionName}")
    public ResponseEntity<SectionDTO> getSectionByNameDTO(@PathVariable String sectionName) {
        return new ResponseEntity<>(sectionService.getSectionByNameDTO(sectionName.toLowerCase()), HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addNewSection(@RequestBody SectionDTO sectionDTO) {
        Map<String, String> response = new HashMap<>();

        try {
            int result = sectionService.addSection(sectionDTO);

            switch (result) {
                case 1:
                    response.put("error", "Teacher Information Not Found");
                    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
                case 2:
                    response.put("error", "Grade Level Information Not Found");
                    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
                case 3:
                    response.put("error", "The Selected Teacher Already Has an Advisory Class");
                    return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
                case 4:
                    response.put("error", "Section Name Already Exists");
                    return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
                default:
                    response.put("message", "Section Information Added Successfully");
                    return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace(); // ✅ This will print the error in your backend console
            response.put("error", "Process Failed: " + e.getMessage()); // ✅ Include the error message
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

    }

    @PutMapping("/update")
    public ResponseEntity<String> updateSectionRecord(@RequestBody SectionDTO sectionDTO){
        System.out.println(sectionDTO.getNumber());
        try{
            if(sectionService.updateSection(sectionDTO))
                return new ResponseEntity<>("Section Record Updated Successfully",HttpStatus.OK);
            else
                return new ResponseEntity<>("Section Record Not Found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace(); // Print the error in logs
            return new ResponseEntity<>("Process Failed", HttpStatus.CONFLICT);
        }
    }

    @DeleteMapping("/delete/{sectionNumber}")
    public ResponseEntity<String> deleteSectionRecord(@PathVariable int sectionNumber) {
        try {
            if (sectionService.deleteSection(sectionNumber))
                return new ResponseEntity<>("Section Record Deleted Successfully", HttpStatus.OK);
            else
                return new ResponseEntity<>("Section Record Not Found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Process Failed", HttpStatus.CONFLICT);
        }
    }

}
