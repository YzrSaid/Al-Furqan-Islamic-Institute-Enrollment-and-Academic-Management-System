package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.Models.Student;
import java.util.List;
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
       "AND (:studentId IS NULL OR s.studentId != :studentId) " +
       "AND Lower(s.firstName) = Lower(:firstName) " +
       "AND Lower(s.lastName) = Lower(:lastName)")
boolean existsByNameIgnoreCaseAndNotDeleted(
    @Param("studentId") Integer studentId,
    @Param("firstName") String firstName,
    @Param("lastName") String lastName
);
    
    @Query("select s from Student s "
            + "WHERE s.isNotDeleted = true")
    List<Student> findRegisteredStudents();
    
    @Query("SELECT s from Student s "+
           "WHERE s.isNotDeleted = true "+
           "AND isNew = true")
    List<Student> findNewStudents();
    
    @Query("SELECT s from Student s "+
           "WHERE s.isNotDeleted = true "+
           "AND isNew = false")
    List<Student> findOldStudents();
    
    @Query("SELECT s from Student s "+
           "WHERE s.isNotDeleted = true "+
           "AND s.isTransferee = true")
    List<Student> findTransfereeStudents();
    
    @Query( "SELECT s from Student s "+
            "WHERE s.isNotDeleted = true "+
            "AND Lower(s.firstName) = Lower(:firstName) " +
            "AND Lower(s.lastName) = Lower(:lastName)")
    Student findByName(
            @Param("firstName") String firstname,
            @Param("lastName") String lastname);
    
    @Query("SELECT s FROM Student s " +
            "WHERE s.isNotDeleted = true " +
            "AND s.studentDisplayId = :displayId")
    Student findByStudentDisplayId(@Param("displayId") String displayId);
    
    @Query("SELECT COUNT(s) from Student s "+
           "WHERE s.studentDisplayId LIKE %:year%")
    Integer findStudentNextId(@Param("year") String year);
    
    
}
