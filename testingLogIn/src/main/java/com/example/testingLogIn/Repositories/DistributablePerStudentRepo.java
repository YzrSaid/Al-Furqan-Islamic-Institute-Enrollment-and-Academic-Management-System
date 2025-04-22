package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.AssociativeModels.DistributablesPerStudent;
import com.example.testingLogIn.Models.Student;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistributablePerStudentRepo extends JpaRepository<DistributablesPerStudent,Integer> {
    @Query("SELECT d FROM DistributablesPerStudent d " +
            "JOIN d.student stud " +
            "WHERE (:isClaimed IS NULL OR d.isReceived = :isClaimed) " +
            "AND (:itemId IS NULL OR d.item.item.itemId = :itemId) " +
            "AND LOWER(stud.fullName) LIKE :student")
    Page<DistributablesPerStudent> getStudentDistPage(String student,Boolean isClaimed,Integer itemId,Pageable pageable);

    @Transactional
    @Modifying
    @Query("""
            UPDATE DistributablesPerStudent dps SET dps.isNotDeleted = :value
            WHERE dps.item.id = :dpgId
            AND dps.sem.sySemNumber = :semId
            """)
    void updateStudentToReceive(int dpgId, int semId, boolean value);

    @Query(//Goal : find the students that are enrolled to the specific grade level where the item to distribute is still not implemented
            """
                SELECT stud FROM Student stud
                JOIN Enrollment e ON e.student.studentId = stud.studentId
                    AND e.gradeLevelToEnroll.levelId = :levelId
                    AND e.enrollmentStatus = 'ENROLLED'
                    AND e.SYSemester.sySemNumber = :semId
                LEFT JOIN DistributablesPerStudent dps ON dps.student.studentId = stud.studentId
                    AND dps.item.id = :itemId
                    AND dps.sem.sySemNumber = :semId
                WHERE dps.student IS NULL
            """)
    List<Student> findEnrolledStudentsNoRecord(int levelId, int semId, int itemId);

    @Modifying
    @Transactional
    @Query("""
           UPDATE DistributablesPerStudent dps
           SET dps.isNotDeleted = false
           WHERE dps.item.id = :itemId
           AND dps.sem.sySemNumber = :semId
           """)
    void setAsDeleted(int itemId, int semId);
}