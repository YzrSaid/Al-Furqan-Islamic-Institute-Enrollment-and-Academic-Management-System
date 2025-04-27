package com.example.testingLogIn.StatisticsModel;

import com.example.testingLogIn.Enums.Gender;
import com.example.testingLogIn.Enums.Semester;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrolledGenderCountRepo extends CrudRepository<EnrolledGenderCount,Integer> {

    @Query("""
           SELECT egc FROM EnrolledGenderCount egc
           WHERE egc.gender = :gender
           AND egc.sem.sySemNumber = :semId
           """)
    Optional<EnrolledGenderCount> findByGenderSemester(Gender gender, int semId);

    @Query("""
            SELECT SUM(egc.count) FROM EnrolledGenderCount egc
            JOIN egc.sem semester
            WHERE (:sy IS NULL OR semester.schoolYear.schoolYearNum = :sy)
            AND (:sem IS NULL OR semester.sem = :sem)
            AND egc.gender = :gender
            """)
    Optional<Long> findTotalCountsByGender(Gender gender, Integer sy, Semester sem);
}
