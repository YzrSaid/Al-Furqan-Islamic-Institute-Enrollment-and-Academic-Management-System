package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.CustomObjects.StudentTotalDiscount;
import com.example.testingLogIn.AssociativeModels.StudentDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentDiscountRepo extends JpaRepository<StudentDiscount,Integer> {
    
    @Query("SELECT NEW com.example.testingLogIn.CustomObjects.StudentTotalDiscount(SUM(sd.discount.percentageDiscount),SUM(sd.discount.fixedDiscount)) "+
           "FROM StudentDiscount sd "+
            "JOIN sd.student stud "+
           "WHERE sd.discount.isNotDeleted = TRUE "+
           "AND sd.isNotDeleted = TRUE "+
           "AND stud.studentId = :studentId")
    Optional<StudentTotalDiscount> getStudentTotalDiscount(@Param("studentId") int studentId);

    @Query("SELECT sd FROM StudentDiscount sd " +
            "JOIN sd.student stud "+
            "WHERE sd.discount.isNotDeleted = TRUE "+
            "AND sd.isNotDeleted = TRUE " +
            "AND sd.discount.discountId = :discountId "+
            "AND stud.studentId = :studentId")
    List<StudentDiscountRepo> findStudentDiscountRecord(@Param("studentId") int studentId, @Param("discountId") int discountId);
}
