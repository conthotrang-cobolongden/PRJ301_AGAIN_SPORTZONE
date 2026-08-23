package utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Tao va kiem tra CSRF token cho moi form POST.
 */
public final class CsrfUtil {

    public static final String PARAMETER_NAME = "csrfToken";
    private static final SecureRandom RANDOM = new SecureRandom();

    private CsrfUtil() {
    }

    // Nếu session đã có CSRF token hợp lệ thì trả lại token đó, không tạo mới
    public static String ensureToken(HttpSession session) {
        Object current = session.getAttribute(AppConstants.SESSION_CSRF_TOKEN);
        if (current instanceof String && !((String) current).isEmpty()) {
            return (String) current;
        }

        byte[] randomBytes = new byte[32];
        RANDOM.nextBytes(randomBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        session.setAttribute(AppConstants.SESSION_CSRF_TOKEN, token);
        return token;
    }

    public static boolean isValid(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        Object expected = session.getAttribute(AppConstants.SESSION_CSRF_TOKEN);
        String actual = request.getParameter(PARAMETER_NAME);
        // Nếu token lấy từ session không phải String hoặc không tồn tại thì không hợp lệ
        if (!(expected instanceof String)) {
            return false;
        }
        // Nếu request không gửi csrfToken lên thì không hợp lệ
        if (actual == null) {
            return false;
        }

        String expectedToken = (String) expected;

        // Chuyển 2 token thành byte[] để so sánh
        byte[] expectedBytes = expectedToken.getBytes(StandardCharsets.UTF_8);
        byte[] actualBytes = actual.getBytes(StandardCharsets.UTF_8);

        return MessageDigest.isEqual(expectedBytes, actualBytes);
    }
}
