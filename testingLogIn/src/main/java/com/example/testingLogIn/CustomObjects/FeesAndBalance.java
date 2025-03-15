package com.example.testingLogIn.CustomObjects;

import com.example.testingLogIn.Models.RequiredFees;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeesAndBalance{
    private RequiredFees fee;
    private double balance;
}
