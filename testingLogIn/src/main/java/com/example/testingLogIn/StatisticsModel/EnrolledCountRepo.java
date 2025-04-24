package com.example.testingLogIn.StatisticsModel;

import com.example.testingLogIn.Enums.Semester;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnrolledCountRepo extends CrudRepository<EnrolledCount,Integer> {

    @Query("SELECT ec FROM EnrolledCount ec " +
            "WHERE ec.sem.sySemNumber = :semId")
    Optional<EnrolledCount> findBySemester(int semId);

    @Modifying
    @Transactional
    @Query("UPDATE EnrolledCount ec SET ec.count = ec.count+1 " +
            "WHERE ec.sem.sySemNumber = :semId")
    void addByOne(int semId);

    @Query("""
            SELECT SUM(ec.count) FROM EnrolledCount ec
            JOIN ec.sem semester
            WHERE (:sy IS NULL OR semester.schoolYear.schoolYearNum = :sy)
            AND (:sem IS NULL OR semester.sem = :sem)
            """)
    Optional<Long> getSum(Integer sy, Semester sem);
}
