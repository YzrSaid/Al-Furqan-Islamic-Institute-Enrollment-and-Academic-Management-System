package com.example.testingLogIn.ModelDTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StudentFeesListDTO {
    private StudentDTO student;
    private String schoolYearSem;
    private List<RequiredPaymentsDTO> feesList;
}
