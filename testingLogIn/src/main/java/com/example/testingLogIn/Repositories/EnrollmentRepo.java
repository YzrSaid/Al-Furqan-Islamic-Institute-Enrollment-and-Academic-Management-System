package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.Models.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author magno
 */
public interface EnrollmentRepo extends JpaRepository<Enrollment, Integer> {
    
}
