package com.example.testingLogIn.ModelDTO;

import java.time.DayOfWeek;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleDTO {
    private int scheduleNumber;
    private String teacherName;
    private String subject;
    private int subjectId;

    private int sectionId;
    private String gradeLevel;
    private String sectionName;
    private String sectionAdviser;

    private DayOfWeek day;
    private LocalTime timeStart;
    private String timeStartString;
    private LocalTime timeEnd;
    private String timeEndString;
    private boolean isNotDeleted;
    
    private Integer gradedCount;
    private Integer toBeGradedCount;
}
