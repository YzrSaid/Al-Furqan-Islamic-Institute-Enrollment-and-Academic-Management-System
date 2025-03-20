package com.example.testingLogIn.CustomObjects;

import com.example.testingLogIn.Models.RequiredFees;

import java.util.List;
import java.util.Map;
import lombok.*;

/**
 *
 * @author magno
 */

@Builder
@Data
class FeesPaidAndBalance{
    private String feeName;
    private double requiredFee;
    private double totalPaid;
    private double balance;
    private String status;
}

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentPaymentView {
    private int studentId;
    private String studentDisplayId;
    private String studentFirstName;
    private String studentLastName;
    private String studentMiddleName;
    private List<FeesPaidAndBalance> feeStatus;

    public void addNewFeeStatus(String feeName, double requiredAmount, double paidAmount,String status){
        feeStatus.add(FeesPaidAndBalance.builder()
                .feeName(feeName)
                .requiredFee(requiredAmount)
                .totalPaid(paidAmount)
                .balance(requiredAmount-paidAmount)
                .status(status)
                .build());
    }
}
