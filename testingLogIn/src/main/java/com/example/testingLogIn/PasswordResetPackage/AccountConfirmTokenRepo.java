package com.example.testingLogIn.PasswordResetPackage;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountConfirmTokenRepo extends CrudRepository<AccountConfirmationToken,Long> {

    @Query("""
           SELECT act FROM AccountConfirmationToken act
           WHERE act.token = :token
           """)
    Optional<AccountConfirmationToken> findByToken(String token);

    @Modifying
    @Transactional
    @Query("""
           DELETE FROM AccountConfirmationToken act
           WHERE act.accountRegister.username = :username
           """)
    void deleteUserTokens(String username);
}
