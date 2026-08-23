package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import model.Account;
import utils.PasswordUtil;

/**
 * Kiem tra persistence contract cho Profile Phase 4.
 * Tags: U4, U5, U6.
 */
public final class ProfileAccountDAOContractTest {

    private ProfileAccountDAOContractTest() {
    }

    public static void main(String[] args) throws Exception {
        AccountDAO accountDAO = new AccountDAO();

        long suffix = System.currentTimeMillis();
        String originalEmail
                = "phase4-" + suffix + "@sportzone.local";
        String updatedEmail
                = "phase4-updated-" + suffix + "@sportzone.local";
        String originalPhone
                = "038" + String.format("%07d", suffix % 10000000L);
        String updatedPhone
                = "037" + String.format("%07d", suffix % 10000000L);

        int accountID = 0;

        try {
            Account account = new Account();
            account.setRoleID(2);
            account.setEmail(originalEmail);
            account.setPhone(originalPhone);
            account.setPasswordHash(
                    PasswordUtil.sha256Hex("Current@123"));
            account.setFullName("Phase Four Original");
            account.setStatus("Active");

            accountID = accountDAO.insert(account);

            require(!accountDAO.existsByEmailExcludingId(
                    originalEmail, accountID),
                    "Current email must not conflict with itself");

            require(!accountDAO.existsByPhoneExcludingId(
                    originalPhone, accountID),
                    "Current phone must not conflict with itself");

            require(accountDAO.existsByEmailExcludingId(
                    originalEmail, -1),
                    "Email must conflict when current account is not excluded");

            require(accountDAO.existsByPhoneExcludingId(
                    originalPhone, -1),
                    "Phone must conflict when current account is not excluded");

            boolean profileUpdated = accountDAO.updateProfile(
                    accountID,
                    "Phase Four Updated",
                    updatedEmail,
                    updatedPhone
            );

            require(profileUpdated,
                    "Existing account profile must be updated");

            Account updated = accountDAO.findById(accountID);

            require("Phase Four Updated".equals(updated.getFullName()),
                    "Updated full name must persist");
            require(updatedEmail.equals(updated.getEmail()),
                    "Updated email must persist");
            require(updatedPhone.equals(updated.getPhone()),
                    "Updated phone must persist");

            String newHash
                    = PasswordUtil.sha256Hex("NewPassword@123");

            boolean passwordUpdated
                    = accountDAO.updatePasswordHash(accountID, newHash);

            require(passwordUpdated,
                    "Existing account password must be updated");

            Account passwordAccount = accountDAO.findById(accountID);

            require(PasswordUtil.matches(
                    "NewPassword@123",
                    passwordAccount.getPasswordHash()),
                    "New password hash must persist");

            require(!accountDAO.updateProfile(
                    -1, "Missing", updatedEmail, updatedPhone),
                    "Missing account profile update must return false");

            require(!accountDAO.updatePasswordHash(-1, newHash),
                    "Missing account password update must return false");
        } finally {
            if (accountID > 0) {
                deleteContractAccount(accountID);
            }
        }

        System.out.println(
                "ProfileAccountDAOContractTest PASSED");
    }

    private static void deleteContractAccount(int accountID)
            throws Exception {
        String sql = "DELETE FROM Accounts WHERE AccountID = ?";

        try (Connection connection = DBUtils.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(sql)) {
            statement.setInt(1, accountID);
            statement.executeUpdate();
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}