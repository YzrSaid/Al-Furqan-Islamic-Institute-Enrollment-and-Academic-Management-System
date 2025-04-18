package com.example.testingLogIn.StatisticsModel;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentPassingRecordRepo extends CrudRepository<StudentPassingRecord,Integer> {
}
