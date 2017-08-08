package com.finance.model;

import com.finance.enums.UserRole;
import com.finance.enums.Zone;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

public class User {

    @Id
    private String userId;
    private String username;
    private String password;
    private Zone zone;
    private UserRole userRole;
    private LocalDateTime lastRecordAssignedOn;
    private String fullName;

    public String getUserId() {
        return userId;
    }

    public Zone getZone() {
        return zone;
    }

    public UserRole getUserRole() {
        return userRole;
    }

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