package utils;

/**
 * Tap trung cac gia tri dung chung cua SportZone.
 */
public final class AppConstants {

    public static final String ROLE_ADMIN = "Admin";
    public static final String ROLE_USER = "User";

    public static final String ACCOUNT_ACTIVE = "Active";
    public static final String ACCOUNT_FROZEN = "Frozen";
    public static final String ACCOUNT_PENDING = "Pending";

    public static final String SESSION_ACCOUNT = "account";
    public static final String SESSION_ROLE_NAME = "roleName";
    public static final String SESSION_FROZEN = "frozen";
    public static final String SESSION_CSRF_TOKEN = "csrfToken";
    public static final String SESSION_REDIRECT_AFTER_LOGIN = "redirectAfterLogin";

    public static final String ATTRIBUTE_FIELD_ERRORS = "fieldErrors";
    public static final String ATTRIBUTE_ERROR_MESSAGE = "errorMessage";

    private AppConstants() {
    }
}
