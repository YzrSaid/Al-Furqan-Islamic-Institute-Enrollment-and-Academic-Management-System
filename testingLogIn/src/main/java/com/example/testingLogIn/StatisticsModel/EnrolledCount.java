package com.example.testingLogIn.StatisticsModel;

import com.example.testingLogIn.Models.SchoolYearSemester;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
public class EnrolledCount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int count;
    @ManyToOne
    @JoinColumn(name = "sem")
    private SchoolYearSemester sem;

    public EnrolledCount(SchoolYearSemester sem) {
        this.count = 0;
        this.sem = sem;
    }
}
