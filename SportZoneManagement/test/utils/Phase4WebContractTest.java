package utils;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** Static routing and authentication contract for Phase 4. */
public final class Phase4WebContractTest {

    private Phase4WebContractTest() {
    }

    public static void main(String[] args) throws Exception {
        String xml = new String(Files.readAllBytes(
                Paths.get("web/WEB-INF/web.xml")), StandardCharsets.UTF_8);
        Document document = parse(xml);

        require("controller.ProfileServlet".equals(
                servletClass(document, "ProfileServlet")),
                "ProfileServlet must use controller.ProfileServlet");
        require(servletPattern(document, "ProfileServlet").isEmpty(),
                "ProfileServlet must not have a direct public mapping");
        require("controller.PasswordServlet".equals(
                servletClass(document, "PasswordServlet")),
                "PasswordServlet must use controller.PasswordServlet");
        require(servletPattern(document, "PasswordServlet").isEmpty(),
                "PasswordServlet must not have a direct public mapping");

        List<String> authPatterns = filterPatterns(document, "AuthFilter");
        require(authPatterns.contains("/MainController"),
                "MainController must remain authentication-protected by action");

        require(xml.indexOf("<servlet-name>ProfileServlet</servlet-name>")
                < xml.indexOf("<servlet-name>PasswordServlet</servlet-name>"),
                "ProfileServlet declaration must precede PasswordServlet declaration");

        System.out.println("Phase4WebContractTest PASSED");
    }

    private static Document parse(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        return factory.newDocumentBuilder().parse(new ByteArrayInputStream(
                xml.getBytes(StandardCharsets.UTF_8)));
    }

    private static String servletClass(Document document, String name) {
        return servletChild(document, "servlet", name, "servlet-class");
    }

    private static String servletPattern(Document document, String name) {
        return servletChild(document, "servlet-mapping", name, "url-pattern");
    }

    private static String servletChild(Document document, String elementName,
            String servletName, String wantedChild) {
        NodeList elements = document.getElementsByTagNameNS("*", elementName);
        for (int index = 0; index < elements.getLength(); index++) {
            Element element = (Element) elements.item(index);
            if (servletName.equals(childText(element, "servlet-name"))) {
                return childText(element, wantedChild);
            }
        }
        return "";
    }

    private static List<String> filterPatterns(Document document, String name) {
        List<String> result = new ArrayList<>();
        NodeList mappings = document.getElementsByTagNameNS("*", "filter-mapping");
        for (int index = 0; index < mappings.getLength(); index++) {
            Element mapping = (Element) mappings.item(index);
            if (!name.equals(childText(mapping, "filter-name"))) continue;
            NodeList children = mapping.getChildNodes();
            for (int childIndex = 0; childIndex < children.getLength(); childIndex++) {
                Node child = children.item(childIndex);
                if (child.getNodeType() == Node.ELEMENT_NODE
                        && "url-pattern".equals(child.getLocalName())) {
                    result.add(child.getTextContent().trim());
                }
            }
        }
        return result;
    }

    private static String childText(Element parent, String name) {
        NodeList children = parent.getChildNodes();
        for (int index = 0; index < children.getLength(); index++) {
            Node child = children.item(index);
            if (child.getNodeType() == Node.ELEMENT_NODE
                    && name.equals(child.getLocalName())) {
                return child.getTextContent().trim();
            }
        }
        return "";
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
