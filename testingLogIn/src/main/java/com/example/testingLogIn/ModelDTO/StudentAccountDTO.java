package com.example.testingLogIn.ModelDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentAccountDTO {
    private String userName;
    private String password;
    private StudentDTO student;
}
