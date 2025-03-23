package com.example.testingLogIn.Controllers;

import com.example.testingLogIn.ModelDTO.SectionDTO;
import com.example.testingLogIn.ModelDTO.StudentDTO;
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

    @GetMapping("/students/{sectionId}")
    public ResponseEntity<List<StudentDTO>> getStudentsOfSection(@PathVariable int sectionId){
        try{
            return new ResponseEntity<>(sectionService.getEnrolledStudentToSection(sectionId),HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<SectionDTO>> getAllSections(@RequestParam(required = false,defaultValue = "false") boolean willCountStudent,
                                                           @RequestParam(defaultValue = "ENROLLED_COUNT") String sortBy,
                                                           @RequestParam(required = false,defaultValue = "") String q) {
        try {
            return new ResponseEntity<>(sectionService.getAllSections(willCountStudent,sortBy,q), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/{sectionNumber}")
    public ResponseEntity<SectionDTO> getSection(@PathVariable int sectionNumber) {
        try {
            SectionDTO section = sectionService.getSection(sectionNumber);
            if (section != null)
                return new ResponseEntity<>(section, HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
    @GetMapping("")
    public ResponseEntity<SectionDTO> getSectionByNameDTO(@RequestParam String sectionName) {
        if(sectionName == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(sectionService.getSectionByNameDTO(sectionName.toLowerCase()), HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addNewSection(@RequestBody SectionDTO sectionDTO) {
        Map<String, String> response = new HashMap<>();
        try {
            int result = sectionService.addSection(sectionDTO);
            System.out.println(result);
            switch (result) {
                case 1:
                    return new ResponseEntity<>("Teacher Information Not Found", HttpStatus.NOT_FOUND);
                case 2:
                    return new ResponseEntity<>("Grade Level Information Not Found", HttpStatus.NOT_FOUND);
                case 3:
                    return new ResponseEntity<>("The Selected Teacher Already Has an Advisory Class", HttpStatus.NOT_ACCEPTABLE);
                case 4:
                    return new ResponseEntity<>("Section Name Already Exists", HttpStatus.NOT_ACCEPTABLE);
                default:
                    return new ResponseEntity<>("Section Information Added Successfully", HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace(); // ✅ This will print the error in your backend console
            return new ResponseEntity<>("Process Failed: " + e.getMessage(), HttpStatus.CONFLICT);
        }

    }

    @PutMapping("/update")
    public ResponseEntity<String> updateSectionRecord(@RequestBody SectionDTO sectionDTO){
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
