package com.example.testingLogIn.ModelDTO;

/**
 *
 * @author magno
 */
import com.example.testingLogIn.Models.Section;
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
    private boolean isNotDeleted;
}
