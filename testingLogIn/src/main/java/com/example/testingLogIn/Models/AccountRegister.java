package com.example.testingLogIn.Models;

import com.example.testingLogIn.WebsiteSecurityConfiguration.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AccountRegister {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    Role role;

    private String firstname;
    private String lastname;

    @Override
    public String toString() {
        return "AccountRegister{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                '}';
    }
}
