package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.CustomObjects.TotalPaid;
import com.example.testingLogIn.Models.PaymentRecords;
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
public interface PaymentsRecordRepo extends JpaRepository<PaymentRecords,Integer>{
    @Query("SELECT pr FROM PaymentRecords pr "+
           "JOIN pr.transaction pt "+
           "JOIN pt.SYSem s "+
           "WHERE s.sySemNumber = :semId")
    List<PaymentRecords> getRecordsBySem(@Param("semId") int semId);
    
    @Query("SELECT pr FROM PaymentRecords pr " +
       "JOIN pr.transaction pt " +
       "JOIN pt.student s " +
       "WHERE s.studentId = :studentId")
    List<PaymentRecords> getAllStudentPaymentRecord(@Param("studentId") int studentId);
    
    @Query("SELECT SUM(pr.amount) from PaymentRecords pr "+
            "JOIN pr.transaction pt "+
            "JOIN pt.SYSem s "+
            "JOIN pt.student stud "+
            "WHERE pt.isNotVoided = TRUE AND " +
            "stud.studentId = :studentId "+
            "AND s.sySemNumber = :semId "+
            "AND pr.requiredPayment.id = :reqPaymentId")
    Double getTotalPaidByStudentForFeeInSemester(
            @Param("studentId") int studentId,
            @Param("reqPaymentId") int reqPaymentId,
            @Param("semId") int semId);
    
    @Query("SELECT NEW com.example.testingLogIn.CustomObjects.TotalPaid(SUM(pr.amount),rp.requiredAmount,pr.requiredPayment) FROM PaymentRecords pr "+
           "JOIN pr.transaction t "+
            "JOIN t.student s "+
            "JOIN pr.requiredPayment rp "+
            "WHERE s.studentId = :studId GROUP BY pr.requiredPayment,rp.requiredAmount")
    List<TotalPaid> totalPaidPerFee(@Param("studId") int studentId);

    @Query("SELECT SUM(pr.amount) from PaymentRecords pr "+
            "JOIN pr.requiredPayment rp "+
            "JOIN pr.transaction t "+
            "JOIN t.SYSem s "+
            "JOIN t.student stud "+
            "WHERE stud.studentId = :studentId "+
            "AND rp.id = :reqPaymentId "+
            "AND (:semId IS NULL OR s.sySemNumber = :semId)")
    Double totalPaidForSpecificFee(@Param("studentId") int studentId,
                                   @Param("reqPaymentId") int reqPaymentId,
                                   @Param("semId") Integer semId);

    @Query("SELECT pr from PaymentRecords pr "+
            "JOIN pr.transaction pt "+
            //"JOIN pt.SYSem s "+
            "JOIN pr.requiredPayment rp "+
            "WHERE pt.isNotVoided = TRUE AND " +
            "rp.id=:reqPaymentId")
    Page<PaymentRecords> getRecordsByFee(@Param("reqPaymentId") int reqPaymentId, Pageable pageable);
}
