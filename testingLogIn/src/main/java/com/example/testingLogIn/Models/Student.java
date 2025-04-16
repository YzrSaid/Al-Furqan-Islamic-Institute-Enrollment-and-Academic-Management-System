package com.example.testingLogIn.Models;

import com.example.testingLogIn.AssociativeModels.StudentDiscount;
import com.example.testingLogIn.AssociativeModels.StudentTransfereeRequirements;
import com.example.testingLogIn.CustomObjects.Address;
import com.example.testingLogIn.Enums.Gender;
import com.example.testingLogIn.Enums.StudentStatus;
import com.example.testingLogIn.ModelDTO.StudentDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.*;
import org.hibernate.annotations.Fetch;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(indexes = {@Index(name = "idx_full_name", columnList = "full_name")})
public class Student{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int studentId;
    
    private String studentDisplayId;
    private String firstName;
    private String lastName;
    private String middleName;
    private String fullName;

    @Enumerated(EnumType.STRING)
    private Gender gender;
    private LocalDate birthdate;
    private String street;
    private String barangay;
    private String city;
    private String cellphoneNum;
    private String birthPlace;
    private double studentBalance;
    
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "gradeAndSection", nullable = true)
    private Section currentGradeSection;
    
    private String motherName;
    private String motherOccupation;
    private String fatherName;
    private String fatherOccupation;
    private String guardianName;
    private String guardianAddress;
    private String guardianContactNum;
    private String guardianOccupation;

    @Enumerated(EnumType.STRING)
    private StudentStatus status;
    private LocalDate dataGraduated;
    private boolean isNew;
    private boolean isScholar;
    private boolean isNotDeleted;
    
    private boolean isTransferee;
    private String madrasaName;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name="lastGradeLevelCompleted")
    private GradeLevel lastGradeLevelCompleted;
    private String lastMadrasaYearCompleted;
    private String madrasaAddress;

    @OneToMany(mappedBy = "student")
    @JsonIgnore
    private List<StudentTransfereeRequirements> transfereeRequirements;

    @OneToMany(mappedBy = "student")
    @JsonIgnore
    private List<StudentDiscount> discounts;
    
    public StudentDTO DTOmapper(){
        GradeLevel currentLevel = null;
        if(isNew && isTransferee){
            currentLevel = lastGradeLevelCompleted;
            for(StudentTransfereeRequirements req : transfereeRequirements){
                if(!req.isNotDeleted()){
                    currentLevel = null;
                    break;
                }
            }
        }
        else
            currentLevel = Optional.ofNullable(currentGradeSection).map(Section::getLevel).orElse(null);

        return StudentDTO.builder()
                        .studentId(studentId)
                        .studentDisplayId(studentDisplayId)
                        .firstName(firstName)
                        .lastName(lastName)
                        .middleName(middleName)
                        .fullName(fullName)
                        .gender(gender)
                        .birthdate(birthdate)
                        .birthPlace(birthPlace)
                        .address(Address.builder().street(street).barangay(barangay).city(city).build())
                        .balanceAmount(studentBalance)
                        .currentGradeLevel(currentLevel)
                        .currentGradeSection(Optional.ofNullable(currentGradeSection).map(Section::toString).orElse("NONE"))
                
                        .motherName(motherName)
                        .motherOccupation(motherOccupation)
                        .fatherName(fatherName)
                        .fatherOccupation(fatherOccupation)
                        .guardianName(guardianName)
                        .guardianAddress(guardianAddress)
                        .guardianContactNum(guardianContactNum)
                        .status(status.toString())
                        .isNew(status == StudentStatus.NEW)
                        .isNotDeleted(isNotDeleted)
                        .isScholar(isScholar)
                        .isTransferee(isTransferee)
                        .madrasaName(madrasaName)
                        .lastGradeLevelCompleted(Optional.ofNullable(lastGradeLevelCompleted).map(GradeLevel::getLevelName).orElse(null))
                        .lastMadrasaYearCompleted(lastMadrasaYearCompleted)
                        .madrasaAddress(madrasaAddress)
                        .transfereeRequirements(transfereeRequirements.stream().filter(StudentTransfereeRequirements::isNotDeleted).map(s-> s.getRequirement().getId()).toList())
                        .discountsAvailed(discounts.stream().filter(StudentDiscount::isNotDeleted).map(sd -> sd.getDiscount().getDiscountId()).toList())
                        .build();
    }
}
