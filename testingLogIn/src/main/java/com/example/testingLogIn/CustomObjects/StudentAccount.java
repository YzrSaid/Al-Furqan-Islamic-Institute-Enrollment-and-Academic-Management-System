package com.example.testingLogIn.CustomObjects;

import com.example.testingLogIn.WebsiteSecurityConfiguration.UserModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StudentAccount {
    private String studentDisplayId;
    private String userName;
    private String password;
    private String studentFullName;
}
