package org.opensimulationsystems.cabsf.common.internal.messaging.xml;

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
import org.opensimulationsystems.cabsf.common.model.cabsfexceptions.CabsfRuntimeException;
import org.xml.sax.InputSource;

/**
 * The Class XMLUtilities.
 *
 * @author Jorge Calderon
 * @version 0.2
 * @since 0.1
 */
public class XMLUtilities {

	/**
	 * Convert Document to XML string.
	 *
	 * @param document
	 *            the document
	 * @param prettyPrint
	 *            pretty-print the XML
	 * @return the string
	 */
	static public String convertDocumentToXMLString(final Document document,
			final boolean prettyPrint) {
		if (document == null) {
			throw new CabsfRuntimeException("Unable to convert document to string.");
		}
		return convertDocumentToXMLString(document.getRootElement(), prettyPrint);
	}

	/**
	 * Convert Document to XML string.
	 *
	 * @param document
	 *            the document
	 * @param prettyPrint
	 *            pretty-print the XML
	 * @return the string
	 */
	static public String convertDocumentToXMLString(final Element document,
			final boolean prettyPrint) {
		XMLOutputter outputter = null;
		if (prettyPrint) {
			outputter = new XMLOutputter(Format.getPrettyFormat());
		} else {
			outputter = new XMLOutputter();
		}
		final String xmlString = outputter.outputString(document);
		return xmlString;
	}

	/**
	 * Convert an JDOM2 Element to an XML string.
	 *
	 * @param document
	 *            the document
	 * @param prettyPrint
	 *            the pretty print
	 * @return the string
	 */
	static public String convertElementToXMLString(final Element document,
			final boolean prettyPrint) {
		XMLOutputter outputter = null;
		if (prettyPrint) {
			outputter = new XMLOutputter(Format.getPrettyFormat());
		} else {
			outputter = new XMLOutputter();
		}
		final String xmlString = outputter.outputString(document);
		return xmlString;
	}

	/**
	 * Execute an XPath.
	 *
	 * @param document
	 *            the Document
	 * @param xpathStr
	 *            the Xpath String
	 * @param namespaceStr
	 *            the namespace str
	 * @param filter
	 *            the filter
	 * @return the list<? extends content>
	 */
	static public List<? extends Content> executeXPath(final Object document,
			final String xpathStr, final String namespaceStr,
			final Filter<? extends Content> filter) {
		final XPathFactory xpathFactory = XPathFactory.instance();
		// XPathExpression<Object> expr = xpathFactory.compile(xpathStr);

		XPathExpression<? extends Content> expr = null;
		if (namespaceStr != null) {
			expr = xpathFactory.compile(xpathStr, filter, null,
					Namespace.getNamespace("x", namespaceStr));
		} else {
			expr = xpathFactory.compile(xpathStr, filter);
		}

		List<? extends Content> xPathSearchedNodes = null;
		try {
			xPathSearchedNodes = expr.evaluate(document);
		}
		// TODO: Add better handling for these kinds of exceptions
		catch (final Exception e) {
			throw new CabsfRuntimeException("Error in querying the message", e);
		}
		return xPathSearchedNodes;
	}

	/**
	 * Take a filename from the classpath, read the file to an XML string, and convert the
	 * string to a JDOM2 Document.
	 *
	 * @param fileName
	 *            the filename on the classpath
	 * @param isOnClassPath
	 *            true if the supplied path is on the classpath. false if path is on file
	 *            system.
	 * @return the org.jdom2. document
	 * @throws JDOMException
	 *             the JDOM exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	static public org.jdom2.Document filenameStrTojdom2Document(final String fileName,
			final boolean isOnClassPath) throws JDOMException, IOException {
		File file = null;
		InputStream inputStream = null;
		Reader reader = null;
		Document document = null;

		try {

			if (isOnClassPath) {
				inputStream = Thread.currentThread().getContextClassLoader()
						.getResourceAsStream(fileName);
			} else {
				file = new File(fileName);
				inputStream = new FileInputStream(file);
			}

			// LOW: is there a way of not having to specify the encoding?
			reader = new InputStreamReader(inputStream, "UTF-8");

			final SAXBuilder saxBuilder = new SAXBuilder();
			// begin of try - catch block
			document = saxBuilder.build(inputStream);

			final Element root = document.getRootElement();
			System.out.println("[CABSF - Common API] Successfully loaded template file: "
					+ root.getName());
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
			if (reader != null) {
				reader.close();
			}
		}

		return document;

	}

	static public org.jdom2.Document fileTojdom2Document(final File file)
			throws JDOMException, IOException {
		InputStream inputStream = null;
		Reader reader = null;
		Document document = null;

		try {
			inputStream = new FileInputStream(file);

			// LOW: is there a way of not having to specify the encoding?
			reader = new InputStreamReader(inputStream, "UTF-8");

			final SAXBuilder saxBuilder = new SAXBuilder();
			// begin of try - catch block
			document = saxBuilder.build(inputStream);

			final Element root = document.getRootElement();
			System.out.println("[CABSF - Comon API] Successfully loaded template file: "
					+ root.getName());
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
			if (reader != null) {
				reader.close();
			}
		}

		return document;
	}

	/**
	 * Xml string tojdom2 document.
	 *
	 * @param xmlString
	 *            the xml string
	 * @return the org.jdom2. document
	 * @throws JDOMException
	 *             the JDOM exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	static public org.jdom2.Document xmlStringTojdom2Document(final String xmlString)
			throws JDOMException, IOException {
		StringReader sr = null;
		InputSource inputSource = null;
		Document document = null;

		try {
			sr = new StringReader(xmlString);
			inputSource = new InputSource(sr);

			final SAXBuilder saxBuilder = new SAXBuilder();
			// begin of try - catch block
			document = saxBuilder.build(inputSource);

			final Element root = document.getRootElement();
			System.out.println("Successfully loaded template file: " + root.getName());
		} finally {
			if (sr != null) {
				sr.close();
			}
		}

		return document;

	}
}