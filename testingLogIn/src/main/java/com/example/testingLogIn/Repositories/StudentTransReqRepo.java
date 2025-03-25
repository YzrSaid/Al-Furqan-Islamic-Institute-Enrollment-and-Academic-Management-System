package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.AssociativeModels.StudentTransfereeRequirements;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentTransReqRepo extends CrudRepository<StudentTransfereeRequirements,Integer> {

    @Query("SELECT str FROM StudentTransfereeRequirements str " +
            "WHERE str.student.studentId = :studentId " +
            "AND str.isNotDeleted")
    List<StudentTransfereeRequirements> findStudentRecords(@Param("studentId") int studentId);
}
