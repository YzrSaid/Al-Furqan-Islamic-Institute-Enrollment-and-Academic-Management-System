package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.Models.SchoolYearSemester;
import com.example.testingLogIn.Models.StudentFeesList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.data.repository.query.Param;

@Repository
public interface StudentFeesListRepo extends JpaRepository<StudentFeesList,Integer> {

    @Query("SELECT sfl FROM StudentFeesList sfl WHERE sfl.student.studentId = :studId AND(:semId IS NULL OR sfl.sem.sySemNumber = :semId)")
    List<StudentFeesList> findBySem(@Param("studId") int studentId,@Param("semId") Integer semId);

    @Query("SELECT COUNT(sf) FROM StudentFeesList sf "+ 
           "WHERE sf.fee.id =:feeId " +
           "AND sf.student.studentId = :studentId")
    Integer feesCount(@Param("studentId") int studentId, @Param("feeId") int feeId);
}
