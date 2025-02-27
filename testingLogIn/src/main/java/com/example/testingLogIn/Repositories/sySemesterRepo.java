package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.Models.SchoolYearSemester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author magno
 */
@Repository
public interface sySemesterRepo extends JpaRepository<SchoolYearSemester,Integer>{
    
}
