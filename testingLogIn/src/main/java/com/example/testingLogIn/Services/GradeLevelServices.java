package com.example.testingLogIn.Services;

import com.example.testingLogIn.ModelDTO.GradeLevelDTO;
import com.example.testingLogIn.Models.GradeLevel;
import com.example.testingLogIn.Repositories.GradeLevelRepo;
import java.util.ArrayList;
import java.util.HashSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GradeLevelServices {

    @Autowired
    GradeLevelRepo gradeLevelRepo;

    public boolean addNewGradeLevel(String levelName, String preRequisite) {
        GradeLevel pre = null;
        if (!preRequisite.equalsIgnoreCase("NONE")) {
            pre = getByName(preRequisite);
            if (pre == null)
                throw new NullPointerException();
        }

        if (getByName(levelName) == null) {
            GradeLevel newGrade = new GradeLevel();
            newGrade.setNotDeleted(true);
            newGrade.setLevelName(levelName);
            newGrade.setPreRequisite(pre);
            gradeLevelRepo.save(newGrade);
            return true;
        } else
            return false;
    }

    public List<GradeLevelDTO> getAllGradeLevelsDTO() {
        return gradeLevelRepo.findAll().stream()
                .filter(GradeLevel::isNotDeleted)
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
        return gradeLevelRepo.findAll().stream()
                .filter(gradelvl -> gradelvl.getLevelId() == levelId && gradelvl.isNotDeleted())
                .map(GradeLevel::mapperDTO)
                .findFirst().orElse(null);
    }
    public GradeLevel getGradeLevel(int levelId) {
        return gradeLevelRepo.findAll().stream()
                .filter(gradelvl -> gradelvl.getLevelId() == levelId && gradelvl.isNotDeleted())
                .findFirst().orElse(null);
    }

    public boolean updateGradeLevel(GradeLevelDTO gradeLevel) {
        String preReqWord = gradeLevel.getPreRequisite();
        GradeLevel newPreRequisite = preReqWord.equalsIgnoreCase("NONE") ? null
                : gradeLevelRepo.findById(Integer.valueOf(preReqWord)).orElse(null);

        GradeLevel updated = gradeLevelRepo.findById(gradeLevel.getLevelId()).orElse(null);
        if (updated != null) {
            updated.setLevelName(gradeLevel.getLevelName());
            updated.setPreRequisite(newPreRequisite);
            gradeLevelRepo.save(updated);
            return true;
        } else
            return false;
    }

    public boolean deleteGradeLevel(int levelId) {
        GradeLevel todelete = gradeLevelRepo.findAll().stream()
                .filter(gradeL -> gradeL.isNotDeleted() &&
                        gradeL.getLevelId() == levelId)
                .findFirst().orElse(null);

        if (todelete != null) {
            todelete.setNotDeleted(false);
            gradeLevelRepo.save(todelete);
            return true;
        } else
            return false;
    }

    public GradeLevel getByName(String levelname) {
        return gradeLevelRepo.findAll().stream()
                .filter(gradeLevel -> gradeLevel.isNotDeleted() &&
                        gradeLevel.getLevelName().equalsIgnoreCase(levelname))
                .findFirst().orElse(null);
    }
}
