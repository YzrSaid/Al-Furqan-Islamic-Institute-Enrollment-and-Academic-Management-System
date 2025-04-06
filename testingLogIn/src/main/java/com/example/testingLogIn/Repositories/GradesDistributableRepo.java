package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.AssociativeModels.DistributablesPerGrade;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GradesDistributableRepo extends CrudRepository<DistributablesPerGrade,Integer> {
    @Query("SELECT dpg FROM DistributablesPerGrade dpg " +
            "WHERE dpg.item.itemId = :itemId")
    List<DistributablesPerGrade> findByDistributableItem(@Param("itemId") int itemId);

    @Query("SELECT dpg FROM DistributablesPerGrade dpg " +
            "WHERE dpg.item.isCurrentlyActive " +
            "AND dpg.gradeLevel.levelId = :itemId " +
            "AND dpg.isNotDeleted")
    List<DistributablesPerGrade> findByDistributableByGradeLevel(@Param("levelId") int levelId);
}
