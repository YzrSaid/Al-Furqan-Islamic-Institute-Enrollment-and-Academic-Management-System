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
public class StudentDTO {
    private int studentId;

    private String firstName;
    private String lastName;
    private String middleName;
    private String contactNum;
    private String motherName;
    private String fatherName;

    private String gender;
    private LocalDate birthdate;
    private boolean isNew;
    
    @JsonProperty()
    private boolean isTransferee;

    private boolean isScholar;

    private boolean isNotDeleted;
}
