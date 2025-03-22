package com.example.testingLogIn.WebsiteSecurityConfiguration;

import com.example.testingLogIn.Enums.Gender;
import com.example.testingLogIn.Enums.Role;
import com.example.testingLogIn.ModelDTO.UserDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UserModel implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int staffId;
    private String username;
    @JsonIgnore private String password;
    private boolean isNotRestricted;
    private boolean isNotDeleted;
    
    @Enumerated(EnumType.STRING)
    Role role;

    private String firstname;
    private String lastname;
    private String middlename;
    private String address;
    private String contactNumber;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private LocalDate birthdate;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getFullName(){
        return firstname+" "+ Optional.ofNullable(middlename).map(m -> m+" ").orElse("")+lastname;
    }
    
    public UserDTO mapperDTO(){
        return UserDTO.builder()
                .username(username)
                .firstname(firstname)
                .lastname(lastname)
                .address(address)
                .birthdate(birthdate)
                .gender(gender)
                .staffId(staffId)
                .role(role)
                .isNotDeleted(isNotDeleted)
                .isNotRestricted(isNotRestricted)
                .build();
    }  
}
