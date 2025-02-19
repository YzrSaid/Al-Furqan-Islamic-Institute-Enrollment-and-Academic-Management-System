/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.Models.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author magno
 */
public interface SubjectRepo extends JpaRepository<Subject, Integer>{
    
}
