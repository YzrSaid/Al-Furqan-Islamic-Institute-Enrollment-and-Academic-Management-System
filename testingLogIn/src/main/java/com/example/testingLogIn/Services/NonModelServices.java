package com.example.testingLogIn.Services;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NonModelServices {
    public static String forLikeOperator(String toFormat){
        return "%"+toFormat.toLowerCase()+"%";
    }

    public static double adjustDecimal(double value){
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public static double zeroIfLess(double value){
        return value < 0 ? 0.0d : value;
    }
}
