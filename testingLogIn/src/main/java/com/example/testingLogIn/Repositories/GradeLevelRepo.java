package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.Models.GradeLevel;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeLevelRepo extends JpaRepository<GradeLevel,Integer> {
    List<GradeLevel> findByPreRequisiteIsNullAndIsNotDeletedTrue();
}
