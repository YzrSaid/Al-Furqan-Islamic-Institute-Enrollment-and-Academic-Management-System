package com.example.testingLogIn.CustomObjects;

import com.example.testingLogIn.Models.RequiredFees;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TotalPaid {

    private Double totalPaid;
    private Double requiredTotalAmount;
    private RequiredFees fee;
}
