package com.example.testingLogIn.WebsiteSecurityConfiguration;

import com.example.testingLogIn.CustomObjects.StudentAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepo extends JpaRepository<UserModel, Integer> {
    @Query("SELECT u FROM UserModel u "+
           "WHERE u.isNotDeleted = true "+
           "AND LOWER(u.username) = LOWER(:username)")
    UserModel findByUsername(@Param("username")String username);

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
}
