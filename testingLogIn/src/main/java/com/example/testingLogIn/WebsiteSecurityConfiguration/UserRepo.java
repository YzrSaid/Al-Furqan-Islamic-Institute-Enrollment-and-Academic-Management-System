package com.example.testingLogIn.WebsiteSecurityConfiguration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepo extends JpaRepository<UserModel, Integer> {
    @Query("SELECT u FROM UserModel u "+
           "WHERE u.isNotDeleted = true "+
           "AND LOWER(u.username) = LOWER(:username)")
    UserModel findByUsername(@Param("username")String username);
}
