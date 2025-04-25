package com.example.testingLogIn.Services;

import com.example.testingLogIn.ModelDTO.GradeLevelDTO;
import com.example.testingLogIn.ModelDTO.StudentDTO;
import com.example.testingLogIn.Models.GradeLevel;
import com.example.testingLogIn.Models.SchoolYearSemester;
import com.example.testingLogIn.Repositories.EnrollmentRepo;
import com.example.testingLogIn.Repositories.GradeLevelRepo;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class GradeLevelServices {

    @Autowired
    private GradeLevelRepo gradeLevelRepo;
    @Autowired
    private sySemesterServices semesterServices;
    @Autowired
    private EnrollmentRepo enrollmentRepo;

    public void addNewGradeLevel(GradeLevelDTO newLvl) {
        GradeLevel pre = null;
        if (!newLvl.getPreRequisite().equalsIgnoreCase("NONE")) {
            pre = getByName(newLvl.getPreRequisite());
            if (pre == null)
                throw new NullPointerException();
        }
        if (getByNameNew(newLvl.getLevelName()) != null )
            throw new IllegalArgumentException("Grade level name already exists");

        GradeLevel newGrade = new GradeLevel();
        newGrade.setNotDeleted(true);
        newGrade.setLevelName(newLvl.getLevelName());
        newGrade.setPreRequisite(pre);
        newGrade.setDuration(newLvl.getSemDuration());
        gradeLevelRepo.save(newGrade);
    }

    public List<GradeLevelDTO> getAllGradeLevelsDTO() {
        return gradeLevelRepo.findByIsNotDeletedTrue().stream()
                .map(GradeLevel::mapperDTO)
                .collect(Collectors.toList());
    }

    public List<GradeLevel> getAllGradeLevels() {
        return gradeLevelRepo.findAll().stream()
                .filter(GradeLevel::isNotDeleted)
                .collect(Collectors.toList());
    }

    public List<GradeLevel> getGradeLevelNoPreRequisite(){
        return gradeLevelRepo.findByPreRequisiteIsNullAndIsNotDeletedTrue();
    }
    public List<GradeLevel> getGradeLevelSuccessor(int levelId){
        return gradeLevelRepo.findSuccessors(levelId);
    }

    public List<GradeLevel> getNoSuccessorsGradeLevels() {
        Set<Integer> levelIdSuccessors = new HashSet<>();
        getAllGradeLevels()
                .forEach(gradeLevel -> {
                    if (gradeLevel.getPreRequisite() != null)
                        levelIdSuccessors.add(gradeLevel.getPreRequisite().getLevelId());
                });

        return getAllGradeLevels().stream()
                .filter(gradeLevel -> !levelIdSuccessors.contains(gradeLevel.getLevelId()))
                .toList();
    }

    public GradeLevelDTO getGradeLevelDTO(int levelId) {
        return gradeLevelRepo.findById(levelId).map(GradeLevel::mapperDTO)
                .orElseThrow(()->new NullPointerException("Grade Level not found"));
    }
    public GradeLevel getGradeLevel(int levelId) {
        return gradeLevelRepo.findByIdNotDeleted(levelId)
                .orElseThrow(()->new NullPointerException("Grade Level not found"));
    }

    @CacheEvict(value = "enrollmentPage",allEntries = true)
    public void updateGradeLevel(GradeLevelDTO gradeLevel) {
        String preReqWord = gradeLevel.getPreRequisite();
        GradeLevel newPreRequisite = preReqWord.equalsIgnoreCase("NONE") ? null
                : gradeLevelRepo.findById(Integer.valueOf(preReqWord)).orElse(null);

        GradeLevel updated = gradeLevelRepo.findByIdNotDeleted(gradeLevel.getLevelId())
                .orElseThrow(()->new NullPointerException("Grade Level not found"));
            updated.setLevelName(gradeLevel.getLevelName());
            updated.setPreRequisite(newPreRequisite);
            updated.setDuration(gradeLevel.getSemDuration());
            gradeLevelRepo.save(updated);
    }

    public void deleteGradeLevel(int levelId) {
        GradeLevel todelete = gradeLevelRepo.findByIdNotDeleted(levelId)
                .orElseThrow(()->new NullPointerException(("Grade Level not found")));

        int semId = Optional.ofNullable(semesterServices.getCurrentActive())
                .map(SchoolYearSemester::getSySemNumber).orElse(0);

        if(enrollmentRepo.countGradeLevelAndSection(semId,levelId,null).orElse(0L) > 0L)
            throw new IllegalArgumentException("Deletion not allowed: This grade level is part of an active enrollment transaction");

        todelete.setNotDeleted(false);
        gradeLevelRepo.save(todelete);
    }

    public List<GradeLevelDTO> preRequisiteOfPreRequisite(int gradeLevelId){
        List<GradeLevelDTO> toReturn = new ArrayList<>();//gradeLevel will be the starting point
        GradeLevel gradeLevel = gradeLevelRepo.findById(gradeLevelId).orElseThrow(NullPointerException::new);
        gradeLevel = gradeLevelRepo.findSuccessorOnly(gradeLevel.getLevelId()).orElse(gradeLevel);

        toReturn.add(gradeLevel.mapperDTO());
        while (true){
            if(gradeLevel.getPreRequisite() != null)
                gradeLevel = gradeLevel.getPreRequisite();
            else
                break;

            toReturn.add(gradeLevel.mapperDTO());
        }
        return toReturn;
    }

    public GradeLevel getByNameNew(String levelname) {
        return gradeLevelRepo.findAll().stream()
                .filter(gradeLevel -> gradeLevel.isNotDeleted() &&
                        gradeLevel.getLevelName().equalsIgnoreCase(levelname))
                .findFirst().orElse(null);
    }

    public GradeLevel getByName(String levelname) {
        return gradeLevelRepo.findAll().stream()
                .filter(gradeLevel -> gradeLevel.isNotDeleted() &&
                        gradeLevel.getLevelName().equalsIgnoreCase(levelname))
                .findFirst().orElseThrow(NullPointerException::new);
    }
}
