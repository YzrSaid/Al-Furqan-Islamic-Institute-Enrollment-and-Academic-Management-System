package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.Models.PaymentCompleteCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
/**
 *
 * @author magno
 */
@Repository
public interface PaymentCheckerRepo extends JpaRepository<PaymentCompleteCheck,Integer>{
    @Query("SELECT COUNT(pcc)>0 from PaymentCompleteCheck pcc "+
            "WHERE pcc.student.studentId = :studentId "+
            "AND pcc.sem.sySemNumber = :semId")
    boolean doesExist(
            @Param("studentId") int studentId,
            @Param("semId") int semId);
    
    @Query("SELECT pcc from PaymentCompleteCheck pcc "+
            "WHERE pcc.student.studentId = :studentId "+
            "AND pcc.sem.sySemNumber = :semId")
    PaymentCompleteCheck getCurrent(
            @Param("studentId") int studentId,
            @Param("semId") int semId);
}
