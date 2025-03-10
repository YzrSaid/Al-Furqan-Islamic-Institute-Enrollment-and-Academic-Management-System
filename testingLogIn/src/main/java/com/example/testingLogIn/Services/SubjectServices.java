package com.example.testingLogIn.Services;

import com.example.testingLogIn.ModelDTO.SectionDTO;
import com.example.testingLogIn.ModelDTO.SubjectDTO;
import com.example.testingLogIn.Models.GradeLevel;
import com.example.testingLogIn.Models.Section;
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

    private final SubjectRepo subjectRepo;
    private final GradeLevelServices gradeLevelService;
    private final SectionServices sectionService;

    @Autowired
    public SubjectServices(SubjectRepo subjectRepo, GradeLevelServices gradeLevelService, SectionServices sectionService) {
        this.subjectRepo = subjectRepo;
        this.gradeLevelService = gradeLevelService;
        this.sectionService = sectionService;
    }

    public List<SubjectDTO> getSubjectByGrade(String gradeLevel){
        return subjectRepo.findAll().stream()
                          .filter(subject ->subject.getGradeLevel().isNotDeleted() && 
                                            subject.getGradeLevel().getLevelName().toLowerCase()
                                                   .equals(gradeLevel.toLowerCase()) &&
                                             subject.isNotDeleted())
                          .map(Subject::mapper)
                          .collect(Collectors.toList());
    }
    
    public List<SubjectDTO> getSectionSubjects(int sectionId){
        SectionDTO sec = sectionService.getSection(sectionId);
        
        return getSubjectByGrade(sec.getGradeLevelName());
    }
    
    public SubjectDTO getSubject(int subjectNumber){
        Subject sub = subjectRepo.findAll().stream()
                                 .filter(subj -> subj.getGradeLevel().isNotDeleted() &&
                                                subj.getSubjectNumber()==subjectNumber &&
                                                subj.isNotDeleted())
                                 .findFirst().orElse(null);
        
        if(sub != null)
            return SubjectToSubjectDTO(sub);
        else
            return null;      
    }
    
    public List<SubjectDTO> getAllSubjects(){
        return subjectRepo.findAll().stream()
                          .filter(subj -> subj.isNotDeleted() &&
                                            subj.getGradeLevel().isNotDeleted())
                          .map(this::SubjectToSubjectDTO)
                          .collect(Collectors.toList());
    }
            
    public boolean addNewSubject(int levelId,String subjectName){
        if(gradeLevelService.getGradeLevel(levelId) == null)
            throw new NullPointerException("Grade Level Not Found");
        
        if(!doesSubjectNameExist(levelId,subjectName)){
            Subject sub=new Subject();
            sub.setNotDeleted(true);
            sub.setGradeLevel(gradeLevelService.getGradeLevel(levelId));
            sub.setSubjectName(subjectName);
            
            subjectRepo.save(sub);
            
            return true;
        }else
            return false;
    }
    
    public Subject getByName(String subjectName){
        return subjectRepo.findAll().stream()
                          .filter(subject -> subject.isNotDeleted() &&
                                             subjectName.equalsIgnoreCase(subject.getSubjectName()))
                          //.map(Subject::mapper)
                            .findFirst().orElse(null);
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
                          .isNotDeleted(subject.isNotDeleted())
                          .build();
     }
    
    private boolean doesSubjectNameExist(int levelId,String subjectName){
        return subjectRepo.findAll().stream()
                          .filter(sub ->sub.isNotDeleted() && 
                                        subjectName.equals(sub.getSubjectName()) &&
                                        sub.getGradeLevel().getLevelId() == levelId)
                          .findFirst().orElse(null) != null;
    }
}
