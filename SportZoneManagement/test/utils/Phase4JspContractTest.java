package utils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/** Static form/security contract for the two Phase 4 JSP pages. */
public final class Phase4JspContractTest {

    private Phase4JspContractTest() {
    }

    public static void main(String[] args) throws Exception {
        String profile = read("web/WEB-INF/views/account/profile.jsp");
        String password = read("web/WEB-INF/views/account/password.jsp");

        require(profile.contains("method=\"post\" action=\"${ctx}/MainController?action=profile\""),
                "Profile form must POST through MainController");
        require(profile.contains("name=\"csrfToken\""),
                "Profile form must retain csrfToken");
        require(profile.contains("name=\"fullName\"")
                && profile.contains("name=\"email\"")
                && profile.contains("name=\"phone\""),
                "Profile form must retain fullName/email/phone names");
        require(profile.contains("fieldErrors.name")
                && profile.contains("fieldErrors.email")
                && profile.contains("fieldErrors.phone"),
                "Profile form must retain all field error keys");
        require(profile.contains("name=\"email\" maxlength=\"100\""),
                "Profile email maxlength must match Accounts.Email NVARCHAR(100)");
        require(!profile.contains(
                "<fmt:formatDate value=\"${sessionScope.account.createdAt}\""),
                "Profile JSP must not pass LocalDateTime to fmt:formatDate");
        require(profile.contains("${memberSince}"),
                "Profile JSP must render the servlet-formatted memberSince value");
        require(profile.contains("value='<c:out value='${account.fullName}'/>"
                + "'") || profile.contains("value=\"<c:out value='${account.fullName}'/>\""),
                "Profile fullName must come from escaped request account");

        require(password.contains("method=\"post\" action=\"${ctx}/MainController?action=password\""),
                "Password form must POST through MainController");
        require(password.contains("name=\"csrfToken\""),
                "Password form must retain csrfToken");
        require(password.contains("name=\"currentPassword\"")
                && password.contains("name=\"newPassword\"")
                && password.contains("name=\"confirmPassword\""),
                "Password form must retain all password field names");
        require(count(password, "maxlength=\"64\"") == 3,
                "All three password controls must enforce the server maximum of 64");
        require(password.contains("fieldErrors.currentPassword")
                && password.contains("fieldErrors.newPassword")
                && password.contains("fieldErrors.confirmPassword"),
                "Password form must retain all field error keys");
        require(password.contains("autocomplete=\"current-password\"")
                && count(password, "autocomplete=\"new-password\"") == 2,
                "Password autocomplete security hints must be retained");

        System.out.println("Phase4JspContractTest PASSED");
    }

    private static String read(String path) throws Exception {
        return new String(Files.readAllBytes(Paths.get(path)),
                StandardCharsets.UTF_8);
    }

    private static int count(String source, String target) {
        int result = 0;
        int start = 0;
        while ((start = source.indexOf(target, start)) >= 0) {
            result++;
            start += target.length();
        }
        return result;
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
