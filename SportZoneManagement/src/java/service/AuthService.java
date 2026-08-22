package service;

import dao.AccountDAO;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import model.Account;
import utils.PasswordUtil;
import utils.Validation;

/** Authentication and registration business rules. */
public class AuthService {

    private final AccountDAO accountDAO;

    public AuthService() {
        this(new AccountDAO());
    }

    public AuthService(AccountDAO accountDAO) {
        if (accountDAO == null) {
            throw new IllegalArgumentException("AccountDAO must not be null");
        }
        this.accountDAO = accountDAO;
    }

    public Account login(String email, String password) throws SQLException {
        String normalizedEmail = normalizeEmail(email);
        if (normalizedEmail == null || Validation.trimToNull(password) == null) {
            return null;
        }

        Account account = accountDAO.findByEmail(normalizedEmail);
        if (account == null || !PasswordUtil.matches(password, account.getPasswordHash())) {
            return null;
        }

        accountDAO.updateLastLogin(account.getAccountID());
        account.setLastLoginAt(LocalDateTime.now());
        return account;
    }

    public RegisterResult register(String fullName, String email, String phone,
            String password, String confirmPassword) throws SQLException {
        String normalizedFullName = Validation.trimToNull(fullName);
        String normalizedEmail = normalizeEmail(email);
        String normalizedPhone = normalizePhone(phone);
        LinkedHashMap<String, String> errors = new LinkedHashMap<>();

        if (normalizedFullName == null) {
            errors.put("fullName", "Họ tên không được để trống");
        } else if (normalizedFullName.length() > 100) {
            errors.put("fullName", "Họ tên tối đa 100 ký tự");
        }

        if (normalizedEmail == null) {
            errors.put("email", "Email không được để trống");
        } else if (!Validation.isEmail(normalizedEmail)) {
            errors.put("email", "Email không đúng định dạng");
        } else if (accountDAO.existsByEmail(normalizedEmail)) {
            errors.put("email", "Email đã được sử dụng");
        }

        if (normalizedPhone == null) {
            errors.put("phone", "Số điện thoại không được để trống");
        } else if (!Validation.isVietnamesePhone(normalizedPhone)) {
            errors.put("phone", "Số điện thoại không đúng định dạng Việt Nam");
        } else if (accountDAO.existsByPhone(normalizedPhone)) {
            errors.put("phone", "Số điện thoại đã được sử dụng");
        }

        if (Validation.trimToNull(password) == null) {
            errors.put("password", "Mật khẩu không được để trống");
        } else if (password.length() < 8 || password.length() > 64) {
            errors.put("password", "Mật khẩu phải từ 8 đến 64 ký tự");
        }

        if (Validation.trimToNull(confirmPassword) == null) {
            errors.put("confirmPassword", "Nhập lại mật khẩu không được để trống");
        } else if (!confirmPassword.equals(password)) {
            errors.put("confirmPassword", "Nhập lại mật khẩu không khớp");
        }

        if (!errors.isEmpty()) {
            return new RegisterResult(errors, null);
        }

        Account account = new Account();
        account.setRoleID(2);
        account.setEmail(normalizedEmail);
        account.setPhone(normalizedPhone);
        account.setPasswordHash(PasswordUtil.sha256Hex(password));
        account.setFullName(normalizedFullName);
        account.setStatus("Active");

        int accountID = accountDAO.insert(account);
        Account insertedAccount = accountDAO.findById(accountID);
        return new RegisterResult(errors, insertedAccount);
    }

    private static String normalizeEmail(String email) {
        String normalized = Validation.trimToNull(email);
        return normalized == null ? null : normalized.toLowerCase(Locale.ROOT);
    }

    private static String normalizePhone(String phone) {
        if (phone == null) {
            return null;
        }
        return Validation.trimToNull(phone.replaceAll("[\\s-]", ""));
    }

    public static final class RegisterResult {

        private final Map<String, String> fieldErrors;
        private final Account account;

        private RegisterResult(Map<String, String> fieldErrors, Account account) {
            this.fieldErrors = Collections.unmodifiableMap(
                    new LinkedHashMap<>(fieldErrors));
            this.account = account;
        }

        public boolean isSuccess() {
            return fieldErrors.isEmpty() && account != null;
        }

        public Map<String, String> getFieldErrors() {
            return fieldErrors;
        }

        public Account getAccount() {
            return account;
        }
    }
}
