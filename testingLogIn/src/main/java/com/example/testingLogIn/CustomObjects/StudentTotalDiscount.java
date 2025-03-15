package com.example.testingLogIn.CustomObjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author magno
 */
@NoArgsConstructor
@Data
public class StudentTotalDiscount {
    
    private Double totalPercentageDiscount;
    private Double totalFixedDiscount;

    public StudentTotalDiscount(Double totalPercentageDiscount, Double totalFixedDiscount) {
        this.totalPercentageDiscount = totalPercentageDiscount == null ? 0 : totalPercentageDiscount;
        this.totalFixedDiscount = totalFixedDiscount == null ? 0 : totalFixedDiscount;
    }
}
