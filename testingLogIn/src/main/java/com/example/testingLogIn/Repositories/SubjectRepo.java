/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.Models.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author magno
 */
public interface SubjectRepo extends JpaRepository<Subject, Integer>{
    @Query("SELECT sub FROM Subject sub " +
            "WHERE sub.isNotDeleted " +
            "AND LOWER(sub.subjectName) = :newSubName " +
            "AND sub.subjectNumber != :subId")
    List<Subject> findByNameNotEqualId(String newSubName, int subId);

    @Transactional
    @Modifying
    @Query("UPDATE Subject sub SET sub.isCurrentlyActive = TRUE")
    void activeAll();

    @Query("""
           SELECT sub FROM Subject sub
           LEFT JOIN sub.gradeLevel gl
           WHERE gl.levelId = :levelId
           AND sub.isNotDeleted
           AND sub.isCurrentlyActive
            """)
    List<Subject> findActiveSubjectsNotDeletedByGradeLevel(int levelId);
}
