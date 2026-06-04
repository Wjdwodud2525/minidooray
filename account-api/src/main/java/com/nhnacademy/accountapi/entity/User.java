package com.nhnacademy.accountapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class User {
    @Id
    private String id;

    @Setter
    private String password;
    private String email;
    private ZonedDateTime createdAt;
    private ZonedDateTime lastLoginAt;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    public String getUserId() {
        return id;
    }
}
