package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.CustomObjects.EnrollmentHandler;
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
    //I changed something in this line by adding this code performance changing comment
@Query("SELECT COUNT(e) > 0 FROM Enrollment e " +
        "JOIN e.student stud "+
        "JOIN e.SYSemester s "+
        "WHERE e.isNotDeleted = true " +
        "AND stud.studentId = :studentId "+
        "AND s.sySemNumber = :activeSemNumber")
boolean studentCurrentlyEnrolled(
    @Param("studentId") int studentId,
    @Param("activeSemNumber") int activeSemNumber);

@Query("SELECT e FROM Enrollment e " +
        "JOIN e.SYSemester s "+
        "WHERE e.isNotDeleted = true " +
        "AND (:status IS NULL OR e.enrollmentStatus = :status) "+
        "AND s.sySemNumber = :activeSemNumber")
List<Enrollment> findRecordsByStatusAndSemester(
    @Param("status") EnrollmentStatus status,
    @Param("activeSemNumber") int activeSemNumber);

    @Query("SELECT e FROM Enrollment e " +
            "JOIN e.student stud "+
            "JOIN e.SYSemester s "+
            "WHERE e.isNotDeleted = true " +
            "AND (:status IS NULL OR e.enrollmentStatus = :status) "+
            "AND s.sySemNumber = :activeSemNumber "+
            "AND (:search IS NULL OR stud.fullName LIKE CONCAT('%',:search,'%') " +
            "OR stud.studentDisplayId LIKE CONCAT('%',:search,'%'))")
    Page<Enrollment> findRecordsByStatusAndSemesterPage(
            @Param("status") EnrollmentStatus status,
            @Param("activeSemNumber") int activeSemNumber,
            @Param("search") String search,
            Pageable pageable);

@Query("SELECT e FROM Enrollment e " +
        "JOIN e.student stud "+
        "JOIN e.SYSemester s "+
        "WHERE e.isNotDeleted = true " +
        "AND s.sySemNumber = :activeSemNumber "+
        "AND stud.studentId = :studentId")
Enrollment getRecordByStudent(
    @Param("studentId") int studentId,
    @Param("activeSemNumber") int activeSemNumber);

@Query("SELECT NEW com.example.testingLogIn.CustomObjects.EnrollmentHandler(e,stud) from Student stud " +
        "LEFT JOIN Enrollment e " +
        "ON e.student.studentId = stud.studentId AND e.SYSemester.sySemNumber = :activeSemNumber "+
        "WHERE (e.isNotDeleted = true OR e.student IS NULL) "+
        "AND (:status IS NULL OR (CASE WHEN e.student IS NULL THEN 'NOT_REGISTERED' ELSE e.enrollmentStatus END) = :status) "+
        "AND (:search IS NULL OR stud.fullName LIKE CONCAT('%',:search,'%') " +
        "OR stud.studentDisplayId LIKE CONCAT('%',:search,'%'))")
Page<EnrollmentHandler> testing(@Param("status")            EnrollmentStatus status,
                                @Param("activeSemNumber")   int activeSemNumber,
                                @Param("search")            String search,
                                                            Pageable pageable);
}
