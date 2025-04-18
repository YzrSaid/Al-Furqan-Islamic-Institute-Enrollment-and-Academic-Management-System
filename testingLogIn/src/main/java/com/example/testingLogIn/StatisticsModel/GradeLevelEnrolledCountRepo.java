package com.example.testingLogIn.StatisticsModel;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GradeLevelEnrolledCountRepo extends CrudRepository<GradeLevelEnrolledCount,Integer> {

    @Query("""
            SELECT glec FROM GradeLevelEnrolledCount glec 
            WHERE glec.gradeLevel.levelId = :levelId 
            AND glec.sem.sySemNumber = :semId
            """)
    Optional<GradeLevelEnrolledCount> findBySemesterAndGrade(int semId, int levelId);

    @Modifying
    @Transactional
    @Query("""
            UPDATE GradeLevelEnrolledCount glec 
            SET glec.count = glec.count + 1 
            WHERE glec.gradeLevel.levelId = :levelId 
            AND glec.sem.sySemNumber = :semId
            """)
    void increaseCount(int semId, int levelId);
}