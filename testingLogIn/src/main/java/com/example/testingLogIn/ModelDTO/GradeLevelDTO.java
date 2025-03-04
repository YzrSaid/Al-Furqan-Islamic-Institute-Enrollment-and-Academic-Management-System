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
@NoArgsConstructor
@AllArgsConstructor
public class GradeLevelDTO {
    private int levelId;
    private String levelName;
    private boolean isNotDeleted;
    private String preRequisite;
}
