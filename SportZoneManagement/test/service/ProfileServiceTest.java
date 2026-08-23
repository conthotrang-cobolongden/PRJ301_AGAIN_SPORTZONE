package service;

import dao.AccountDAO;
import java.util.Map;
import model.Account;
import utils.PasswordUtil;

/**
 * Test nghiep vu cap nhat ho so va doi mat khau.
 * Tags: U4, U5, U6.
 */
public final class ProfileServiceTest {

    private ProfileServiceTest() {
    }

    public static void main(String[] args) throws Exception {
        testUpdateProfileNormalizesAndReloadsAccount();
        testUpdateProfileRejectsInvalidFields();
        testUpdateProfileRejectsDuplicateEmailAndPhone();
        testChangePasswordUpdatesHash();
        testChangePasswordRejectsWrongCurrentPassword();
        testChangePasswordRejectsInvalidNewPassword();

        System.out.println("ProfileServiceTest PASSED");
    }

    private static void testUpdateProfileNormalizesAndReloadsAccount()
            throws Exception {
        FakeAccountDAO dao = new FakeAccountDAO();
        dao.reloadedAccount = account(
                7,
                "member@sportzone.local",
                "0901234567",
                "Updated Member",
                PasswordUtil.sha256Hex("Current@123")
        );

        ProfileService.UpdateProfileResult result
                = new ProfileService(dao).updateProfile(
                        7,
                        "  Updated Member  ",
                        "  MEMBER@SPORTZONE.LOCAL ",
                        "0901-234-567"
                );

        require(result.isSuccess(),
                "Valid profile update must succeed");
        require(dao.updateProfileCalls == 1,
                "Valid profile update must write exactly once");
        require(dao.updatedAccountID == 7,
                "Profile update must use logged-in account ID");
        require("Updated Member".equals(dao.updatedFullName),
                "Full name must be trimmed");
        require("member@sportzone.local".equals(dao.updatedEmail),
                "Email must be trimmed and lowercased");
        require("0901234567".equals(dao.updatedPhone),
                "Phone must remove spaces and hyphens");
        require(result.getAccount() == dao.reloadedAccount,
                "Successful update must return the account reloaded from DB");
    }

    private static void testUpdateProfileRejectsInvalidFields()
            throws Exception {
        FakeAccountDAO dao = new FakeAccountDAO();

        ProfileService.UpdateProfileResult result
                = new ProfileService(dao).updateProfile(
                        7,
                        " ",
                        "not-an-email",
                        "0123"
                );

        Map<String, String> errors = result.getFieldErrors();

        require(!result.isSuccess(),
                "Invalid profile must fail");
        require(errors.containsKey("name"),
                "Blank full name must use JSP field key name");
        require(errors.containsKey("email"),
                "Invalid email must report email error");
        require(errors.containsKey("phone"),
                "Invalid phone must report phone error");
        require(dao.updateProfileCalls == 0,
                "Invalid profile must not write to DB");
        require(dao.emailChecks == 0 && dao.phoneChecks == 0,
                "Syntactically invalid values must not query uniqueness");
    }

    private static void testUpdateProfileRejectsDuplicateEmailAndPhone()
            throws Exception {
        FakeAccountDAO dao = new FakeAccountDAO();
        dao.duplicateEmail = true;
        dao.duplicatePhone = true;

        ProfileService.UpdateProfileResult result
                = new ProfileService(dao).updateProfile(
                        7,
                        "Valid Member",
                        "used@sportzone.local",
                        "0901234567"
                );

        require(!result.isSuccess(),
                "Duplicate profile values must fail");
        require("Email đã được sử dụng".equals(
                result.getFieldErrors().get("email")),
                "Duplicate email must have friendly error");
        require("Số điện thoại đã được sử dụng".equals(
                result.getFieldErrors().get("phone")),
                "Duplicate phone must have friendly error");
        require(dao.excludedAccountID == 7,
                "Uniqueness checks must exclude current account");
        require(dao.updateProfileCalls == 0,
                "Duplicate values must not be written");
    }

    private static void testChangePasswordUpdatesHash()
            throws Exception {
        FakeAccountDAO dao = new FakeAccountDAO();
        dao.reloadedAccount = account(
                7,
                "member@sportzone.local",
                "0901234567",
                "Member",
                PasswordUtil.sha256Hex("Current@123")
        );

        ProfileService.ChangePasswordResult result
                = new ProfileService(dao).changePassword(
                        7,
                        "Current@123",
                        "NewPassword@123",
                        "NewPassword@123"
                );

        require(result.isSuccess(),
                "Correct current password must allow password change");
        require(dao.updatePasswordCalls == 1,
                "Password must be updated exactly once");
        require(dao.updatedAccountID == 7,
                "Password update must use logged-in account ID");
        require(PasswordUtil.matches(
                "NewPassword@123", dao.updatedPasswordHash),
                "DAO must receive SHA-256 hash of new password");
        require(!"NewPassword@123".equals(dao.updatedPasswordHash),
                "DAO must never receive raw password");
    }

    private static void testChangePasswordRejectsWrongCurrentPassword()
            throws Exception {
        FakeAccountDAO dao = new FakeAccountDAO();
        dao.reloadedAccount = account(
                7,
                "member@sportzone.local",
                "0901234567",
                "Member",
                PasswordUtil.sha256Hex("Current@123")
        );

        ProfileService.ChangePasswordResult result
                = new ProfileService(dao).changePassword(
                        7,
                        "Wrong@123",
                        "NewPassword@123",
                        "NewPassword@123"
                );

        require(!result.isSuccess(),
                "Wrong current password must fail");
        require("Mật khẩu hiện tại không đúng".equals(
                result.getFieldErrors().get("currentPassword")),
                "Wrong current password must use currentPassword field");
        require(dao.updatePasswordCalls == 0,
                "Wrong current password must not update DB");
    }

    private static void testChangePasswordRejectsInvalidNewPassword()
            throws Exception {
        FakeAccountDAO dao = new FakeAccountDAO();

        ProfileService.ChangePasswordResult result
                = new ProfileService(dao).changePassword(
                        7,
                        "",
                        "short",
                        "different"
                );

        require(!result.isSuccess(),
                "Invalid password form must fail");
        require(result.getFieldErrors().containsKey("currentPassword"),
                "Missing current password must report its field");
        require(result.getFieldErrors().containsKey("newPassword"),
                "Short new password must report its field");
        require(result.getFieldErrors().containsKey("confirmPassword"),
                "Mismatched confirmation must report its field");
        require(dao.findByIdCalls == 0,
                "Invalid password form must not query account");
        require(dao.updatePasswordCalls == 0,
                "Invalid password form must not update DB");
    }

    private static Account account(int accountID, String email,
            String phone, String fullName, String passwordHash) {
        Account account = new Account();
        account.setAccountID(accountID);
        account.setEmail(email);
        account.setPhone(phone);
        account.setFullName(fullName);
        account.setPasswordHash(passwordHash);
        account.setRoleID(2);
        account.setRoleName("User");
        account.setStatus("Active");
        return account;
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static final class FakeAccountDAO extends AccountDAO {

        private boolean duplicateEmail;
        private boolean duplicatePhone;
        private Account reloadedAccount;

        private int emailChecks;
        private int phoneChecks;
        private int findByIdCalls;
        private int updateProfileCalls;
        private int updatePasswordCalls;
        private int excludedAccountID;
        private int updatedAccountID;

        private String updatedFullName;
        private String updatedEmail;
        private String updatedPhone;
        private String updatedPasswordHash;

        @Override
        public boolean existsByEmailExcludingId(
                String email, int accountID) {
            emailChecks++;
            excludedAccountID = accountID;
            return duplicateEmail;
        }

        @Override
        public boolean existsByPhoneExcludingId(
                String phone, int accountID) {
            phoneChecks++;
            excludedAccountID = accountID;
            return duplicatePhone;
        }

        @Override
        public boolean updateProfile(int accountID, String fullName,
                String email, String phone) {
            updateProfileCalls++;
            updatedAccountID = accountID;
            updatedFullName = fullName;
            updatedEmail = email;
            updatedPhone = phone;
            return true;
        }

        @Override
        public boolean updatePasswordHash(
                int accountID, String passwordHash) {
            updatePasswordCalls++;
            updatedAccountID = accountID;
            updatedPasswordHash = passwordHash;
            return true;
        }

        @Override
        public Account findById(int accountID) {
            findByIdCalls++;
            return reloadedAccount;
        }
    }
}