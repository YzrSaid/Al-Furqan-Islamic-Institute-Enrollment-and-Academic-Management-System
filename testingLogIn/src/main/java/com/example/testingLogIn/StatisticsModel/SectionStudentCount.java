package com.example.testingLogIn.StatisticsModel;

import com.example.testingLogIn.Models.SchoolYearSemester;
import com.example.testingLogIn.Models.Section;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class SectionStudentCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idNumber;

    @ManyToOne
    @JoinColumn(name="section",nullable = false)
    private Section section;

    @ManyToOne
    @JoinColumn(name="sySem",nullable = false)
    private SchoolYearSemester sySem;

    private int studentCount;
}
