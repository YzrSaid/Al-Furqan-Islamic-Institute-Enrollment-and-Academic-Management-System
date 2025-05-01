package com.example.testingLogIn.ModelDTO;

import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GradeLogsDTO {

    private String modifiedBY;
    private String modifiedOn;

    public GradeLogsDTO(String modifiedBY, String modifiedOn) {
        this.modifiedBY = modifiedBY;
        this.modifiedOn = modifiedOn;
    }
}
