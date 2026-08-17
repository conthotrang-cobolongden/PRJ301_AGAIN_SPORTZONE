package com.sportzone.util;

public final class AppConstants {

    public static final String SESSION_ACCOUNT = "account";
    public static final String SESSION_ROLE = "roleName";
    public static final String SESSION_FROZEN = "frozen";
    public static final String SESSION_REDIRECT_AFTER_LOGIN = "redirectAfterLogin";

    public static final String ROLE_ADMIN = "Admin";
    public static final String ROLE_USER = "User";

    public static final int ROLE_ID_ADMIN = 1;
    public static final int ROLE_ID_USER = 2;

    public static final String ATTR_ERROR = "error";
    public static final String ATTR_SUCCESS = "success";
    public static final String ATTR_FIELD_ERRORS = "fieldErrors";

    public static final String CSRF_PARAM = "csrfToken";

    private AppConstants() {
    }
}
