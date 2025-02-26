package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.Models.SchoolYear;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author magno
 */
public interface SchoolYearRepo extends JpaRepository<SchoolYear,Integer> {
    
}
