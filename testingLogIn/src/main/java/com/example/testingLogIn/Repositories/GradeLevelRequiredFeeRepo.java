package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.AssociativeModels.GradeLevelRequiredFees;
import java.util.List;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author magno
 */
@Repository
public interface GradeLevelRequiredFeeRepo extends JpaRepository<GradeLevelRequiredFees,Integer>{
    
    @Query("SELECT glr FROM GradeLevelRequiredFees glr "+
            "WHERE glr.requiredFee.id = :feeId")
    List<GradeLevelRequiredFees> findByRequiredFee(@Param("feeId") int feeId);
    
    @Query("""
           SELECT glr FROM GradeLevelRequiredFees glr
           WHERE glr.isNotDeleted
           AND glr.requiredFee.isNotDeleted
           AND glr.requiredFee.isCurrentlyActive
           AND glr.gradeLevel.levelId = :gardeLevelid
           """)
    List<GradeLevelRequiredFees> findByGradeLevel(@Param("gardeLevelid") int gardeLevelid);
    
    @Query("SELECT SUM(glr.requiredFee.requiredAmount) FROM GradeLevelRequiredFees glr "+
           "WHERE glr.requiredFee.isNotDeleted AND glr.requiredFee.isCurrentlyActive "+
           "AND glr.isNotDeleted = true "+
           "AND glr.gradeLevel.levelId = :gradeLevelId")
    Double findTotalAmountByGradeLevel(@Param("gradeLevelId") int gradeLevelId);

    @Modifying
    @Transactional
    @Query("""
           UPDATE GradeLevelRequiredFees glr
           SET glr.isNotDeleted = FALSE
           WHERE glr.requiredFee.id = :feeId
            """)
    void setAsDeleted(int feeId);
}
