package com.example.testingLogIn.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 *
 * @author magno
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class SchoolYear {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int schoolYearNum;
    
    private String schoolYear;
    private boolean isNotDeleted;

    @OneToMany(mappedBy = "schoolYear")
    @JsonIgnore
    List<SchoolYearSemester> semesterList;
}
