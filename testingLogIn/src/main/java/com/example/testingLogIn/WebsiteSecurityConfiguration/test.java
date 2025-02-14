package com.example.testingLogIn.WebsiteSecurityConfiguration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Enumeration;

public class test {
    public static void main(String[] args) {
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
                        System.out.println("Inside : "+ count);
                        if (count == 2) {
                            System.out.println("Second Private IP Address: " + address.getHostAddress());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
