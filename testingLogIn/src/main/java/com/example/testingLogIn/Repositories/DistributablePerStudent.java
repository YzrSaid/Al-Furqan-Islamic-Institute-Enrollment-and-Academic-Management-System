package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.AssociativeModels.DistributablesPerStudent;
import org.springframework.data.repository.CrudRepository;

public interface DistributablePerStudent extends CrudRepository<DistributablesPerStudent,Integer> {
}
