package com.example.testingLogIn.WebsiteConfiguration;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolProfileRepo extends CrudRepository<SchoolProfile,String> {
}
