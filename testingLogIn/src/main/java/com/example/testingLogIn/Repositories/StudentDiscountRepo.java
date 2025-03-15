package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.CustomObjects.StudentTotalDiscount;
import com.example.testingLogIn.Models.StudentDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentDiscountRepo extends JpaRepository<StudentDiscount,Integer> {
    
    @Query("SELECT NEW com.example.testingLogIn.CustomObjects.StudentTotalDiscount(SUM(sd.discount.percentageDiscount),SUM(sd.discount.fixedDiscount)) "+
           "FROM StudentDiscount sd "+
           "WHERE sd.discount.isNotDeleted = TRUE "+
           "AND sd.isNotDeleted = TRUE "+
           "AND sd.student.studentId = :studentId")
    Optional<StudentTotalDiscount> getStudentTotalDiscount(@Param("studentId") int studentId);

    @Query("SELECT sd FROM StudentDiscount sd " + 
           "WHERE sd.discount.isNotDeleted = TRUE "+
           "AND sd.isNotDeleted = TRUE " +
           "AND sd.student.studentId = :studentId " + 
           "AND sd.discount.discountId = :discountId")
    List<StudentDiscountRepo> findStudentDiscountRecord(@Param("studentId") int studentId, @Param("discountId") int discountId);
}
