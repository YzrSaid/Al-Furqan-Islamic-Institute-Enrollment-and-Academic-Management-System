/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.testingLogIn.ModelDTO;

import com.example.testingLogIn.Enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private int staffId;
    private String username;
    private boolean isNotRestricted;

    @Enumerated(EnumType.STRING)
    Role role;

    private String firstname;
    private String lastname;
}
