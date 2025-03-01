package com.example.testingLogIn.ModelDTO;

import com.example.testingLogIn.Enums.Gender;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalDate;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentDTO {
    private int studentId;

    private String firstName;
    private String lastName;
    private String contactNum;
    private Gender gender;
    private LocalDate birthdate;
    private String address;
    
    private String currentSection;
    private String gradeSectionToEnroll;
    
    private String contactPersonName;
    private String contactPersonAddress;
    private String contactPersonCellphone;
    
    private boolean isNew;
    private boolean isTransferee;
    private boolean isScholar;
    private boolean isNotDeleted;
}
