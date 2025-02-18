package com.example.testingLogIn.Services;

import com.example.testingLogIn.ModelDTO.SubjectDTO;
import com.example.testingLogIn.Models.GradeLevel;
import com.example.testingLogIn.Models.Subject;
import com.example.testingLogIn.Repositories.SubjectRepo;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author magno
 */
@Service
public class SubjectServices {
    
    @Autowired
    private SubjectRepo subjectRepo;
    @Autowired
    private GradeLevelServices gradeLevelService;
    
    public SubjectDTO getSubject(int subjectNumber){
        Subject sub = subjectRepo.findById(subjectNumber).orElse(null);
        if(sub != null)
            return SubjectToSubjectDTO(sub);
        else
            return null;
                    
    }
    
    public List<SubjectDTO> getAllSubjects(){
        return subjectRepo.findAll().stream()
                          .filter(Subject::isNotDeleted)
                          .map(subject -> SubjectToSubjectDTO(subject))
                          .collect(Collectors.toList());
    }
            
    public boolean addNewSubject(int levelId,String subjectname){
        
        if(!doesSubjectNameExist(subjectname)){
            Subject sub=new Subject();
            sub.setNotDeleted(true);
            sub.setGradeLevel(gradeLevelService.getGradeLevel(levelId));
            
            subjectRepo.save(sub);
            
            return true;
        }else
            return false;
    }
    
    public List<SubjectDTO> getSubjects(){
         return subjectRepo.findAll().stream()
                           .filter(Subject::isNotDeleted)
                           .map(subject -> SubjectToSubjectDTO(subject))
                           .collect(Collectors.toList());
     }
    
    public boolean updateSubjectDescription(SubjectDTO subject){
        
        Subject updatedSub = subjectRepo.findAll().stream()
                                        .filter(sub -> sub.isNotDeleted() && 
                                                sub.getSubjectNumber() == subject.getSubjectNumber())
                                        .findFirst().orElse(null);
        if(updatedSub != null){
            updatedSub.setGradeLevel(gradeLevelService.getByName(subject.getGradeLevel()));
            updatedSub.setSubjectName(subject.getSubjectName());
            subjectRepo.save(updatedSub);
            return true;
        }
        
        return false;
    }
    
    public boolean deleteSubject(int subjectNumber){
        Subject todelete = subjectRepo.findById(subjectNumber).orElse(null);
        
        if(todelete != null){
            todelete.setNotDeleted(false);
            subjectRepo.save(todelete);
            return true;
        }else
            return false;
    }
     
    private SubjectDTO SubjectToSubjectDTO(Subject subject){
         return SubjectDTO.builder()
                          .subjectNumber(subject.getSubjectNumber())
                          .gradeLevel(subject.getGradeLevel().getLevelName())
                          .subjectName(subject.getSubjectName())
                          .build();
     }
    
    private boolean doesSubjectNameExist(String subjectName){
        return subjectRepo.findAll().stream()
                          .filter(sub -> sub.getSubjectName().equals(subjectName) &&
                                  sub.isNotDeleted())
                          .findFirst().orElse(null) != null;
    }
}
