package com.example.testingLogIn.Models;

import com.example.testingLogIn.AssociativeModels.DistributablesPerGrade;
import com.example.testingLogIn.ModelDTO.DistributableDTO;
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
    private int itemId;

    @Column(nullable = false)
    private String itemName;
    private boolean isNotDeleted;
    private boolean isCurrentlyActive;

    @OneToMany(mappedBy = "item")
    private List<DistributablesPerGrade> gradeList;

    public Distributable(String itemName,boolean isCurrentlyActive){
        this.itemName = itemName;
        this.isNotDeleted = true;
        this.isCurrentlyActive = isCurrentlyActive;
    }

    public DistributableDTO DTOmapper(){
        return DistributableDTO.builder()
                .itemId(itemId)
                .itemName(itemName)
                .gradeLevelList(gradeList.stream().filter(DistributablesPerGrade::isNotDeleted).map(DistributablesPerGrade::getGradeLevel).toList())
                .isCurrentlyActive(isCurrentlyActive)
                .build();
    }
}
