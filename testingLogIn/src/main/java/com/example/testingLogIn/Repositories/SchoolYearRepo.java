package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.Models.SchoolYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author magno
 */
public interface SchoolYearRepo extends JpaRepository<SchoolYear,Integer> {
    @Query("SELECT sy FROM SchoolYear sy "+
           "WHERE sy.isNotDeleted = true "+
           "AND LOWER(sy.schoolYear) = LOWER(:schoolYearName)")
    public SchoolYear getByName(@Param("schoolYearName") String schoolYearName);
}
