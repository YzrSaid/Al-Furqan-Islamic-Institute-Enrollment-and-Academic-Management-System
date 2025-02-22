package com.example.testingLogIn.Models;

import com.example.testingLogIn.ModelDTO.ScheduleDTO;
import com.example.testingLogIn.WebsiteSecurityConfiguration.UserModel;
import jakarta.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author magno
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Schedule {
 
    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY)
    private int scheduleNumber;
    
    @ManyToOne
    @JoinColumn(name="teacher")
    private UserModel teacher;
    
    @ManyToOne
    @JoinColumn(name = "subject")
    private Subject subject;
    
    @ManyToOne
    @JoinColumn(name = "section")
    private Section section;
    
    @Enumerated(EnumType.STRING)
    private DayOfWeek day;
    private LocalTime timeStart;
    private LocalTime timeEnd;
    private boolean isNotDeleted;
    public ScheduleDTO mapper(){
        return ScheduleDTO.builder()
                          .scheduleNumber(scheduleNumber)
                          .teacherName(teacher.getFirstname()+" "+teacher.getLastname())
                          .subject(subject.getSubjectName())
                          .sectionName(section.getSectionName())
                          .day(day)
                          .timeStart(timeStart)
                          .timeEnd(timeEnd)
                          .isNotDeleted(isNotDeleted)
                          .build();
    }
}
