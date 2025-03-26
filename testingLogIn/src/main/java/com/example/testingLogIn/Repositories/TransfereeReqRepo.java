package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.Models.TransfereeRequirements;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransfereeReqRepo extends CrudRepository<TransfereeRequirements,Integer> {

    List<TransfereeRequirements> findByIsNotDeletedTrue();
    List<TransfereeRequirements> findByIsNotDeletedFalse();

    @Query("SELECT req from  TransfereeRequirements req " +
            "WHERE req.isNotDeleted " +
            "AND LOWER(req.name) LIKE :reqName")
    Optional<TransfereeRequirements> findUsingName(@Param("reqName") String reqName);
}
