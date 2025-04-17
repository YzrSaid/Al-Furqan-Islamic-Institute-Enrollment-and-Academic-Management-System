package com.example.testingLogIn.PasswordResetPackage;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends CrudRepository <PasswordResetToken,Long> {

    @Query("SELECT t FROM PasswordResetToken t " +
            "WHERE t.token = :token")
    Optional<PasswordResetToken> findByToken(String token);
}
