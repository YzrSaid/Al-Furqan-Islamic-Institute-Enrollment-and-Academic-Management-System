package com.example.testingLogIn.StatisticsModel;

import com.example.testingLogIn.CustomObjects.PagedResponse;
import com.example.testingLogIn.Enums.Semester;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface GraduateStudentsRepo extends CrudRepository<GraduateStudents,Integer> {

    @Query("""
           SELECT gs FROM GraduateStudents gs
           JOIN gs.student stud
           JOIN gs.sem sem
           WHERE (:sy IS NULL OR sem.schoolYear.schoolYearNum = :sy)
           AND (:sem IS NULL OR sem.sem = :sem)
           AND (LOWER(stud.fullName) LIKE :search
           OR stud.studentDisplayId LIKE :search)
           """)
    Page<GraduateStudents> getGraduatesPage(String search, Integer sy, Semester sem, Pageable pageable);
}
