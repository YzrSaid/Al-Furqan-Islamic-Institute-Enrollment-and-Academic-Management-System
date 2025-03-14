package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.CustomObjects.StudentTotalDiscount;
import com.example.testingLogIn.Models.StudentDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudentDiscountRepo extends JpaRepository<StudentDiscount,Integer> {
    
    @Query("SELECT NEW com.example.testingLogIn.CustomObjects.StudentTotalDiscount(SUM(sd.discount.percentageDiscount),sd.discount.fixedDiscount) "+
           "FROM StudentDiscount sd "+
           "WHERE sd.isNotDeleted = TRUE "+
           "AND sd.student.studentId = :studentId")
    StudentTotalDiscount getStudentTotalDiscount(@Param("studentId") int studentId);
}
