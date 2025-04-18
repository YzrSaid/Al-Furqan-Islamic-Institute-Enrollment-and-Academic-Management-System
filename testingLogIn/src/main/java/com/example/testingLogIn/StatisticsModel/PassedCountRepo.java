package com.example.testingLogIn.StatisticsModel;

import com.example.testingLogIn.Enums.Semester;
import jakarta.persistence.Entity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PassedCountRepo extends CrudRepository<PassedCount,Integer> {

    @Query("""
            SELECT pc FROM PassedCount pc
            WHERE pc.sem.sySemNumber = :semId
            """)
    Optional<PassedCount> findBySemester(int semId);

    @Modifying
    @Transactional
    @Query("""
            UPDATE PassedCount pc
            SET pc.count = :count
            WHERE pc.sem.sySemNumber = :semId
            """)
    void updateCount(int semId, int count);

    @Query("""
            SELECT SUM(pc.count) FROM PassedCount pc
            WHERE (:sy IS NULL OR pc.sem.schoolYear.schoolYearNum = :sy)
            AND (:sem IS NULL OR pc.sem.sem = :sem)
            """)
    Optional<Integer> getSum(Integer sy, Semester sem);
}
