package com.example.testingLogIn.AssociativeModels;

import com.example.testingLogIn.WebsiteSecurityConfiguration.UserModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class StudentSubjectGradeLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int logId;

    @ManyToOne
    @JoinColumn(name = "studSubjGrade")
    private StudentSubjectGrade ssg;

    @ManyToOne
    @JoinColumn(name = "gradedBy")
    private UserModel gradedBy;

    private LocalDateTime dateModified;

    public StudentSubjectGradeLogs(StudentSubjectGrade ssg, UserModel gradedBy) {
        this.ssg = ssg;
        this.gradedBy = gradedBy;
        dateModified = LocalDateTime.now();
    }
}
