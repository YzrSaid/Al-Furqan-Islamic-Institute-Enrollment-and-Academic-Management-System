package com.example.testingLogIn.ModelDTO;

import com.example.testingLogIn.Enums.Gender;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.*;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TeacherDTO {

    private int staffId;
    private String fullname;
    private String address;
    private LocalDate birthdate;
    private String gender;
    private String contactNum;
}
