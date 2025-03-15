package com.example.testingLogIn.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int discountId;

    private String discountName;
    private boolean isNotDeleted;

    private double fixedDiscount;
    private float percentageDiscount;

    public Discount(int discountId, String discountName, boolean isNotDeleted, double fixedDiscount, float percentageDiscount) {
        this.discountId = discountId;
        this.discountName = discountName;
        this.isNotDeleted = isNotDeleted;
        this.fixedDiscount = fixedDiscount;
        this.percentageDiscount = percentageDiscount/100;
    }
}
