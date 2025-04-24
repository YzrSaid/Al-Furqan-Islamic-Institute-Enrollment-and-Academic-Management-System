package com.example.testingLogIn.StatisticsModel;

import com.example.testingLogIn.Enums.Semester;
import com.example.testingLogIn.Models.GradeLevel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentPassingRecordRepo extends CrudRepository<StudentPassingRecord,Integer> {

    @Query("""
            SELECT COUNT(spc) FROM StudentPassingRecord spc
            JOIN spc.sem semester
            WHERE (:sy IS NULL OR semester.schoolYear.schoolYearNum = :sy)
            AND (:sem IS NULL OR semester.sem = :sem)
            AND spc.didPassed = :didPassed
            """)
    Optional<Integer> countRecord(Integer sy, Semester sem, boolean didPassed);

    @Query("""
            SELECT spc.gradeLevel FROM StudentPassingRecord spc
            JOIN spc.sem semester
            WHERE (:sy IS NULL OR semester.schoolYear.schoolYearNum = :sy)
            AND (:sem IS NULL OR semester.sem = :sem)
            AND spc.didPassed = :didPassed
            GROUP BY spc.gradeLevel
            """)
    List<GradeLevel> getUniqueGradeLevels(Integer sy, Semester sem, boolean didPassed);
}
