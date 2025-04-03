package com.example.testingLogIn.WebsiteConfiguration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Base64;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebsiteProfile {
    private Integer graduatingLevel;
    private String schoolName;
    private String schoolAddress;
    private String schoolEmail;
    private String schoolContact;
    private String themeColor;
    private String logoBase64;
}
