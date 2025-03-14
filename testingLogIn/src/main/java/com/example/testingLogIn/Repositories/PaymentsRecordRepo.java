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
    @Query("SELECT pr FROM PaymentRecords pr " +
           "WHERE pr.student.studentId = :studentId " +
           "AND pr.SYSem.sySemNumber = :semId " +
           "GROUP BY pr.requiredPayment")
    List<PaymentRecords> getCurrentPaidRequiredPaymentOfStudent(
            @Param("studentId") int studentId,
            @Param("semId") int semId);
    
    @Query("SELECT COUNT(*)>0 from PaymentRecords pr "+
            "WHERE pr.student.studentId = :studentId "+
            "AND pr.SYSem.sySemNumber = :semId "+
            "AND pr.requiredPayment.id = :reqPaymentId")
    boolean doesExist(
            @Param("studentId") int studentId,
            @Param("reqPaymentId") int reqPaymentId,
            @Param("semId") int semId);
    
    @Query("SELECT pr FROM PaymentRecords pr "+
           "WHERE pr.SYSem.sySemNumber = :semId "+
           "ORDER BY pr.datePaid DESC")
    List<PaymentRecords> getRecordsBySem(@Param("semId") int semId);
    
    @Query("SELECT pr FROM PaymentRecords pr "+
           "WHERE pr.student.studentId = :studentId "+
           "ORDER BY pr.datePaid DESC")
    List<PaymentRecords> getAllStudentPaymentRecord(@Param("studentId") int studentId);
    
    @Query("SELECT SUM(pr.amount) from PaymentRecords pr "+
            "WHERE pr.student.studentId = :studentId "+
            "AND pr.SYSem.sySemNumber = :semId "+
            "AND pr.requiredPayment.id = :reqPaymentId")
    Double getTotalPaidByStudentForFeeInSemester(
            @Param("studentId") int studentId,
            @Param("reqPaymentId") int reqPaymentId,
            @Param("semId") int semId);
    
    @Query("SELECT pr FROM PaymentRecords pr ORDER BY pr.datePaid DESC")
    List<PaymentRecords> getAllRecords();

    @Query("SELECT pr FROM PaymentRecords pr ORDER BY pr.datePaid")
    Page<PaymentRecords> getRecordsPage(Pageable pageable);
    
    @Query("SELECT NEW com.example.testingLogIn.CustomObjects.TotalPaid(SUM(pr.amount),pr.requiredPayment.requiredAmount,pr.requiredPayment) FROM PaymentRecords pr "+
           "WHERE pr.student.studentId = :studId GROUP BY pr.requiredPayment,pr.requiredPayment.requiredAmount")
    List<TotalPaid> totalPaidPerFee(@Param("studId") int studentId);

    @Query("SELECT SUM(pr.amount) from PaymentRecords pr "+
            "WHERE pr.student.studentId = :studentId "+
            "AND pr.requiredPayment.id = :reqPaymentId "+
            "AND (:semId IS NULL OR pr.SYSem.sySemNumber = :semId)")
    Double totalPaidForSpecificFee(@Param("studentId") int studentId,
                                   @Param("reqPaymentId") int reqPaymentId,
                                   @Param("semId") Integer semId);
}
