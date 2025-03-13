package com.example.testingLogIn.Models;

import com.example.testingLogIn.Enums.Gender;
import com.example.testingLogIn.ModelDTO.StudentDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.time.LocalDate;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
public class Student {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int studentId;
    
    private String studentDisplayId;
    private String firstName;
    private String lastName;
    private String middleName;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private LocalDate birthdate;
    private String address;
    private String cellphoneNum;
    private String birthPlace;
    private double studentBalance;
    
    @ManyToOne
    @JoinColumn(name = "gradeAndSection", nullable = true)
    private Section currentGradeSection;
    
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
    
    public StudentDTO DTOmapper(){
        return StudentDTO.builder()
                        .studentId(studentId)
                
                        .studentDisplayId(studentDisplayId)
                        .firstName(firstName)
                        .lastName(lastName)
                        .middleName(middleName)
                        .gender(gender)
                        .birthdate(birthdate)
                        .address(address)
                        .balanceAmount(studentBalance)
                        .currentGradeSection(currentGradeSection != null? currentGradeSection.getLevel().getLevelName()
                                            +" - "+currentGradeSection.getSectionName() : "NONE")
                
                        .motherName(motherName)
                        .motherOccupation(motherOccupation)
                        .fatherName(fatherName)
                        .fatherOccupation(fatherOccupation)
                        .guardianName(guardianName)
                        .guardianAddress(guardianAddress)
                        .guardianContactNum(guardianContactNum)
                        .isNew(isNew)
                        .isNotDeleted(isNotDeleted)
                        .isScholar(isScholar)
                
                        .isTransferee(isTransferee)
                        .madrasaName(madrasaName)
                        .lastGradeLevelCompleted(lastGradeLevelCompleted)
                        .lastMadrasaYearCompleted(lastMadrasaYearCompleted)
                        .madrasaAddress(madrasaAddress)
                        .build();
    }

    @Override
    public String toString() {
        return "Student{" +
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
                ", studentBalance=" + studentBalance +
                ", currentGradeSection=" + currentGradeSection +
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
