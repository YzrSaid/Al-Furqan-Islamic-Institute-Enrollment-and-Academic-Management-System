package com.example.testingLogIn.WebsiteConfiguration;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SchoolProfile {
    @Id
    private String identifier;

    @Lob
    private byte[] key_value;

    public SchoolProfile(String identifier){
        this.identifier = identifier;
    }
}
