package com.example.testingLogIn.Models;

import com.example.testingLogIn.Enums.Gender;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Student {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int studentId;
    
    private String firstName;
    private String lastName;
    private String middleName;
    private String contactNum;
    private String motherName;
    private String fatherName;
    
    @OneToOne
    @JoinColumn(name = "gradeAndSection", nullable = true)
    private Section gradeAndSection;
    
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private LocalDate birthdate;
    @JsonProperty("isNew")
    private boolean isNew;
    @JsonProperty("isTransferee")
    private boolean isTransferee;
    @JsonProperty("isScholar")
    private boolean isScholar;
    @JsonProperty("isNotDeleted")
    private boolean isNotDeleted;
}
