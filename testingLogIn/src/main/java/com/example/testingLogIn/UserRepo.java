package com.example.testingLogIn;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<UserModel, Integer> {

    UserModel findByUsername(String username);
}
