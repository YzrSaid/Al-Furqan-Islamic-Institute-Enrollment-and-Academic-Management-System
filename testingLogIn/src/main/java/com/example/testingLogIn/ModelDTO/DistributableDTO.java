package com.example.testingLogIn.ModelDTO;

import com.example.testingLogIn.Models.GradeLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DistributableDTO {
    private int itemId;
    private String itemName;
    private List<GradeLevel> gradeLevelList;
    private List<Integer> gradeLevelIds;
    private boolean isCurrentlyActive;
}
