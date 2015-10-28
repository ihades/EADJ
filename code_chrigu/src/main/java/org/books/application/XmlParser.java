package org.books.application;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class XmlParser {

	public static <T> List<T> parse(String path, Class<T> type) {
		try {
			InputStream instream = XmlParser.class.getResourceAsStream(path);
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(instream);
			String name = type.getSimpleName().substring(0, 1).toLowerCase() + type.getSimpleName().substring(1);
			NodeList nodes = document.getElementsByTagName(name);
			Unmarshaller unmarshaller = JAXBContext.newInstance(type).createUnmarshaller();
			List<T> items = new ArrayList<>();
			for (int i = 0; i < nodes.getLength(); i++) {
				items.add(unmarshaller.unmarshal(nodes.item(i), type).getValue());
			}
			return items;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
