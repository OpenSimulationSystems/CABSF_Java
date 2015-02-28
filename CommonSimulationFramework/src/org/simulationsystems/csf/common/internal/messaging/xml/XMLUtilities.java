package org.simulationsystems.csf.common.internal.messaging.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.Filter;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.simulationsystems.csf.common.csfmodel.csfexceptions.CsfRuntimeException;
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
			System.out.println("[Common Simulation Framework - internal] Successfully loaded template file: "
					+ root.getName());
		} finally {
			if (inputStream != null)
				inputStream.close();
			if (reader != null)
				reader.close();
		}

		return document;

	}

	/*
	 * @throws IOException
	 * 
	 * @throws JDOMException
	 */
	static public org.jdom2.Document xmlStringTojdom2Document(String xmlString)
			throws JDOMException, IOException {
		StringReader sr = null;
		InputSource inputSource = null;
		Document document = null;

		try {
			sr = new StringReader(xmlString);
			inputSource = new InputSource(sr);

			SAXBuilder saxBuilder = new SAXBuilder();
			// begin of try - catch block
			document = saxBuilder.build(inputSource);

			Element root = document.getRootElement();
			System.out.println("Successfully loaded template file: " + root.getName());
		} finally {
			if (sr != null)
				sr.close();
		}

		return document;

	}

	static public List<? extends Content> executeXPath(Object document, String xpathStr,
			String namespaceStr, Filter<? extends Content> filter) {
		XPathFactory xpathFactory = XPathFactory.instance();
		// XPathExpression<Object> expr = xpathFactory.compile(xpathStr);

		XPathExpression<? extends Content> expr = null;
		if (namespaceStr != null)
			expr = xpathFactory.compile(xpathStr, filter, null,
					Namespace.getNamespace("x", namespaceStr));
		else
			expr = xpathFactory.compile(xpathStr, filter);

		List<? extends Content> xPathSearchedNodes = null;
		try {
			xPathSearchedNodes = expr.evaluate(document);
		}
		// TODO: Add better handling for these kinds of exceptions
		catch (Exception e) {
			throw new CsfRuntimeException("Error in querying the message", e);
		}
		return xPathSearchedNodes;
		/*
		 * for (int i = 0; i < xPathSearchedNodes.size(); i++) { Content content =
		 * xPathSearchedNodes.get(i); System.out.println("content: " + i + ": " +
		 * content.getValue()); }
		 */
	}

	static public String convertDocumentToXMLString(Document document, boolean prettyPrint) {
		return convertDocumentToXMLString(document.getRootElement(), prettyPrint);
	}

	static public String convertDocumentToXMLString(Element document, boolean prettyPrint) {
		XMLOutputter outputter = null;
		if (prettyPrint)
			outputter = new XMLOutputter(Format.getPrettyFormat());
		else
			outputter = new XMLOutputter();
		String xmlString = outputter.outputString(document);
		return xmlString;
	}

	static public String convertElementToXMLString(Element document, boolean prettyPrint) {
		XMLOutputter outputter = null;
		if (prettyPrint)
			outputter = new XMLOutputter(Format.getPrettyFormat());
		else
			outputter = new XMLOutputter();
		String xmlString = outputter.outputString(document);
		return xmlString;
	}
}
