package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.AssociativeModels.StudentFeesList;
import com.example.testingLogIn.Models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.query.Param;

@Repository
public interface StudentFeesListRepo extends JpaRepository<StudentFeesList,Integer> {

    @Query("SELECT sfl FROM StudentFeesList sfl " +
            "JOIN sfl.student stud "+
            "JOIN sfl.sem s "+
            "WHERE (:semId IS NULL OR s.sySemNumber = :semId) "+
            "AND stud.studentId = :studId " +
            "GROUP BY sfl.fee")
    List<StudentFeesList> findBySem(@Param("studId") int studentId,@Param("semId") Integer semId);

    @Query("SELECT sfl FROM StudentFeesList sfl " +
            "JOIN sfl.student stud "+
            "JOIN sfl.fee fee "+
            "WHERE stud.studentId = :studId " +
            "GROUP BY fee")
    List<StudentFeesList> findUniqueFees(@Param("studId") int studentId);

    @Query("SELECT COUNT(sfl) FROM StudentFeesList sfl "+
            "JOIN sfl.student stud "+
            "WHERE sfl.fee.id =:feeId " +
            "AND stud.studentId = :studentId")
    Integer feesCount(@Param("studentId") int studentId, @Param("feeId") int feeId);
    
    @Query("SELECT SUM(sfl.amount) FROM StudentFeesList sfl " +
            "JOIN sfl.student stud "+
            "JOIN sfl.fee fee "+
            "WHERE fee.id =:feeId " +
            "AND stud.studentId = :studentId")
    Double totalPerFeesByStudent(@Param("studentId") int studentId, @Param("feeId") int feeId);
    
    @Query("SELECT sfl FROM StudentFeesList sfl " +
            "JOIN sfl.student stud "+
            "JOIN sfl.sem s "+
            "JOIN sfl.fee fee "+
            "WHERE fee.id = :feeId " +
            "AND s.sySemNumber = :semId "+
            "AND stud.studentId = :studentId")
    Optional<StudentFeesList> getFeesBySem(@Param("studentId") int studentId, @Param("feeId") int feeId,@Param("semId") int semId);

    @Query("SELECT sfl FROM StudentFeesList sfl " +
            "JOIN sfl.sem s "+
            "JOIN sfl.fee fee "+
            "WHERE fee.id = :feeId " +
            "AND s.sySemNumber = :semId ")
    List<StudentFeesList> getFeesBySemAnyStudent(@Param("feeId") int feeId,@Param("semId") int semId);

    @Query("SELECT sfl FROM StudentFeesList sfl " +
            "JOIN sfl.student stud "+
            "JOIN sfl.sem s "+
            "WHERE stud.studentId = :studentId " +
            "AND s.sySemNumber = :semId ")
    List<StudentFeesList> getFeesBySemAndStudent(@Param("studentId") int studentId,@Param("semId") int semId);

    @Query("""
           SELECT sfl.student FROM StudentFeesList sfl
           JOIN sfl.sem s
           JOIN sfl.fee fee
           WHERE fee.id = :feeId
           AND s.sySemNumber = :semId
           """)
    List<Student> studentsWithThisFee(int feeId, int semId);

    @Query("""
            SELECT stud FROM Student stud
            JOIN Enrollment e ON e.student.studentId = stud.studentId
                AND e.gradeLevelToEnroll.levelId = :levelId
                AND e.enrollmentStatus = 'ENROLLED'
                AND e.SYSemester.sySemNumber = :semId
            LEFT JOIN StudentFeesList sfl ON sfl.student.studentId = stud.studentId
                AND sfl.fee.id = :feeId
                AND sfl.sem.sySemNumber = :semId
            WHERE sfl.student IS NOT NULL
            """)
    List<Student> findEnrolledStudentsWithRecord(int levelId, int semId, int feeId);

    @Query("""
            SELECT stud FROM Student stud
            JOIN Enrollment e ON e.student.studentId = stud.studentId
                AND e.gradeLevelToEnroll.levelId = :levelId
                AND e.enrollmentStatus = 'ENROLLED'
                AND e.SYSemester.sySemNumber = :semId
            LEFT JOIN StudentFeesList sfl ON sfl.student.studentId = stud.studentId
                AND sfl.fee.id = :feeId
                AND sfl.sem.sySemNumber = :semId
            WHERE sfl.student IS NULL
            """)
    List<Student> findEnrolledStudentsNoRecord(int levelId, int semId, int feeId);
}
