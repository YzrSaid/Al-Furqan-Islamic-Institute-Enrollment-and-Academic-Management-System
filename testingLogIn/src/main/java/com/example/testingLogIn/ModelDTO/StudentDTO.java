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
    private String middleName;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private LocalDate birthdate;
    private String address;
    private String cellphoneNum;
    private String birthPlace;
    
    private String currentGradeSection;
    
    private String motherName;
    private String motherOccupation;
    private String fatherName;
    private String fatherOccupation;
    private String guardianName;
    private String guardianAddress;
    private String guardianContactNum;
    
    private boolean isNew;
    private boolean isTransferee;
    private boolean isScholar;
    private boolean isNotDeleted;
}
