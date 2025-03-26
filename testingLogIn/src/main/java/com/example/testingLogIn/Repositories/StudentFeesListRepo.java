package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.AssociativeModels.StudentFeesList;
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
            "AND stud.studentId = :studId")
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
}
