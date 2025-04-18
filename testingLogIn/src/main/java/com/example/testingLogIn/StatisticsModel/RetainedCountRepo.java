package com.example.testingLogIn.StatisticsModel;

import com.example.testingLogIn.Enums.Semester;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RetainedCountRepo extends CrudRepository<RetainedCount,Integer> {
    @Query("""
            SELECT rc FROM RetainedCount rc
            WHERE rc.sem.sySemNumber = :semId
            """)
    Optional<PassedCount> findBySemester(int semId);

    @Modifying
    @Transactional
    @Query("""
            UPDATE RetainedCount rc
            SET rc.count = :count
            WHERE rc.sem.sySemNumber = :semId
            """)
    void updateCount(int semId, int count);

    @Query("""
            SELECT SUM(rc.count) FROM RetainedCount rc
            WHERE (:sy IS NULL OR rc.sem.schoolYear.schoolYearNum = :sy)
            AND (:sem IS NULL OR rc.sem.sem = :sem)
            """)
    Optional<Long> getSum(Integer sy, Semester sem);
}
