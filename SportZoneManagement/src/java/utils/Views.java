package utils;

/**
 * Tap trung duong dan JSP nam duoi WEB-INF.
 */
public final class Views {

    private static final String ROOT = "/WEB-INF/views/";

    public static final String HOME = ROOT + "home/index.jsp";
    public static final String LOGIN = ROOT + "auth/login.jsp";
    public static final String REGISTER = ROOT + "auth/register.jsp";
    public static final String COURT_LIST = ROOT + "court/list.jsp";
    public static final String COURT_DETAIL = ROOT + "court/detail.jsp";
    public static final String BOOKING_CREATE = ROOT + "booking/create.jsp";
    public static final String BOOKING_LIST = ROOT + "booking/list.jsp";
    public static final String BOOKING_DETAIL = ROOT + "booking/detail.jsp";
    public static final String INVOICE_LIST = ROOT + "invoice/list.jsp";
    public static final String INVOICE_DETAIL = ROOT + "invoice/detail.jsp";
    public static final String PROFILE = ROOT + "account/profile.jsp";
    public static final String PASSWORD = ROOT + "account/password.jsp";
    public static final String ACCOUNT_LOCKED
            = ROOT + "error/account-locked.jsp";

    private Views() {
    }
}
