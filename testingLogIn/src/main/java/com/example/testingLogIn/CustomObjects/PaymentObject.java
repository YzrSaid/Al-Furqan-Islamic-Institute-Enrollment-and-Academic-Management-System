package com.example.testingLogIn.CustomObjects;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentObject {
    private List<Integer> feesId;
}
