package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.Models.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface DiscountRepo extends JpaRepository<Discount,Integer> {

    @Query("SELECT dc FROM Discount dc WHERE dc.discountName LIKE CONCAT('%',:name,'%')")
    List<Discount> findDiscountByName(@Param("name") String name);

    List<Discount> findByIsNotDeletedTrue();
    List<Discount> findByIsNotDeletedFalse();
}
