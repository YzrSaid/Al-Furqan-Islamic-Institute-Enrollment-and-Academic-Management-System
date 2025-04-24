package com.example.testingLogIn.StatisticsModel;

import com.example.testingLogIn.Enums.Semester;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GradeLevelRetainedCountRepo extends CrudRepository<GradeLevelRetainedCount,Integer> {

    @Query("""
            SELECT glrc FROM GradeLevelRetainedCount glrc
            JOIN glrc.gradeLevel gl
            JOIN glrc.sem semester
            WHERE gl.levelId = :levelId
            AND semester.sySemNumber = :semId
            """)
    Optional<GradeLevelRetainedCount> findByGradeLevelAndSemester(int semId, int levelId);

    @Query("""
            SELECT SUM(glrc.count) FROM GradeLevelRetainedCount glrc
            JOIN glrc.gradeLevel gl
            JOIN glrc.sem semester
            WHERE (:levelId IS NULL OR gl.levelId = :levelId)
            AND (:sy IS NULL OR semester.schoolYear.schoolYearNum = :sy)
            AND (:sem IS NULL OR semester.sem = :sem)
            """)
    Optional<Long> getTotal(Integer sy, Semester sem, Integer levelId);
}
