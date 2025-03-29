package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.Models.Distributable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DistributableRepo extends CrudRepository<Distributable,Integer> {

    @Query("SELECT dis FROM Distributable dis WHERE dis.isNotDeleted AND LOWER(dis.itemName) LIKE itemName")
    Optional<Distributable> findByName(@Param("itemName") String itemName);

    List<Distributable> findByIsNotDeletedTrue();
    List<Distributable> findByIsNotDeletedFalse();
}
