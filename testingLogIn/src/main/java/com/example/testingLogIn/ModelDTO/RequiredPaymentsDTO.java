package com.example.testingLogIn.ModelDTO;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author magno
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequiredPaymentsDTO {
    private int id;
    private String name;
    private double requiredAmount;
    private boolean isDeleted;
    private boolean isCurrentlyActive;
    private boolean isDistributable;
    private boolean willApplyNow;
    private List<Integer> gradeLevels;
    private List<String> gradeLevelNames;
}
