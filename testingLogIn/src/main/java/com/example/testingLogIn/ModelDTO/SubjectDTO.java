/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.testingLogIn.ModelDTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectDTO {
    private int subjectNumber;
    private String gradeLevel;
    private int levelId;
    private String subjectName;
    private boolean isNotDeleted;
    private boolean willApplyNow;
}
