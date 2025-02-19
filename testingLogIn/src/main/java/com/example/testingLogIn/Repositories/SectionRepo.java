package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.Models.Section;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author magno
 */
public interface SectionRepo extends JpaRepository<Section, Integer> {
    
}
