package com.example.testingLogIn.PasswordResetPackage;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends CrudRepository <PasswordResetToken,Long> {

    @Query("SELECT t FROM PasswordResetToken t " +
            "WHERE t.token = :token")
    Optional<PasswordResetToken> findByToken(String token);

    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetToken t " +
            "WHERE t.user.staffId = :userId")
    void deleteUserTokens(int userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetToken pwt WHERE pwt.expiryDate < :timeNow")
    void deleteSomeTokens(LocalDateTime timeNow);
}
