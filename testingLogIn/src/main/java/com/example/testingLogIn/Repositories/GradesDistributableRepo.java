package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.AssociativeModels.DistributablesPerGrade;
import org.springframework.data.repository.CrudRepository;

public interface GradesDistributableRepo extends CrudRepository<DistributablesPerGrade,Integer> {
}
