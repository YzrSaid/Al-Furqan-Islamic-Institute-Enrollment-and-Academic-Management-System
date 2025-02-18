/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.testingLogIn.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
public class Section {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int number;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gradeLevel")
    private GradeLevel level;
    
    private int capacity;
    private boolean isNotDeleted;
    
}
