package com.example.testingLogIn.StatisticsModel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CounterObject {

    private long enrolledCount;
    private long preEnrolledCount;
    private long graduatesCount;
    private long retainedCount;
    private long passedCount;
}
