package com.example.testingLogIn.CustomObjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Address {

    private String street;
    private String barangay;
    private String city;

    @Override
    public String toString() {
        return street+", "+barangay+" "+city;
    }
}
