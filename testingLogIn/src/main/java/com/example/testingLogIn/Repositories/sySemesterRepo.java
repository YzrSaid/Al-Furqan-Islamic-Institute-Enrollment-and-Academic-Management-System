package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.Models.SchoolYearSemester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author magno
 */
@Repository
public interface sySemesterRepo extends JpaRepository<SchoolYearSemester,Integer>{
    
    @Query("SELECT sem from SchoolYearSemester sem "+
           "WHERE sem.isNotDeleted = true "+
           "AND sem.isActive = true")
    public SchoolYearSemester findCurrentActive();
}
