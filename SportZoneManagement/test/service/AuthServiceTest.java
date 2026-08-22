package service;

import dao.AccountDAO;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import model.Account;
import utils.PasswordUtil;

/** Main-method tests for authentication business rules without a database. */
public final class AuthServiceTest {

    private AuthServiceTest() {
    }

    public static void main(String[] args) throws Exception {
        testLoginAcceptsValidPasswordAndUpdatesLastLogin();
        testLoginUsesOneNullResultForUnknownEmailAndWrongPassword();
        testRegisterNormalizesAndCreatesActiveUser();
        testRegisterRejectsEveryInvalidFieldWithoutInsert();
        testRegisterReportsDuplicateEmailAndPhone();
        System.out.println("AuthServiceTest PASSED");
    }

    private static void testLoginAcceptsValidPasswordAndUpdatesLastLogin() throws Exception {
        FakeAccountDAO dao = new FakeAccountDAO();
        Account stored = account(42, "member@sportzone.local", "0901234567",
                PasswordUtil.sha256Hex("Valid@123"), "Valid Member");
        dao.emailResult = stored;

        Account result = new AuthService(dao).login("  MEMBER@SPORTZONE.LOCAL  ", "Valid@123");

        require(result == stored, "Login must return the matched account");
        require(dao.emailLookups.size() == 1, "Login must perform exactly one email lookup");
        require("member@sportzone.local".equals(dao.emailLookups.get(0)),
                "Login email must be trimmed and lowercased");
        require(dao.lastLoginUpdates.size() == 1 && dao.lastLoginUpdates.get(0) == 42,
                "Successful login must update the matching account ID");
        require(result.getLastLoginAt() != null,
                "Successful login must update the returned account timestamp");
    }

    private static void testLoginUsesOneNullResultForUnknownEmailAndWrongPassword()
            throws Exception {
        FakeAccountDAO unknownDao = new FakeAccountDAO();
        Account unknownResult = new AuthService(unknownDao)
                .login("missing@sportzone.local", "Valid@123");

        FakeAccountDAO wrongPasswordDao = new FakeAccountDAO();
        wrongPasswordDao.emailResult = account(7, "member@sportzone.local", "0901234567",
                PasswordUtil.sha256Hex("Correct@123"), "Member");
        Account wrongPasswordResult = new AuthService(wrongPasswordDao)
                .login("MEMBER@SPORTZONE.LOCAL", "Wrong@123");

        require(unknownResult == null && wrongPasswordResult == null,
                "Unknown email and wrong password must have the same null result shape");
        require(unknownDao.emailLookups.size() == 1 && wrongPasswordDao.emailLookups.size() == 1,
                "Each failed login must perform exactly one email lookup");
        require(unknownDao.lastLoginUpdates.isEmpty()
                && wrongPasswordDao.lastLoginUpdates.isEmpty(),
                "Failed login must not update last login");

        FakeAccountDAO missingInputDao = new FakeAccountDAO();
        require(new AuthService(missingInputDao).login("  ", "Valid@123") == null,
                "Missing email must return null");
        require(new AuthService(missingInputDao).login("member@sportzone.local", null) == null,
                "Missing password must return null");
        require(missingInputDao.emailLookups.isEmpty(),
                "Missing login input must not query the DAO");
    }

    private static void testRegisterNormalizesAndCreatesActiveUser() throws Exception {
        FakeAccountDAO dao = new FakeAccountDAO();
        dao.generatedID = 81;
        dao.idResult = account(81, "new.user@sportzone.local", "0901234567",
                PasswordUtil.sha256Hex("Strong@123"), "Nguyen Van A");
        dao.idResult.setRoleID(2);
        dao.idResult.setRoleName("User");
        dao.idResult.setStatus("Active");

        AuthService.RegisterResult result = new AuthService(dao).register(
                "  Nguyen Van A  ", "  NEW.USER@SPORTZONE.LOCAL  ",
                "090 123-4567", "Strong@123", "Strong@123");

        require(result.isSuccess(), "Valid registration must succeed");
        require(result.getFieldErrors().isEmpty(), "Successful registration must have no errors");
        require(result.getAccount() == dao.idResult,
                "Registration must return the account reloaded by generated ID");
        require(dao.insertedAccounts.size() == 1, "Valid registration must insert once");
        Account inserted = dao.insertedAccounts.get(0);
        require("Nguyen Van A".equals(inserted.getFullName()), "Full name must be trimmed");
        require("new.user@sportzone.local".equals(inserted.getEmail()),
                "Registration email must be trimmed and lowercased");
        require("0901234567".equals(inserted.getPhone()),
                "Registration phone must remove spaces and hyphens");
        require(inserted.getRoleID() == 2, "Registered account must use User role ID 2");
        require("Active".equals(inserted.getStatus()),
                "Registered account must start with Active status");
        require(PasswordUtil.matches("Strong@123", inserted.getPasswordHash()),
                "Registered password must be stored as a matching SHA-256 hash");
        require(dao.idLookups.size() == 1 && dao.idLookups.get(0) == 81,
                "Registration must reload the generated account ID");
    }

    private static void testRegisterRejectsEveryInvalidFieldWithoutInsert() throws Exception {
        FakeAccountDAO blankDao = new FakeAccountDAO();
        AuthService.RegisterResult blankResult = new AuthService(blankDao)
                .register("  ", null, " - ", "", null);

        Map<String, String> blankErrors = blankResult.getFieldErrors();
        require(!blankResult.isSuccess() && blankResult.getAccount() == null,
                "Invalid registration must fail without an account");
        require(blankErrors.size() == 5,
                "All five invalid registration fields must be reported together");
        require("Họ tên không được để trống".equals(blankErrors.get("fullName")),
                "Blank full name message must match");
        require("Email không được để trống".equals(blankErrors.get("email")),
                "Blank email message must match");
        require("Số điện thoại không được để trống".equals(blankErrors.get("phone")),
                "Blank phone message must match");
        require("Mật khẩu không được để trống".equals(blankErrors.get("password")),
                "Blank password message must match");
        require("Nhập lại mật khẩu không được để trống"
                .equals(blankErrors.get("confirmPassword")),
                "Blank confirmation message must match");
        require(new ArrayList<>(blankErrors.keySet()).equals(listOf(
                "fullName", "email", "phone", "password", "confirmPassword")),
                "Field errors must retain validation order");
        require(blankDao.insertedAccounts.isEmpty(), "Invalid registration must not insert");
        require(blankDao.emailExistenceLookups.isEmpty()
                && blankDao.phoneExistenceLookups.isEmpty(),
                "Syntactically invalid fields must not trigger uniqueness lookups");

        FakeAccountDAO malformedDao = new FakeAccountDAO();
        AuthService.RegisterResult malformedResult = new AuthService(malformedDao).register(
                repeat('a', 101), "not-an-email", "0123", "short", "different");
        Map<String, String> malformedErrors = malformedResult.getFieldErrors();
        require("Họ tên tối đa 100 ký tự".equals(malformedErrors.get("fullName")),
                "Long full name message must match");
        require("Email không đúng định dạng".equals(malformedErrors.get("email")),
                "Malformed email message must match");
        require("Số điện thoại không đúng định dạng Việt Nam".equals(malformedErrors.get("phone")),
                "Malformed phone message must match");
        require("Mật khẩu phải từ 8 đến 64 ký tự".equals(malformedErrors.get("password")),
                "Password length message must match");
        require("Nhập lại mật khẩu không khớp".equals(malformedErrors.get("confirmPassword")),
                "Password mismatch message must match");
        require(malformedDao.insertedAccounts.isEmpty(),
                "Malformed registration must not insert");

        boolean rejectedNullDAO = false;
        try {
            new AuthService(null);
        } catch (IllegalArgumentException expected) {
            rejectedNullDAO = true;
        }
        require(rejectedNullDAO, "AuthService must reject a null DAO");

        boolean immutableErrors = false;
        try {
            blankErrors.put("other", "not allowed");
        } catch (UnsupportedOperationException expected) {
            immutableErrors = true;
        }
        require(immutableErrors, "RegisterResult field errors must be immutable");
    }

    private static void testRegisterReportsDuplicateEmailAndPhone() throws Exception {
        FakeAccountDAO dao = new FakeAccountDAO();
        dao.emailExists = true;
        dao.phoneExists = true;

        AuthService.RegisterResult result = new AuthService(dao).register(
                "Duplicate User", "  DUPLICATE@SPORTZONE.LOCAL ",
                "090 123-4567", "Strong@123", "Strong@123");

        require(!result.isSuccess(), "Duplicate registration must fail");
        require("Email đã được sử dụng".equals(result.getFieldErrors().get("email")),
                "Duplicate email must be reported");
        require("Số điện thoại đã được sử dụng".equals(result.getFieldErrors().get("phone")),
                "Duplicate phone must be reported");
        require(dao.emailExistenceLookups.size() == 1
                && "duplicate@sportzone.local".equals(dao.emailExistenceLookups.get(0)),
                "Duplicate email lookup must use normalized email");
        require(dao.phoneExistenceLookups.size() == 1
                && "0901234567".equals(dao.phoneExistenceLookups.get(0)),
                "Duplicate phone lookup must use normalized phone");
        require(dao.insertedAccounts.isEmpty(), "Duplicate registration must not insert");
    }

    private static Account account(int id, String email, String phone,
            String passwordHash, String fullName) {
        Account account = new Account();
        account.setAccountID(id);
        account.setEmail(email);
        account.setPhone(phone);
        account.setPasswordHash(passwordHash);
        account.setFullName(fullName);
        return account;
    }

    private static List<String> listOf(String... values) {
        List<String> result = new ArrayList<>();
        for (String value : values) {
            result.add(value);
        }
        return result;
    }

    private static String repeat(char value, int count) {
        StringBuilder result = new StringBuilder(count);
        for (int index = 0; index < count; index++) {
            result.append(value);
        }
        return result.toString();
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static final class FakeAccountDAO extends AccountDAO {
        private final List<String> emailLookups = new ArrayList<>();
        private final List<Integer> idLookups = new ArrayList<>();
        private final List<String> emailExistenceLookups = new ArrayList<>();
        private final List<String> phoneExistenceLookups = new ArrayList<>();
        private final List<Account> insertedAccounts = new ArrayList<>();
        private final List<Integer> lastLoginUpdates = new ArrayList<>();
        private Account emailResult;
        private Account idResult;
        private boolean emailExists;
        private boolean phoneExists;
        private int generatedID = 1;

        @Override
        public Account findByEmail(String email) throws SQLException {
            emailLookups.add(email);
            return emailResult;
        }

        @Override
        public Account findById(int accountID) throws SQLException {
            idLookups.add(accountID);
            return idResult;
        }

        @Override
        public boolean existsByEmail(String email) throws SQLException {
            emailExistenceLookups.add(email);
            return emailExists;
        }

        @Override
        public boolean existsByPhone(String phone) throws SQLException {
            phoneExistenceLookups.add(phone);
            return phoneExists;
        }

        @Override
        public int insert(Account account) throws SQLException {
            insertedAccounts.add(account);
            return generatedID;
        }

        @Override
        public void updateLastLogin(int accountID) throws SQLException {
            lastLoginUpdates.add(accountID);
        }
    }
}
