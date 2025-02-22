package com.example.testingLogIn.ModelDTO;

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
