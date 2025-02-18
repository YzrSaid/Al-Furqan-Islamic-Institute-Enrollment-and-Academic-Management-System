package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.Models.GradeLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeLevelRepo extends JpaRepository<GradeLevel,String> {
}
