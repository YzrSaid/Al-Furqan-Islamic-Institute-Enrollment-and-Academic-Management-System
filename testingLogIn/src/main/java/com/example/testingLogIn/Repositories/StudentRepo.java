package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.Models.Student;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
        "AND Lower(s.fullName) LIKE CONCAT('%',:lastName,'%') "+
        "AND Lower(s.fullName) LIKE CONCAT('%',:firstName,'%') "+
        "AND (:middleName IS NULL OR s.fullName LIKE CONCAT('%',:middleName,'%'))")
boolean existsByNameIgnoreCaseAndNotDeleted(
    @Param("studentId") Integer studentId,
    @Param("firstName") String firstName,
    @Param("lastName") String lastName,
    @Param("middleName") String middleName
);
    List<Student> findByIsNotDeletedTrue();

    Page<Student> findByIsNewTrue(Pageable pageable);//will replace the method below soon
    List<Student> findByIsNotDeletedTrueAndIsNewTrue();
    Page<Student> findByIsNewFalse(Pageable pageable);//will replace the method below soon
    List<Student> findByIsNotDeletedTrueAndIsNewFalse();

    @Query("SELECT s FROM Student s " +
            "WHERE s.isNotDeleted = true " +
            "AND (s.studentDisplayId LIKE CONCAT('%', :search, '%') " +
            "OR s.fullName LIKE CONCAT('%', :search, '%')) ")
    Page<Student> findByStudentDisplayIdOrName(@Param("search")String searching, Pageable pageable);

    List<Student> findByIsNotDeletedTrueAndIsTransfereeTrue();
    
    @Query( "SELECT s from Student s "+
            "WHERE s.isNotDeleted = TRUE "+
            "AND s.fullName LIKE CONCAT('%',:firstName,'%') "+
            "AND s.fullName LIKE CONCAT('%',:lastName,'%') "+
            "AND (:middleName IS NULL OR s.fullName LIKE CONCAT('%',:middleName,'%'))")
    Student findByName(
            @Param("firstName") String firstname,
            @Param("lastName") String lastname,
            @Param("middleName") String middleName);

    @Query("SELECT s FROM Student s " +
            "WHERE s.isNotDeleted = true " +
            "AND (s.studentDisplayId LIKE CONCAT('%', :searching, '%') " +
            "OR LOWER(s.fullName) LIKE CONCAT('%', :searching, '%'))")
    List<Student> findByStudentDisplayIDOrName(@Param("searching") String searching);
    
    @Query("SELECT COUNT(s) from Student s "+
           "WHERE s.studentDisplayId LIKE %:year%")
    Integer findStudentNextId(@Param("year") String year);
}
