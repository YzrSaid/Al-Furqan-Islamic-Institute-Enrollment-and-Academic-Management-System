package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.CustomObjects.EnrollmentHandler;
import com.example.testingLogIn.Enums.EnrollmentStatus;
import com.example.testingLogIn.Enums.Semester;
import com.example.testingLogIn.Models.Enrollment;
import java.util.List;
import java.util.Optional;

import com.example.testingLogIn.Models.GradeLevel;
import com.example.testingLogIn.Models.Student;
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
            "AND s.sySemNumber = :activeSemNumber "+
            "AND stud.studentId = :studentId")
    Enrollment getRecordByStudent(
        @Param("studentId") int studentId,
        @Param("activeSemNumber") int activeSemNumber);

    @Query("""
            SELECT NEW com.example.testingLogIn.CustomObjects.EnrollmentHandler(e,stud) from Student stud
            LEFT JOIN Enrollment e
            ON e.student.studentId = stud.studentId
            AND (stud.status = 'OLD' OR stud.status = 'NEW')
            AND e.SYSemester.sySemNumber = :activeSemNumber
            WHERE (:status IS NULL OR
                (CASE
                WHEN e.student IS NULL THEN 'NOT_REGISTERED'
                WHEN e.isNotDeleted = FALSE THEN 'CANCELLED'
                ELSE e.enrollmentStatus END) = :status)
            AND (CASE
                WHEN e.student IS NULL THEN 'NOT_REGISTERED'
                WHEN e.isNotDeleted = FALSE THEN 'CANCELLED'
                ELSE e.enrollmentStatus END) != 'ENROLLED'
            AND (:search IS NULL OR stud.fullName LIKE CONCAT('%',:search,'%')
            OR stud.studentDisplayId LIKE CONCAT('%',:search,'%'))
            AND stud.status != 'GRADUATE'
            """)
    Page<EnrollmentHandler> findStudentsEnrollment(@Param("status") EnrollmentStatus status,
                                                   @Param("activeSemNumber")   int activeSemNumber,
                                                   @Param("search")            String search,
                                                   Pageable pageable);


    @Query("""
           SELECT e FROM Enrollment e
           JOIN e.student stud
           WHERE e.enrollmentStatus = 'ENROLLED'
           AND e.SYSemester.sySemNumber = :activeSemNumber
           AND (LOWER(stud.fullName) LIKE :search
           OR stud.studentDisplayId LIKE :search)
           """)
    Page<Enrollment> findEnrolledStatus(int activeSemNumber, String search, Pageable pageable);

    @Query("SELECT e.student FROM Enrollment e " +
            "JOIN e.SYSemester sem " +
            "JOIN e.sectionToEnroll sec " +
            "WHERE sem.sySemNumber = :semId " +
            "AND sec.number = :sectionId " +
            "AND e.enrollmentStatus = 'ENROLLED'")
    List<Student> countCurrentlyEnrolled(@Param("sectionId") int sectionId,
                                         @Param("semId") int semId);

    @Query("SELECT e.student FROM Enrollment e " +
            "JOIN e.SYSemester sem " +
            "JOIN e.gradeLevelToEnroll grl " +
            "WHERE sem.sySemNumber = :semId " +
            "AND grl.levelId = :gradeId " +
            "AND e.enrollmentStatus = 'ENROLLED'")
    List<Student> getCurrentlyEnrolledToGrade(@Param("gradeId") int gradeId,
                                         @Param("semId") int semId);

    @Query("""
            SELECT COUNT(e) FROM Enrollment e
            LEFT JOIN e.gradeLevelToEnroll gl
            LEFT JOIN e.sectionToEnroll sec
            JOIN e.SYSemester sem
            WHERE sem.sySemNumber = :semId
            AND e.isNotDeleted
            AND (:levelId IS NULL OR gl.levelId = :levelId)
            AND (:sectionId IS NULL OR sec.number = :sectionId)
            """)
    Optional<Long> countGradeLevelAndSection(int semId, Integer levelId, Integer sectionId);

    @Query("""
           SELECT COUNT(e) FROM Enrollment e
           JOIN e.SYSemester sem
           JOIN e.sectionToEnroll section
           WHERE sem.sySemNumber = :semId
           AND section.number = :sectionId
           """)
    Optional<Integer> countSectionTransaction(int sectionId, int semId);

    @Query("""
           SELECT e FROM Enrollment e
           JOIN e.student stud
           JOIN e.SYSemester sem
           WHERE (:sy IS NULL OR sem.schoolYear.schoolYearNum = :sy)
           AND (:status IS NULL OR e.enrollmentStatus = :status)
           AND (:sem IS NULL OR sem.sem = :sem)
           AND (LOWER(stud.fullName) LIKE :search
           OR stud.studentDisplayId LIKE :search)
           """)
    Page<Enrollment> getEnrollmentPageStatistics(String search, EnrollmentStatus status,Integer sy, Semester sem, Pageable pageable);

    @Query("""
           SELECT COUNT(e) FROM Enrollment e
           JOIN e.SYSemester sem
           WHERE (e.enrollmentStatus = 'PAYMENT'
           OR e.enrollmentStatus = 'ENROLLED')
           AND sem.sySemNumber = :sem
           AND e.sectionToEnroll.number = :sectionId
           """)
    Optional<Integer> countSectionTransact(int sectionId, int sem);

    @Query("""
           SELECT COUNT(e) FROM Enrollment e
           JOIN e.SYSemester sem
           JOIN e.gradeLevelToEnroll gl
           WHERE sem.sySemNumber = :sem
           AND (:sectionId IS NULL OR e.sectionToEnroll.number = :sectionId)
           AND (:levelId IS NULL OR gl.levelId = :levelId)
           """)
    Optional<Integer> countSectionGradeEnrollment(Integer sectionId, int sem, Integer levelId);

    @Query("""
           SELECT e FROM Enrollment e
           JOIN e.SYSemester sem
           WHERE sem.sySemNumber = :semId
           AND e.enrollmentStatus = 'ENROLLED'
           """)
    List<Enrollment> findBySchoolYear(int semId);

    @Query("""
           SELECT gl FROM Enrollment e
           JOIN e.gradeLevelToEnroll gl
           WHERE e.enrollmentStatus = 'ENROLLED'
           GROUP BY gl
           """)
    List<GradeLevel> uniqueGradeLevelsEnrolled(int semId);
}
