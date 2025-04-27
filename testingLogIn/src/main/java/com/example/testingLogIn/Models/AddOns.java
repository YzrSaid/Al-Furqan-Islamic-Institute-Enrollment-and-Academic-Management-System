package com.example.testingLogIn.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
public class AddOns {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int addOnId;

    private String name;
    private double price;

    private boolean isNotDeleted;

    public AddOns(String name,double price) {
        this.isNotDeleted = true;
        this.price = price;
        this.name = name;
    }
}
