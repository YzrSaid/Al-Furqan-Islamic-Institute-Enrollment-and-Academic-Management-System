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
    private String  adviserName;
    private String sectionName;
    private int capacity;
    
    private Integer subSchedCount;
    private boolean isNotDeleted;
}
