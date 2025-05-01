package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.AssociativeModels.StudentSubjectGradeLogs;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentSubjectGradeLogsRepo extends CrudRepository<StudentSubjectGradeLogs,Integer> {

    @Query("""
           SELECT ssgl FROM StudentSubjectGradeLogs ssgl
           JOIN ssgl.ssg sg
           WHERE sg.numberId = :ssgId
           ORDER BY ssgl.dateModified DESC
           """)
    List<StudentSubjectGradeLogs> getLogs(int ssgId);
}
