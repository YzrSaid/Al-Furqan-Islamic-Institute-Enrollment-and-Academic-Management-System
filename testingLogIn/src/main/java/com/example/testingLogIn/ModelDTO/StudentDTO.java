package com.example.testingLogIn.ModelDTO;

import com.example.testingLogIn.Enums.Gender;
import com.example.testingLogIn.Models.GradeLevel;
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
    
    private String studentDisplayId;
    private String firstName;
    private String lastName;
    private String middleName;
    private String fullName;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private LocalDate birthdate;
    private String address;
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
    
    private boolean isNew;
    private boolean isScholar;
    private boolean isNotDeleted;
    
    private boolean isTransferee;
    private String madrasaName;
    private String lastGradeLevelCompleted;
    private String lastMadrasaYearCompleted;
    private String madrasaAddress;

    @Override
    public String toString() {
        return "StudentDTO{" +
                "studentId=" + studentId +
                ", studentDisplayId='" + studentDisplayId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", gender=" + gender +
                ", birthdate=" + birthdate +
                ", address='" + address + '\'' +
                ", cellphoneNum='" + cellphoneNum + '\'' +
                ", birthPlace='" + birthPlace + '\'' +
                ", balanceAmount=" + balanceAmount +
                ", currentGradeSection='" + currentGradeSection + '\'' +
                ", motherName='" + motherName + '\'' +
                ", motherOccupation='" + motherOccupation + '\'' +
                ", fatherName='" + fatherName + '\'' +
                ", fatherOccupation='" + fatherOccupation + '\'' +
                ", guardianName='" + guardianName + '\'' +
                ", guardianAddress='" + guardianAddress + '\'' +
                ", guardianContactNum='" + guardianContactNum + '\'' +
                ", isNew=" + isNew +
                ", isScholar=" + isScholar +
                ", isNotDeleted=" + isNotDeleted +
                ", isTransferee=" + isTransferee +
                ", madrasaName='" + madrasaName + '\'' +
                ", lastGradeLevelCompleted='" + lastGradeLevelCompleted + '\'' +
                ", lastMadrasaYearCompleted='" + lastMadrasaYearCompleted + '\'' +
                ", madrasaAddress='" + madrasaAddress + '\'' +
                '}';
    }
}
