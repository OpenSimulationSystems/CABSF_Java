package org.simulationsystems.csf.common.internal.messaging.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.Filter;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.xml.sax.InputSource;

public class XMLUtilities {
	/*
	 * @throws IOException
	 * 
	 * @throws JDOMException
	 */
	static public org.jdom2.Document filenameStrTojdom2Document(String fileName)
			throws JDOMException, IOException {
		File file = null;
		InputStream inputStream = null;
		Reader reader = null;
		Document document = null;

		try {
			file = new File(fileName);
			inputStream = new FileInputStream(file);

			// LOW: is there a way of not having to specify the encoding?
			reader = new InputStreamReader(inputStream, "UTF-8");

			SAXBuilder saxBuilder = new SAXBuilder();
			// begin of try - catch block
			document = saxBuilder.build(inputStream);

			Element root = document.getRootElement();
			System.out.println("Succesfully loaded template file: " + root.getName());
		} finally {
			if (inputStream != null)
				inputStream.close();
			if (reader != null)
				reader.close();
		}

		return document;

	}

	static public List<Element> executeXPath(Document document, String xpathStr, String namespace, Filter<Element> filter) {
		XPathFactory xpathFactory = XPathFactory.instance();
		// XPathExpression<Object> expr = xpathFactory.compile(xpathStr);

		XPathExpression<Element> expr = xpathFactory.compile(xpathStr, filter, null,
				Namespace.getNamespace("x", namespace));

		List<Element> xPathSearchedNodes = expr.evaluate(document);
		
		return xPathSearchedNodes;
/*		for (int i = 0; i < xPathSearchedNodes.size(); i++) {
			Content content = xPathSearchedNodes.get(i);
			System.out.println("content: " + i + ": " + content.getValue());
		}*/
	}
}
