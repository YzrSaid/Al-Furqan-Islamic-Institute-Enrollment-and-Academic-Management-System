package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.Models.GradeLevel;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeLevelRepo extends JpaRepository<GradeLevel,Integer> {
    List<GradeLevel> findByPreRequisiteIsNullAndIsNotDeletedTrue();
    
    @Query("SELECT gl FROM GradeLevel gl  "+
            "WHERE gl.isNotDeleted = TRUE "+
           "AND (gl.preRequisite.levelId = :levelId "+
           "OR gl.levelId = :levelId)")
    List<GradeLevel> findSuccessors(@Param("levelId") int levelId);
}
