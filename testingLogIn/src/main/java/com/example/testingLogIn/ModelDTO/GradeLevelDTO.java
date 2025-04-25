package com.example.testingLogIn.ModelDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author magno
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GradeLevelDTO {
    private int semDuration;
    private int levelId;
    private String levelName;
    private boolean isNotDeleted;
    private int subjectsCount;
    private int sectionsCount;
    private String preRequisite;
}
