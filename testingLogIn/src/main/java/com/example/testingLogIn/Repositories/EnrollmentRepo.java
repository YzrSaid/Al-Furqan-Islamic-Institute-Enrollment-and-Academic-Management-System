package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.Enums.EnrollmentStatus;
import com.example.testingLogIn.Models.Enrollment;
import com.example.testingLogIn.Models.SchoolYearSemester;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author magno
 */
@Repository
public interface EnrollmentRepo extends JpaRepository<Enrollment, Integer> {
    
@Query("SELECT COUNT(e) > 0 FROM Enrollment e " +
       "WHERE e.isNotDeleted = true " +
       "AND e.student.studentId = :studentId "+
       "AND e.SYSemester.sySemNumber = :activeSemNumber")
boolean studentCurrentlyEnrolled(
    @Param("studentId") int studentId,
    @Param("activeSemNumber") int activeSemNumber);

@Query("SELECT e FROM Enrollment e " +
       "WHERE e.isNotDeleted = true " +
       "AND (:status IS NULL OR e.enrollmentStatus = :status) "+
       "AND e.SYSemester.sySemNumber = :activeSemNumber")
List<Enrollment> findRecordsByStatusAndSemester(
    @Param("status") EnrollmentStatus status,
    @Param("activeSemNumber") int activeSemNumber);

    @Query("SELECT e FROM Enrollment e " +
            "WHERE e.isNotDeleted = true " +
            "AND (:status IS NULL OR e.enrollmentStatus = :status) "+
            "AND e.SYSemester.sySemNumber = :activeSemNumber")
    Page<Enrollment> findRecordsByStatusAndSemesterPage(
            @Param("status") EnrollmentStatus status,
            @Param("activeSemNumber") int activeSemNumber,
            Pageable pageable);

@Query("SELECT e FROM Enrollment e " +
       "WHERE e.isNotDeleted = true " +
       "AND e.student.studentId = :studentId "+
       "AND e.SYSemester.sySemNumber = :activeSemNumber")
Enrollment getRecordByStudent(
    @Param("studentId") int studentId,
    @Param("activeSemNumber") int activeSemNumber);
}
