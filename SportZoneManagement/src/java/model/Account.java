package model;

import java.time.LocalDateTime;

/** Model cua bang Accounts, kem roleName phuc vu view. Tag: A1, A4, U1, U4. */
public class Account {
    private int accountID;
    private int roleID;
    private String email;
    private String phone;
    private String passwordHash;
    private String fullName;
    private String status;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private String roleName;

    public Account() { }
    public Account(int accountID, int roleID, String email, String phone, String passwordHash,
            String fullName, String status, LocalDateTime lastLoginAt, LocalDateTime createdAt) {
        this.accountID = accountID; this.roleID = roleID; this.email = email; this.phone = phone;
        this.passwordHash = passwordHash; this.fullName = fullName; this.status = status;
        this.lastLoginAt = lastLoginAt; this.createdAt = createdAt;
    }
    public int getAccountID() { return accountID; }
    public void setAccountID(int accountID) { this.accountID = accountID; }
    public int getRoleID() { return roleID; }
    public void setRoleID(int roleID) { this.roleID = roleID; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
}
