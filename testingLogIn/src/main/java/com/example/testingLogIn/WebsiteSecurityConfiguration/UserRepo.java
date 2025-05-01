package com.example.testingLogIn.WebsiteSecurityConfiguration;

import com.example.testingLogIn.CustomObjects.StudentAccount;
import com.example.testingLogIn.Enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepo extends JpaRepository<UserModel, Integer> {
    @Query("SELECT u FROM UserModel u "+
           "WHERE u.isNotDeleted = true "+
           "AND LOWER(u.username) = LOWER(:username)")
    UserModel findByUsername(@Param("username")String username);

    @Query("""
           SELECT usr FROM UserModel usr
           JOIN usr.student stud
           WHERE stud.studentId = :studentId
            """)
    Optional<UserModel> findStudentAccount(int studentId);

    @Query("SELECT staff FROM UserModel staff " +
            "WHERE LOWER(staff.fullName) LIKE :search")
    Page<UserModel> findStaffAccounts(String search, Pageable pageable);

    @Query("SELECT NEW com.example.testingLogIn.CustomObjects.StudentAccount(stud.studentDisplayId,acc.username,pw.password,stud.fullName) FROM Student stud " +
            "RIGHT JOIN UserModel acc ON stud.studentId = acc.student.studentId " +
            "RIGHT JOIN StudentPassword pw ON stud.studentId = pw.student.studentId "+
            "WHERE acc.role = 'STUDENT' " +
            "AND (stud.studentDisplayId LIKE :search " +
            "OR LOWER(stud.fullName) LIKE :search)")
    Page<StudentAccount> findStudentAccounts(String search, Pageable pageable);

    @Query("""
           SELECT user FROM UserModel user
           WHERE user.isNotRestricted = :isNotRestricted
           AND user.role != 'ADMIN'
           AND (:role IS NULL OR user.role = :role)
           AND LOWER(user.fullName) LIKE :name
           """)
    Page<UserModel> findUsersByPageRole(Role role, String name, boolean isNotRestricted, Pageable pageable);

    @Query("""
           SELECT COUNT(user) FROM UserModel user
           WHERE user.role = :role
           """)
    Optional<Integer> countStaffByRoles(Role role);

    @Query("""
           SELECT user FROM UserModel user
           WHERE LOWER(user.fullName) = :fullName
           """)
    Optional<UserModel> findByFullName(String fullName);
}
