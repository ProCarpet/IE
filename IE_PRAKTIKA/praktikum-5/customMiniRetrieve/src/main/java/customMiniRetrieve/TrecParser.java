package customMiniRetrieve;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TrecParser {

    public List<TrecItem> parse(String fileName) throws ParserConfigurationException, IOException, SAXException {
        File file = new File(fileName);
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(file);
        NodeList nodeList = document.getElementsByTagName("DOC");
        List<TrecItem> items = new ArrayList<>();
        for (int i = 0; i <= nodeList.getLength() - 1; i++) {
            Node node = nodeList.item(i);
            Element element = (Element) node;
            items.add(new TrecItem(
                    Integer.parseInt(getElementText(element, "recordId")),
                    getElementText(element, "text")));
        }
        return items;
    }



    public static String getElementText(Element element, String tagName) {
        return element.getElementsByTagName(tagName).item(0).getTextContent();
    }
}
