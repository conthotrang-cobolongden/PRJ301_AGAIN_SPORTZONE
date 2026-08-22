package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import model.Account;

/**
 * Kiem tra contract persistence cua AccountDAO tren SportZoneDB that.
 */
public final class AccountDAOContractTest {

    private static final String USER_PASSWORD_HASH
            = "3e7c19576488862816f13b512cacf3e4ba97dd97243ea0bd6a2ad1642d86ba72";

    private AccountDAOContractTest() {
    }

    public static void main(String[] args) throws Exception {
        AccountDAO accountDAO = new AccountDAO();
        Account admin = accountDAO.findByEmail("admin@sportzone.local");

        require(admin != null, "Admin seed account must exist");
        require("Admin".equals(admin.getRoleName()), "Admin seed account must have Admin role");
        require(accountDAO.existsByEmail("user1@sportzone.local"),
                "User seed email must exist");
        require(accountDAO.existsByPhone("0902000001"),
                "User seed phone must exist");

        long suffix = System.currentTimeMillis();
        String email = "phase2-" + suffix + "@sportzone.local";
        String phone = "039" + String.format("%07d", suffix % 10000000L);
        int accountID = 0;

        try {
            Account account = new Account();
            account.setRoleID(2);
            account.setEmail(email);
            account.setPhone(phone);
            account.setPasswordHash(USER_PASSWORD_HASH);
            account.setFullName("Phase Two Contract");
            account.setStatus("Active");

            accountID = accountDAO.insert(account);
            require(accountID > 0, "Insert must return a generated account ID");

            Account inserted = accountDAO.findById(accountID);
            require(inserted != null, "Inserted account must be reloadable by ID");
            require("User".equals(inserted.getRoleName()), "Inserted account must have User role");

            java.time.LocalDateTime previousLastLogin = inserted.getLastLoginAt();
            accountDAO.updateLastLogin(accountID);
            Account updated = accountDAO.findById(accountID);
            require(updated.getLastLoginAt() != null, "Last login timestamp must be updated");
            require(previousLastLogin == null
                    || updated.getLastLoginAt().isAfter(previousLastLogin),
                    "Last login timestamp must advance after update");
        } finally {
            if (accountID > 0) {
                deleteContractAccount(accountID, email);
            }
        }

        System.out.println("AccountDAOContractTest PASSED");
    }

    private static void deleteContractAccount(int accountID, String email) throws Exception {
        String sql = "DELETE FROM Accounts WHERE AccountID = ? AND Email = ?";
        try (Connection connection = DBUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, accountID);
            statement.setString(2, email);
            statement.executeUpdate();
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
