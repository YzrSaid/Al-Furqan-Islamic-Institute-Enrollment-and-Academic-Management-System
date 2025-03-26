package com.example.testingLogIn.AssociativeModels;

import com.example.testingLogIn.Models.Discount;
import com.example.testingLogIn.Models.Student;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class StudentDiscount {

    @Id
    @GeneratedValue
    private int connectionId;

    @ManyToOne
    @JoinColumn(name = "student")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "discount")
    private Discount discount;

    private boolean isNotDeleted;
}
