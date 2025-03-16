package com.example.testingLogIn.CustomObjects;

import com.example.testingLogIn.Models.Subject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author magno
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectSectionCount {
    
    private Subject subject;
    private Long count;
}
