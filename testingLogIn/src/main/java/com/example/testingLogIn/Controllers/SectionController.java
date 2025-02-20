package com.example.testingLogIn.Controllers;

import com.example.testingLogIn.ModelDTO.SectionDTO;
import com.example.testingLogIn.ModelDTO.TeacherDTO;
import com.example.testingLogIn.Services.SectionServices;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public ResponseEntity<List<SectionDTO>> getSectionByGradeLevel(@PathVariable String gradeLevel){
        return new ResponseEntity<>(sectionService.getSectionsByLevel(gradeLevel),HttpStatus.OK);
    }
    
    @GetMapping("/adviserids")
    public ResponseEntity<List<TeacherDTO>> getAdviserIds(){
        try{
            return new ResponseEntity<>(sectionService.getNoAdvisoryTeachers(),HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<SectionDTO>> getAllSections(){
        try{
            return new ResponseEntity<>(sectionService.getAllSections(),HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
    
    @GetMapping("/{sectionNumber}")
    public ResponseEntity<SectionDTO> getSection(@PathVariable int sectionNumber){
        try{
            SectionDTO section = sectionService.getSection(sectionNumber);

            if(section != null)
                return new ResponseEntity<>(section,HttpStatus.FOUND);
            else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
    
    @PostMapping("/add")
    public ResponseEntity<String> addNewSection(@RequestBody SectionDTO sectionDTO){
        try{
            int result = sectionService.addSection(sectionDTO);

            switch (result) {
                case 1:
                    return new ResponseEntity<>("Teacher Information Not Found",HttpStatus.NOT_FOUND);
                case 2:
                    return new ResponseEntity<>("Grade Level Information Not Found",HttpStatus.NOT_FOUND);
                case 3:
                    return new ResponseEntity<>("The Selected Teacher Already Have an Advisory Class",HttpStatus.NOT_ACCEPTABLE);
                case 4:
                    return new ResponseEntity<>("Section Name Already Exist",HttpStatus.NOT_ACCEPTABLE);
                default:
                    return new ResponseEntity<>("Section Information Added Successfully",HttpStatus.OK);
            }
        }catch(Exception e){
            return new ResponseEntity<>("Process Failed",HttpStatus.CONFLICT);
        }
    }
    
    @PutMapping("/update")
    public ResponseEntity<String> updateSectionRecord(@PathVariable SectionDTO sectionDTO){
        try{
            if(sectionService.updateSection(sectionDTO))
                return new ResponseEntity<>("Section Record Updated Successfully",HttpStatus.OK);
            else
                return new ResponseEntity<>("Section Record Not Found",HttpStatus.NOT_FOUND);
        }catch(Exception e){
            return new ResponseEntity<>("Process Failed",HttpStatus.CONFLICT);
        }
    }
    
    @DeleteMapping("/delete/{sectionNumber}")
    public ResponseEntity<String> deleteSectionRecord(@PathVariable int sectionNumber){
        try{
            if(sectionService.deleteSection(sectionNumber))
                return new ResponseEntity<>("Section Record Deleted Successfully",HttpStatus.OK);
            else
                return new ResponseEntity<>("Section Record Not Found",HttpStatus.NOT_FOUND);
        }catch(Exception e){
            return new ResponseEntity<>("Process Failed",HttpStatus.CONFLICT);
        }
    }
        
}
