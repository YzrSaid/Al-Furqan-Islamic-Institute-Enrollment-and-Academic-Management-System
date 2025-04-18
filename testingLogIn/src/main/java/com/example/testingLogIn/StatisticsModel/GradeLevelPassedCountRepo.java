package com.example.testingLogIn.StatisticsModel;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeLevelPassedCountRepo extends CrudRepository<GradeLevelPassedCount,Integer> {
}
