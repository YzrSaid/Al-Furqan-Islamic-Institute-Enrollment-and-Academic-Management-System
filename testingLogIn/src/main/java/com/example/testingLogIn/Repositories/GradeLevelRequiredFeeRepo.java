package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.Models.GradeLevelToRequiredPayment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author magno
 */
@Repository
public interface GradeLevelRequiredFeeRepo extends JpaRepository<GradeLevelToRequiredPayment,Integer>{
    
    @Query("SELECT glr FROM GradeLevelToRequiredPayment glr "+
           "WHERE glr.isNotDeleted = true "+
           "AND glr.requiredFee.id = :feeId")
    List<GradeLevelToRequiredPayment> findByRequiredFee(@Param("feeId") int feeId);
    
    @Query("SELECT glr FROM GradeLevelToRequiredPayment glr "+
           "WHERE glr.isNotDeleted = true "+
           "AND glr.gradeLevel.levelId = :gardeLevelid")
    List<GradeLevelToRequiredPayment> findByGradeLevel(@Param("gardeLevelid") int gardeLevelid);
}
