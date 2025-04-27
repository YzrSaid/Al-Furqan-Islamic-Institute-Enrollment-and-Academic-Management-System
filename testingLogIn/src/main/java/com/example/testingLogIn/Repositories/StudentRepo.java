package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.CustomObjects.StudentHandler;
import com.example.testingLogIn.Models.Student;

import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author magno
 */
public interface StudentRepo extends JpaRepository<Student,Integer> {
    @Query("SELECT COUNT(s) > 0 FROM Student s " +
        "WHERE s.isNotDeleted = true "+
        "AND (:studentId IS NULL OR s.studentId != :studentId) "+
        "AND Lower(s.fullName) = :fullName")
    boolean existsByNameIgnoreCaseAndNotDeleted(
        @Param("studentId") Integer studentId,
                            String fullName
    );

    @Query("SELECT stud FROM Student stud WHERE LOWER(stud.fullName) LIKE :fullName")
    Optional<Student> findByName(String fullName);

    Page<Student> findByIsNewTrue(Pageable pageable);//will replace the method below soon
    List<Student> findByIsNotDeletedTrueAndIsNewTrue();
    Page<Student> findByIsNewFalse(Pageable pageable);//will replace the method below soon
    List<Student> findByIsNotDeletedTrueAndIsNewFalse();

    @Query("SELECT s FROM Student s " +
            "WHERE (:isFullyPaid IS NULL OR s.studentBalance > 0) " +
            "AND s.isNotDeleted = true " +
            "AND (s.studentDisplayId LIKE :search " +
            "OR LOWER(s.fullName) LIKE :search)")
    Page<Student> findByStudentDisplayIdOrName(@Param("search")String searching, Pageable pageable, Boolean isFullyPaid);

    @Query("""
    SELECT s FROM Student s
    LEFT JOIN FETCH s.currentGradeSection gs
    LEFT JOIN FETCH gs.level l 
    WHERE (:isFullPaid IS NULL OR s.studentBalance > 0)
    AND (s.studentDisplayId LIKE :search 
    OR LOWER(s.fullName) LIKE :search)
    ORDER BY 
        CASE 
            WHEN gs IS NULL THEN 2 
            ELSE 1 
        END,
        l.levelName ASC NULLS LAST
    """)
    Page<Student> findByStudentHandlerDisplayIdOrName(@Param("search")String searching, Pageable pageable, Boolean isFullPaid);

    List<Student> findByIsNotDeletedTrueAndIsTransfereeTrue();
    
    @Query( "SELECT s from Student s "+
            "WHERE s.isNotDeleted = TRUE "+
            "AND s.fullName LIKE CONCAT('%',:firstName,'%') "+
            "AND s.fullName LIKE CONCAT('%',:lastName,'%') "+
            "AND s.fullName LIKE CONCAT('%',:middleName,'%')")
    Student findByName(
            @Param("firstName") String firstname,
            @Param("lastName") String lastname,
            @Param("middleName") String middleName);

    @Query("SELECT s FROM Student s " +
            "WHERE s.isNotDeleted = true " +
            "AND (s.studentDisplayId LIKE CONCAT('%', :searching, '%') " +
            "OR LOWER(s.fullName) LIKE CONCAT('%', :searching, '%'))")
    List<Student> findByStudentDisplayIDOrName(@Param("searching") String searching);

    @Query("SELECT s FROM Student s " +
            "JOIN s.currentGradeSection.level glvl " +
            "WHERE glvl.levelId = :levelId")
    List<Student> findStudentsByCurrentGradeLevel(@Param("levelId") int levelId);
    
    @Query("SELECT COUNT(s) from Student s "+
           "WHERE s.studentDisplayId LIKE %:year%")
    Integer findStudentNextId(@Param("year") String year);

    @Modifying
    @Transactional
    @Query("UPDATE Student stud SET stud.status = 'OLD' WHERE stud.status = 'NEW'")
    void setNewStudentsToOld();
}
