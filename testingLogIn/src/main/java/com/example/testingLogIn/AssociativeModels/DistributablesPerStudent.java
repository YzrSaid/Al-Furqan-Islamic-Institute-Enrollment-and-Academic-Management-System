package com.example.testingLogIn.AssociativeModels;

import com.example.testingLogIn.Models.Distributables;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
public class DistributablesPerStudent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "item",nullable = false)
    private Distributables item;
    private boolean isReceived;
}
