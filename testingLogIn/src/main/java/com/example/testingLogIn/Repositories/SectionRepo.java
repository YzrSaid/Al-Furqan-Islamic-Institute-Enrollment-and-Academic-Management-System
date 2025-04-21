package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.ModelDTO.SectionDTO;
import com.example.testingLogIn.Models.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * @author magno
 */
@Repository
public interface SectionRepo extends JpaRepository<Section, Integer> {

    @Query("SELECT sec FROM Section sec " +
            "JOIN sec.level gl " +/*on sec.level.levelId = gl.levelId*/
            "JOIN sec.adviser staff " +
            "WHERE sec.isNotDeleted = TRUE " +
            "AND gl.isNotDeleted = TRUE " +
            "AND (:search IS NULL " +
            "OR LOWER(sec.sectionName) LIKE :search " +
            "OR LOWER(gl.levelName) LIKE :search " +
            "OR LOWER(staff.fullName) LIKE :search)")
    List<Section> findSectionsAndGradeLevelNotDeleted(@Param("search") String search);

    List<Section> findByIsNotDeletedTrue();
}
