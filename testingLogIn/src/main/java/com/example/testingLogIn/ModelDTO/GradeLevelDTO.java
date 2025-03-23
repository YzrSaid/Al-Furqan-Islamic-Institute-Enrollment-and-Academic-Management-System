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
public class GradeLevelDTO {
    private int levelId;
    private String levelName;
    private boolean isNotDeleted;
    private int subjectsCount;
    private int sectionsCount;
    private String preRequisite;

    public GradeLevelDTO(int levelId, String levelName, boolean isNotDeleted, int subjectsCount, int sectionsCount, String preRequisite) {
        this.levelId = levelId;
        this.levelName = levelName;
        this.isNotDeleted = isNotDeleted;
        this.subjectsCount = subjectsCount;
        this.sectionsCount = sectionsCount;
        this.preRequisite = preRequisite;
    }
}
