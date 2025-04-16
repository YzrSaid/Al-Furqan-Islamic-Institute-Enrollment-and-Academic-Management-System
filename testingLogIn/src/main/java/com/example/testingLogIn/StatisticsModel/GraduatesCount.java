package com.example.testingLogIn.StatisticsModel;

import com.example.testingLogIn.Models.SchoolYearSemester;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
public class GraduatesCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "sem")
    private SchoolYearSemester sem;

    private int count;

    public GraduatesCount(int count, SchoolYearSemester sem) {
        this.count = count;
        this.sem = sem;
    }
}
