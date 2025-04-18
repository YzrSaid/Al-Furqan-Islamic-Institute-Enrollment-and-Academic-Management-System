package com.example.testingLogIn.StatisticsModel;

import com.example.testingLogIn.Enums.Semester;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GraduatesCountRepo extends CrudRepository<GraduatesCount,Integer> {

    @Query("""
            SELECT gc FROM GraduatesCount gc
            WHERE gc.sem.sySemNumber = :semId
            """)
    Optional<GraduatesCount> findBySemester(int semId);

    @Query("""
            SELECT SUM(gc.count) FROM GraduatesCount gc
            WHERE (:sy IS NULL OR gc.sem.schoolYear.schoolYearNum = :sy)
            AND (:sem IS NULL OR gc.sem.sem = :sem)
            """)
    Optional<Long> getSum(Integer sy, Semester sem);
}
