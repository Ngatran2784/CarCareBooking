package com.carcare.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private int userId;
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private String phone;
    private int roleId;
    private Role role;
    private List<Role> roles = new ArrayList<>();
    private String status; // ACTIVE, INACTIVE, LOCKED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User() {}

    public User(int userId, String username, String fullName, String email, String phone, int roleId, String status) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.roleId = roleId;
        this.status = status;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public int getRoleId() { return roleId; }
    public void setRoleId(int roleId) { this.roleId = roleId; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public List<Role> getRoles() { return roles; }
    public void setRoles(List<Role> roles) { this.roles = roles; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(this.status);
    }

    public boolean isLocked() {
        return "LOCKED".equalsIgnoreCase(this.status);
    }

    public boolean hasRole(String roleName) {
        if (roleName == null) return false;
        if (role != null && roleName.equalsIgnoreCase(role.getRoleName())) {
            return true;
        }
        for (Role r : roles) {
            if (r != null && roleName.equalsIgnoreCase(r.getRoleName())) {
                return true;
            }
        }
        return false;
    }

    public String getPrimaryRoleName() {
        if (role != null && role.getRoleName() != null) {
            return role.getRoleName();
        }
        if (!roles.isEmpty() && roles.get(0) != null) {
            return roles.get(0).getRoleName();
        }
        return "CUSTOMER";
    }

    @Override
    public String toString() {
        return "User{" + "userId=" + userId + ", username='" + username + "', fullName='" + fullName + "', status='" + status + "'}";
    }
}
