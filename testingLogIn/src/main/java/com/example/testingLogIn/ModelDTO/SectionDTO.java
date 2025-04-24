package com.example.testingLogIn.ModelDTO;

/**
 *
 * @author magno
 */
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SectionDTO {
    private int number;
    private String gradeLevelName;
    private int levelId;
    private String  adviserName;
    private int adviserId;
    private String sectionName;
    private int capacity;
    private Integer studentEnrolledCount;
    private Integer subSchedCount;
    private boolean isNotDeleted;
}
