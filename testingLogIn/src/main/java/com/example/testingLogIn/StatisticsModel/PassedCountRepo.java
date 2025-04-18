package com.example.testingLogIn.StatisticsModel;

import jakarta.persistence.Entity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PassedCountRepo extends CrudRepository<PassedCount,Integer> {
}
