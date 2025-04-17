package com.example.testingLogIn.StatisticsModel;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PreEnrolledCountRepo extends CrudRepository<PreEnrolledCount,Integer> {

    @Query("SELECT per FROM PreEnrolledCount per " +
            "WHERE per.sem.sySemNumber = :semId")
    Optional<PreEnrolledCount> findBySemester(int semId);

    @Modifying
    @Transactional
    @Query("UPDATE PreEnrolledCount per SET per.count = per.count-1 " +
            "WHERE per.sem.sySemNumber = :semId")
    void reduceByOne(int semId);

    @Modifying
    @Transactional
    @Query("UPDATE PreEnrolledCount per SET per.count = per.count+1 " +
            "WHERE per.sem.sySemNumber = :semId")
    void addByOne(int semId);
}
