package com.example.testingLogIn.StatisticsModel;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RetainedCountRepo extends CrudRepository<RetainedCount,Integer> {
}
