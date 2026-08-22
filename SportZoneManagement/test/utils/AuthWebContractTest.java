package utils;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** Verifies the deployment descriptor and auth JSP contracts without a container. */
public final class AuthWebContractTest {

    private AuthWebContractTest() { }

    public static void main(String[] args) throws Exception {
        String webXml = readUtf8("web/WEB-INF/web.xml");
        String login = readUtf8("web/WEB-INF/views/auth/login.jsp");
        String register = readUtf8("web/WEB-INF/views/auth/register.jsp");
        Document document = parse(webXml);

        require(names(document, "filter", "filter-name").equals(Arrays.asList(
                "EncodingFilter", "AuthFilter", "RoleFilter")),
                "Filters must be declared in EncodingFilter -> AuthFilter -> RoleFilter order");
        require(names(document, "filter-mapping", "filter-name").equals(Arrays.asList(
                "EncodingFilter", "AuthFilter", "RoleFilter")),
                "Filters must be mapped in EncodingFilter -> AuthFilter -> RoleFilter order");

        require(patterns(document, "AuthFilter").equals(Arrays.asList(
                "/profile", "/password", "/booking", "/booking/*", "/bookings",
                "/invoice", "/invoice/*", "/invoices", "/admin/*")),
                "AuthFilter must map exactly every protected URL pattern");
        require(patterns(document, "RoleFilter").equals(Arrays.asList("/admin/*")),
                "RoleFilter must map exactly /admin/*");

        List<String> servletNames = names(document, "servlet", "servlet-name");
        require(servletNames.containsAll(Arrays.asList(
                "LoginServlet", "RegisterServlet", "LogoutServlet")),
                "LoginServlet, RegisterServlet, and LogoutServlet must be declared");
        require(servletPattern(document, "LoginServlet").equals("/login"),
                "LoginServlet must map to /login");
        require(servletPattern(document, "RegisterServlet").equals("/register"),
                "RegisterServlet must map to /register");
        require(servletPattern(document, "LogoutServlet").equals("/logout"),
                "LogoutServlet must map to /logout");

        require(login.contains("${errorMessage}"),
                "Login view must render errorMessage");
        require(login.contains("name=\"csrfToken\""),
                "Login form must submit csrfToken");
        require(register.contains("name=\"csrfToken\""),
                "Register form must submit csrfToken");
        require(!register.contains("value=\"${password}"),
                "Register view must never repopulate a password");

        System.out.println("AuthWebContractTest PASSED");
    }

    private static String readUtf8(String relativePath) throws Exception {
        Path path = Paths.get(relativePath);
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }

    private static Document parse(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        return factory.newDocumentBuilder().parse(new ByteArrayInputStream(
                xml.getBytes(StandardCharsets.UTF_8)));
    }

    private static List<String> names(Document document, String elementName, String childName) {
        List<String> result = new ArrayList<>();
        NodeList elements = document.getElementsByTagNameNS("*", elementName);
        for (int i = 0; i < elements.getLength(); i++) {
            result.add(childText((Element) elements.item(i), childName));
        }
        return result;
    }

    private static List<String> patterns(Document document, String filterName) {
        List<String> result = new ArrayList<>();
        NodeList mappings = document.getElementsByTagNameNS("*", "filter-mapping");
        for (int i = 0; i < mappings.getLength(); i++) {
            Element mapping = (Element) mappings.item(i);
            if (filterName.equals(childText(mapping, "filter-name"))) {
                NodeList children = mapping.getChildNodes();
                for (int j = 0; j < children.getLength(); j++) {
                    Node child = children.item(j);
                    if (child.getNodeType() == Node.ELEMENT_NODE
                            && "url-pattern".equals(child.getLocalName())) {
                        result.add(child.getTextContent().trim());
                    }
                }
            }
        }
        return result;
    }

    private static String servletPattern(Document document, String servletName) {
        NodeList mappings = document.getElementsByTagNameNS("*", "servlet-mapping");
        for (int i = 0; i < mappings.getLength(); i++) {
            Element mapping = (Element) mappings.item(i);
            if (servletName.equals(childText(mapping, "servlet-name"))) {
                return childText(mapping, "url-pattern");
            }
        }
        return "";
    }

    private static String childText(Element parent, String childName) {
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE
                    && childName.equals(child.getLocalName())) {
                return child.getTextContent().trim();
            }
        }
        return "";
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
