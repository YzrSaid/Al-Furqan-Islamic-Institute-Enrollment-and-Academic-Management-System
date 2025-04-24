package com.example.testingLogIn.AssociativeModels;

import com.example.testingLogIn.ModelDTO.StudentDiscountDTO;
import com.example.testingLogIn.Models.Discount;
import com.example.testingLogIn.Models.Student;
import jakarta.persistence.*;
import lombok.*;

import java.util.Optional;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class StudentDiscount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int connectionId;

    @ManyToOne
    @JoinColumn(name = "student")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "discount")
    private Discount discount;

    private boolean isNotDeleted;

    public StudentDiscount(Student student, Discount discount){
        isNotDeleted = true;
        this.student = student;
        this.discount = discount;
    }
}
