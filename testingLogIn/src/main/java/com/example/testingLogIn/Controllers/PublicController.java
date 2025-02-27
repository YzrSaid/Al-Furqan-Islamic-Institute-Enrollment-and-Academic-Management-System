package com.example.testingLogIn.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.*;
import java.util.Enumeration;

@RequestMapping
@Controller
public class PublicController {
    @GetMapping("/login")
    public String getPage(){
        return "LogIn";
    }
    
    @GetMapping("/signing")
    public String signPage(){
        return "signin";
    }

    @GetMapping("/ipaddress")
    public ResponseEntity<String> getIpAddress(){
        try {
            // Get the network interfaces
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            int count = 0;
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();

                // Skip loopback interfaces (like 127.0.0.1)
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }

                // Get the IP addresses for each network interface
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();

                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address instanceof java.net.Inet4Address) {
                        count++;
                        if (count == 2) {
                            return new ResponseEntity<>(address.getHostAddress(),HttpStatus.OK);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
