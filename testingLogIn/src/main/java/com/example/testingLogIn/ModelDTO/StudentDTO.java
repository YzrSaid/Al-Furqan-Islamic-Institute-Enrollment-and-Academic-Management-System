package com.example.testingLogIn.ModelDTO;

import com.example.testingLogIn.AssociativeModels.StudentTransfereeRequirements;
import com.example.testingLogIn.CustomObjects.Address;
import com.example.testingLogIn.Enums.Gender;
import com.example.testingLogIn.Models.GradeLevel;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalDate;
import java.util.List;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentDTO {
    private int studentId;
    
    private String studentDisplayId;
    private String firstName;
    private String lastName;
    private String middleName;
    private String fullName;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private LocalDate birthdate;
    private Address address;
    private String cellphoneNum;
    private String birthPlace;
    private double balanceAmount;
    private GradeLevel currentGradeLevel;
    private String currentGradeSection;
    
    private String motherName;
    private String motherOccupation;
    private String fatherName;
    private String fatherOccupation;
    private String guardianName;
    private String guardianAddress;
    private String guardianContactNum;
    private String guardianOccupation;

    private String status;
    private boolean isNew;
    private boolean isScholar;
    private boolean isNotDeleted;
    
    private boolean isTransferee;
    private String madrasaName;
    private Integer lastGradeLevelId;
    private String lastGradeLevelCompleted;
    private String lastMadrasaYearCompleted;
    private String madrasaAddress;
    private List<Integer> transfereeRequirements;
    private List<Integer> discountsAvailed;
}
