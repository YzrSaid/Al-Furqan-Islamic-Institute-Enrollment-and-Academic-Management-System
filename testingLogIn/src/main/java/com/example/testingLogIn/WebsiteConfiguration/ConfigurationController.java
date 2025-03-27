package com.example.testingLogIn.WebsiteConfiguration;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.Base64;

@RequestMapping("/website-config")
@Controller
public class ConfigurationController {
    @Value("${images.external-dir:./external-images}")
    private String externalImageDir;

    @PutMapping("/update")
    public ResponseEntity<String> updateSchoolInterface(@RequestBody WebsiteProfile profile) throws FileNotFoundException {
        try(ByteArrayInputStream reader = new ByteArrayInputStream(Base64.getDecoder().decode(profile.getLogoBase64()));
            FileOutputStream fos = new FileOutputStream(externalImageDir+"//al-furqanlogo.jpg")){
            byte[] buffer = new byte[1024];  //// Standard buffer size (1024 bytes = 1KB)
            int bytesRead;

            while ((bytesRead = reader.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);  // Write only the bytes that were actually read
            }
            fos.flush();
            fos.flush();
            return new ResponseEntity<>("School Profile edited successfully", HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("Transaction failed. Unexpected Error", HttpStatus.CONFLICT);
        }
    }
}
