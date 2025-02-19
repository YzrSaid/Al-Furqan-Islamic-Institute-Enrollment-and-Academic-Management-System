package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.Models.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author magno
 */
@Repository
public interface SectionRepo extends JpaRepository<Section, Integer> {
    
}
