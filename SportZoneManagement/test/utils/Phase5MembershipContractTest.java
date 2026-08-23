package utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

public class Phase5MembershipContractTest {

    public static void main(String[] args) throws Exception {
        String invoiceDAO = readProjectFile("src/java/dao/InvoiceDAO.java");
        String membershipService
                = readProjectFile("src/java/service/MembershipService.java");
        String profileServlet
                = readProjectFile("src/java/controller/ProfileServlet.java");
        String profileJsp
                = readProjectFile("web/WEB-INF/views/account/profile.jsp");

        shouldImplementPaidInvoiceSpendingQuery(invoiceDAO);
        shouldProvideDefaultMembershipService(membershipService);
        shouldBindMembershipToProfile(profileServlet);
        shouldDisplayMembershipOnProfile(profileJsp);
        shouldPreserveExistingProfileContracts(profileJsp);

        System.out.println("Phase5MembershipContractTest PASSED");
    }

    private static void shouldImplementPaidInvoiceSpendingQuery(
            String source) {

        String normalized = normalize(source);

        require(
                source.contains("DBUtils.getConnection()"),
                "InvoiceDAO phai mo connection bang DBUtils"
        );

        require(
                source.contains("PreparedStatement"),
                "InvoiceDAO phai dung PreparedStatement"
        );

        require(
                normalized.contains("JOIN BOOKINGS"),
                "InvoiceDAO phai JOIN Bookings de loc theo AccountID"
        );

        require(
                normalized.contains("FINALAMOUNT"),
                "Tong chi tieu phai tinh tu Invoices.FinalAmount"
        );

        require(
                normalized.contains("PAYMENTSTATUS = N'PAID'")
                || normalized.contains("PAYMENTSTATUS = ?"),
                "Chi duoc tinh invoice co PaymentStatus = Paid"
        );

        require(
                normalized.contains("PAIDAT >= ?"),
                "Khoang thoi gian phai bao gom moc bat dau"
        );

        require(
                normalized.contains("PAIDAT < ?"),
                "Khoang thoi gian phai loai tru moc ket thuc"
        );

        require(
                source.contains("setInt("),
                "InvoiceDAO phai bind AccountID"
        );

        require(
                source.contains("setTimestamp("),
                "InvoiceDAO phai bind moc thoi gian"
        );

        require(
                source.contains("getBigDecimal("),
                "InvoiceDAO phai doc tong tien bang BigDecimal"
        );
    }

    private static void shouldProvideDefaultMembershipService(
            String source) {

        require(
                source.contains("public MembershipService()"),
                "MembershipService can constructor mac dinh cho ProfileServlet"
        );

        require(
                source.contains("this(new InvoiceDAO())"),
                "Constructor mac dinh phai su dung InvoiceDAO that"
        );
    }

    private static void shouldBindMembershipToProfile(
            String source) {

        require(
                source.contains("MembershipService"),
                "ProfileServlet phai su dung MembershipService"
        );

        require(
                source.contains("membershipService.getSummary("),
                "ProfileServlet phai lay membership summary"
        );

        require(
                source.contains("\"membershipSummary\""),
                "ProfileServlet phai bind attribute membershipSummary"
        );

        require(
                source.contains("LocalDate.now()"),
                "ProfileServlet phai xet membership theo thang hien tai"
        );

        require(
                countOccurrences(source, "bindMembership(") >= 3,
                "Phai bind membership khi GET, khi render loi va co method helper"
        );
    }

    private static void shouldDisplayMembershipOnProfile(
            String source) {

        require(
                source.contains("membershipSummary.tier"),
                "Profile JSP phai hien thi membership tier"
        );

        require(
                source.contains("membershipSummary.currentMonthSpent"),
                "Profile JSP phai hien thi chi tieu thang hien tai"
        );

        require(
                source.contains("fmt:formatNumber"),
                "So tien tich luy phai duoc format de de doc"
        );

        String futurePromotionMessage
                = "\u01AFu \u0111\u00E3i theo h\u1EA1ng th\u00E0nh vi\u00EAn "
                + "s\u1EBD \u0111\u01B0\u1EE3c ph\u00E1t tri\u1EC3n "
                + "trong t\u01B0\u01A1ng lai.";

        require(
                source.contains(futurePromotionMessage),
                "Profile phai thong bao uu dai se phat trien trong tuong lai"
        );
    }

    private static void shouldPreserveExistingProfileContracts(
            String source) {

        require(
                source.contains("method=\"post\"")
                && source.contains("action=\"${ctx}/profile\""),
                "Khong duoc thay doi profile form action/method"
        );

        require(
                source.contains("name=\"csrfToken\""),
                "Khong duoc lam mat CSRF token"
        );

        require(
                source.contains("name=\"fullName\""),
                "Khong duoc lam mat field fullName"
        );

        require(
                source.contains("name=\"email\""),
                "Khong duoc lam mat field email"
        );

        require(
                source.contains("name=\"phone\""),
                "Khong duoc lam mat field phone"
        );

        require(
                source.contains("fieldErrors"),
                "Khong duoc lam mat validation errors"
        );

        require(
                source.contains("memberSince"),
                "Khong duoc lam mat ngay tham gia"
        );
    }

    private static String readProjectFile(String relativePath)
            throws IOException {

        Path current = Paths.get("").toAbsolutePath();

        while (current != null) {
            Path direct = current.resolve(relativePath);

            if (Files.exists(direct)) {
                return new String(
                        Files.readAllBytes(direct),
                        StandardCharsets.UTF_8
                );
            }

            Path nested = current
                    .resolve("SportZoneManagement")
                    .resolve(relativePath);

            if (Files.exists(nested)) {
                return new String(
                        Files.readAllBytes(nested),
                        StandardCharsets.UTF_8
                );
            }

            current = current.getParent();
        }

        throw new IOException(
                "Khong tim thay project file: " + relativePath
        );
    }

    private static String normalize(String source) {
        return source
                .replaceAll("\\s+", " ")
                .toUpperCase(Locale.ROOT);
    }

    private static int countOccurrences(String source, String keyword) {
        int count = 0;
        int index = 0;

        while ((index = source.indexOf(keyword, index)) >= 0) {
            count++;
            index += keyword.length();
        }

        return count;
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}