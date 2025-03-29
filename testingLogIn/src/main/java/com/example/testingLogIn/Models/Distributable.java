package com.example.testingLogIn.Models;

import com.example.testingLogIn.AssociativeModels.DistributablesPerGrade;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
public class Distributable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String itemName;
    private boolean isNotDeleted;

    @OneToMany(mappedBy = "item")
    private List<DistributablesPerGrade> gradeList;

    public Distributable(String itemName){
        this.itemName = itemName;
        this.isNotDeleted = true;
    }
}
