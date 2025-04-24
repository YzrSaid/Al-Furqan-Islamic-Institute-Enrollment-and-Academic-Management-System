package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.AssociativeModels.DistributablesPerGrade;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GradesDistributableRepo extends CrudRepository<DistributablesPerGrade,Integer> {
    @Query("SELECT dpg FROM DistributablesPerGrade dpg " +
            "WHERE dpg.item.itemId = :itemId")
    List<DistributablesPerGrade> findByDistributableItem(int itemId);

    @Query("SELECT dpg FROM DistributablesPerGrade dpg " +
            "WHERE dpg.item.isCurrentlyActive " +
            "AND dpg.item.isNotDeleted " +
            "AND dpg.isNotDeleted " +
            "AND dpg.gradeLevel.levelId = :levelId")
    List<DistributablesPerGrade> findByDistributableByGradeLevel(@Param("levelId") int levelId);
}
