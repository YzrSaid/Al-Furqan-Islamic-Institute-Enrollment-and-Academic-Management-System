package com.example.testingLogIn.Models;

import com.example.testingLogIn.ModelDTO.ScheduleDTO;
import com.example.testingLogIn.WebsiteSecurityConfiguration.UserModel;
import jakarta.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import lombok.*;

/**
 *
 * @author magno
 */
@Getter
@Setter
@ToString
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
                          .subjectId(subject.getSubjectNumber())
                          .subject(subject.getSubjectName())
                          .sectionName(section.getSectionName())
                          .day(day)
                          .timeStartString(timeStart.format(DateTimeFormatter.ofPattern("hh:mm a")))
                          .timeEndString(timeEnd.format(DateTimeFormatter.ofPattern("hh:mm a")))
                          .isNotDeleted(isNotDeleted)
                          .build();
    }
}
