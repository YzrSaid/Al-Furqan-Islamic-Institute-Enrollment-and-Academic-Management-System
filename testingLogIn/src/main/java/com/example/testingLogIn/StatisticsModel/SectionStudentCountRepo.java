package com.example.testingLogIn.StatisticsModel;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SectionStudentCountRepo extends CrudRepository<SectionStudentCount,Integer> {

    @Query("SELECT ssc FROM SectionStudentCount ssc " +
            "WHERE ssc.section.number = :sectionId " +
            "AND ssc.sySem.sySemNumber = :semId")
    Optional<SectionStudentCount> findBySectionAndSemester(@Param("sectionId") int sectionId,
                                                           @Param("semId") int semId);
}
