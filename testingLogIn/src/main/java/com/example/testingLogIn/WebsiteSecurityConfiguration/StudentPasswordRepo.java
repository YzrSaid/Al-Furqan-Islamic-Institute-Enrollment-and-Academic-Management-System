package com.example.testingLogIn.WebsiteSecurityConfiguration;

import com.example.testingLogIn.AssociativeModels.StudentPassword;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentPasswordRepo extends CrudRepository<StudentPassword, Integer> {
}
