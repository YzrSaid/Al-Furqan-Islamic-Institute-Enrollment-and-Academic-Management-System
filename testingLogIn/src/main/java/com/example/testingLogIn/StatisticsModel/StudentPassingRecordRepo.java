package com.example.testingLogIn.StatisticsModel;

import com.example.testingLogIn.Enums.Semester;
import com.example.testingLogIn.Models.GradeLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
            SELECT spc.section.level FROM StudentPassingRecord spc
            JOIN spc.sem semester
            WHERE (:sy IS NULL OR semester.schoolYear.schoolYearNum = :sy)
            AND (:sem IS NULL OR semester.sem = :sem)
            AND spc.didPassed = :didPassed
            GROUP BY spc.section.level
            """)
    List<GradeLevel> getUniqueGradeLevels(Integer sy, Semester sem, boolean didPassed);

    @Query("""
           SELECT spd FROM StudentPassingRecord spd
           JOIN spd.student stud
           JOIN spd.sem sem
           JOIN spd.section sec
           WHERE (:sy IS NULL OR sem.schoolYear.schoolYearNum = :sy)
           AND (:levelId IS NULL OR sec.level.levelId = :levelId)
           AND (:passed IS NULL OR spd.didPassed = :passed)
           AND (:sem IS NULL OR sem.sem = :sem)
           AND (LOWER(stud.fullName) LIKE :search
           OR stud.studentDisplayId LIKE :search)
           """)
    Page<StudentPassingRecord> getPassingRecords(String search, Integer sy, Semester sem,Boolean passed,Integer levelId, Pageable pageable);
}
