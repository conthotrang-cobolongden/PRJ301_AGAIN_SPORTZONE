package utils;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Kiem tra web.xml wiring cho Phase 3.
 *
 * Contract:
 * - CourtsServlet map GET /courts.
 * - CourtDetailServlet map GET /court.
 * - Hai route deu public.
 * - EncodingFilter van ap dung qua pattern /*.
 *
 * Tag: U7, U8, U9
 */
public final class Phase3WebContractTest {

    private Phase3WebContractTest() {
    }

    public static void main(String[] args) throws Exception {
        String webXml = readUtf8(
                "web/WEB-INF/web.xml"
        );

        Document document = parse(webXml);

        require(
                servletExists(
                        document,
                        "CourtsServlet"
                ),
                "CourtsServlet must be declared"
        );

        require(
                "controller.CourtsServlet".equals(
                        servletClass(
                                document,
                                "CourtsServlet"
                        )
                ),
                "CourtsServlet must use controller.CourtsServlet"
        );

        require(
                "/courts".equals(
                        servletPattern(
                                document,
                                "CourtsServlet"
                        )
                ),
                "CourtsServlet must map to /courts"
        );

        require(
                servletExists(
                        document,
                        "CourtDetailServlet"
                ),
                "CourtDetailServlet must be declared"
        );

        require(
                "controller.CourtDetailServlet".equals(
                        servletClass(
                                document,
                                "CourtDetailServlet"
                        )
                ),
                "CourtDetailServlet must use "
                + "controller.CourtDetailServlet"
        );

        require(
                "/court".equals(
                        servletPattern(
                                document,
                                "CourtDetailServlet"
                        )
                ),
                "CourtDetailServlet must map to /court"
        );

        /*
         * U7-U9 la public browsing.
         * Khong duoc bat user login chi de xem san/lich.
         */
        List<String> authPatterns = filterPatterns(
                document,
                "AuthFilter"
        );

        require(
                !authPatterns.contains("/courts"),
                "/courts must remain public"
        );

        require(
                !authPatterns.contains("/court"),
                "/court must remain public"
        );

        /*
         * EncodingFilter map /* nen van xu ly hai route public.
         */
        List<String> encodingPatterns = filterPatterns(
                document,
                "EncodingFilter"
        );

        require(
                encodingPatterns.size() == 1
                && "/*".equals(encodingPatterns.get(0)),
                "EncodingFilter must continue to map /*"
        );

        System.out.println(
                "Phase3WebContractTest PASSED"
        );
    }

    private static String readUtf8(String relativePath)
            throws Exception {
        Path path = Paths.get(relativePath);

        return new String(
                Files.readAllBytes(path),
                StandardCharsets.UTF_8
        );
    }

    private static Document parse(String xml)
            throws Exception {
        DocumentBuilderFactory factory
                = DocumentBuilderFactory.newInstance();

        factory.setNamespaceAware(true);

        /*
         * web.xml la file local cua project.
         * Khong can resolve external resource.
         */
        return factory.newDocumentBuilder().parse(
                new ByteArrayInputStream(
                        xml.getBytes(StandardCharsets.UTF_8)
                )
        );
    }

    private static boolean servletExists(
            Document document,
            String servletName
    ) {
        NodeList servlets = document.getElementsByTagNameNS(
                "*",
                "servlet"
        );

        for (int index = 0;
                index < servlets.getLength();
                index++) {
            Element servlet
                    = (Element) servlets.item(index);

            if (servletName.equals(
                    childText(servlet, "servlet-name")
            )) {
                return true;
            }
        }

        return false;
    }

    private static String servletClass(
            Document document,
            String servletName
    ) {
        NodeList servlets = document.getElementsByTagNameNS(
                "*",
                "servlet"
        );

        for (int index = 0;
                index < servlets.getLength();
                index++) {
            Element servlet
                    = (Element) servlets.item(index);

            if (servletName.equals(
                    childText(servlet, "servlet-name")
            )) {
                return childText(
                        servlet,
                        "servlet-class"
                );
            }
        }

        return "";
    }

    private static String servletPattern(
            Document document,
            String servletName
    ) {
        NodeList mappings
                = document.getElementsByTagNameNS(
                        "*",
                        "servlet-mapping"
                );

        for (int index = 0;
                index < mappings.getLength();
                index++) {
            Element mapping
                    = (Element) mappings.item(index);

            if (servletName.equals(
                    childText(mapping, "servlet-name")
            )) {
                return childText(
                        mapping,
                        "url-pattern"
                );
            }
        }

        return "";
    }

    private static List<String> filterPatterns(
            Document document,
            String filterName
    ) {
        List<String> result = new ArrayList<>();

        NodeList mappings
                = document.getElementsByTagNameNS(
                        "*",
                        "filter-mapping"
                );

        for (int index = 0;
                index < mappings.getLength();
                index++) {
            Element mapping
                    = (Element) mappings.item(index);

            if (!filterName.equals(
                    childText(mapping, "filter-name")
            )) {
                continue;
            }

            NodeList children = mapping.getChildNodes();

            for (int childIndex = 0;
                    childIndex < children.getLength();
                    childIndex++) {
                Node child = children.item(childIndex);

                if (child.getNodeType()
                        == Node.ELEMENT_NODE
                        && "url-pattern".equals(
                                child.getLocalName()
                        )) {
                    result.add(
                            child.getTextContent().trim()
                    );
                }
            }
        }

        return result;
    }

    private static String childText(
            Element parent,
            String childName
    ) {
        NodeList children = parent.getChildNodes();

        for (int index = 0;
                index < children.getLength();
                index++) {
            Node child = children.item(index);

            if (child.getNodeType()
                    == Node.ELEMENT_NODE
                    && childName.equals(
                            child.getLocalName()
                    )) {
                return child.getTextContent().trim();
            }
        }

        return "";
    }

    private static void require(
            boolean condition,
            String message
    ) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}