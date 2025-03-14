package com.example.testingLogIn.Models;

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
