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
        return new ResponseEntity<>(sectionService.getNoAdvisoryTeachers(), HttpStatus.OK);
    }

    @GetMapping("/students/{sectionId}")
    public ResponseEntity<List<StudentDTO>> getStudentsOfSection(@PathVariable int sectionId){
        return new ResponseEntity<>(sectionService.getEnrolledStudentToSection(sectionId),HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<SectionDTO>> getAllSections(@RequestParam(required = false,defaultValue = "false") boolean willCountStudent,
                                                           @RequestParam(defaultValue = "ENROLLED_COUNT") String sortBy,
                                                           @RequestParam(required = false,defaultValue = "") String q) {
        return new ResponseEntity<>(sectionService.getAllSections(willCountStudent,sortBy,q), HttpStatus.OK);
    }

    @GetMapping("/{sectionNumber}")
    public ResponseEntity<SectionDTO> getSection(@PathVariable int sectionNumber) {
        return new ResponseEntity<>(sectionService.getSection(sectionNumber), HttpStatus.OK);
    }
    @GetMapping("")
    public ResponseEntity<SectionDTO> getSectionByNameDTO(@RequestParam String sectionName) {
        return new ResponseEntity<>(sectionService.getSectionByNameDTO(sectionName.toLowerCase()), HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addNewSection(@RequestBody SectionDTO sectionDTO) {
        switch (sectionService.addSection(sectionDTO)) {
            case 3:
                return new ResponseEntity<>("The Selected Teacher Already Has an Advisory Class", HttpStatus.NOT_ACCEPTABLE);
            default:
                return new ResponseEntity<>("Section Information Added Successfully", HttpStatus.OK);
        }

    }

    @PutMapping("/update")
    public ResponseEntity<String> updateSectionRecord(@RequestBody SectionDTO sectionDTO){
        sectionService.updateSection(sectionDTO);
        return new ResponseEntity<>("Section Record Updated Successfully",HttpStatus.OK);
    }

    @DeleteMapping("/delete/{sectionNumber}")
    public ResponseEntity<String> deleteSectionRecord(@PathVariable int sectionNumber) {
        sectionService.deleteSection(sectionNumber);
        return new ResponseEntity<>("Section Record Deleted Successfully", HttpStatus.OK);
    }

}
