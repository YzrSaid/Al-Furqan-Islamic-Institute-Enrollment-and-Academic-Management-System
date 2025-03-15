package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.Models.SchoolYearSemester;
import com.example.testingLogIn.Models.StudentFeesList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.query.Param;

@Repository
public interface StudentFeesListRepo extends JpaRepository<StudentFeesList,Integer> {

    @Query("SELECT sfl FROM StudentFeesList sfl WHERE sfl.student.studentId = :studId AND(:semId IS NULL OR sfl.sem.sySemNumber = :semId)")
    List<StudentFeesList> findBySem(@Param("studId") int studentId,@Param("semId") Integer semId);

    @Query("SELECT sfl FROM StudentFeesList sfl WHERE sfl.student.studentId = :studId GROUP BY sfl.fee")
    List<StudentFeesList> findUniqueFees(@Param("studId") int studentId);

    @Query("SELECT COUNT(sf) FROM StudentFeesList sf "+ 
           "WHERE sf.fee.id =:feeId " +
           "AND sf.student.studentId = :studentId")
    Integer feesCount(@Param("studentId") int studentId, @Param("feeId") int feeId);
    
    @Query("SELECT SUM(sf.amount) FROM StudentFeesList sf WHERE sf.fee.id =:feeId AND sf.student.studentId = :studentId")
    Double totalPerFeesByStudent(@Param("studentId") int studentId, @Param("feeId") int feeId);
    
    @Query("SELECT sf FROM StudentFeesList sf " + 
           "WHERE sf.fee.id = :feeId " + 
           "AND sf.student.studentId = :studentId " + 
           "AND sf.sem.sySemNumber = :semId")
    Optional<StudentFeesList> getFeesBySem(@Param("studentId") int studentId, @Param("feeId") int feeId,@Param("semId") int semId);
}
