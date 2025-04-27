package com.example.testingLogIn.CustomObjects;

import com.example.testingLogIn.Models.RequiredFees;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class FeesAndBalance{
    private RequiredFees fee;
    private double discount;
    private double balance;
    private double totalPaid;
    private double totalRequired;
}
