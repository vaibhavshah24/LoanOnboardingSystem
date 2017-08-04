package com.finance.model;

import com.finance.enums.UserRole;
import com.finance.enums.Zone;
import org.springframework.cglib.core.Local;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedList;

public class User {

    @Id
    private String userId;

    public String getUserId() {
        return userId;
    }

    public Zone getZone() {
        return zone;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    private String username;

    private String password;
    private Zone zone;
    private UserRole userRole;
    private LocalDateTime lastRecordAssignedOn;
    private String fullName;

    public String getFullName() {
        return fullName;
    }

    public void setLastRecordAssignedOn(LocalDateTime lastRecordAssignedOn) {
        this.lastRecordAssignedOn = lastRecordAssignedOn;
    }

    public User(String userId, String username, String password, Zone zone, UserRole userRole, String fullName) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.zone = zone;
        this.userRole = userRole;
        this.fullName = fullName;
    }
}