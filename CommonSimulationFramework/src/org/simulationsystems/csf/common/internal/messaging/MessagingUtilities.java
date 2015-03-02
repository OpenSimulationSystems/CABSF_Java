package org.simulationsystems.csf.common.internal.messaging;

import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.simulationsystems.csf.common.internal.messaging.xml.XMLUtilities;

/**
 * XML Utilities class to be used by the agents
 * 
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
// TODO: Replace with XMLUtilities?
public class MessagingUtilities {

	/**
	 * Convert Document to XML string.
	 * 
	 * @param document
	 *            the document
	 * @param prettyPrint
	 *            the pretty print
	 * @return the string
	 */
	static public String convertDocumentToXMLString(final Document document,
			final boolean prettyPrint) {
		return XMLUtilities.convertDocumentToXMLString(document.getRootElement(),
				prettyPrint);
	}

	/**
	 * Creates the cached message exchange template as a Document
	 * 
	 * @return the org.jdom2. document
	 * @throws JDOMException
	 *             the JDOM exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	static public org.jdom2.Document createCachedMessageExchangeTemplateWithPlaceholders()
			throws JDOMException, IOException {
		// FIXME: Use the current path of this java file
		Document doc = null;
		try {
			doc = XMLUtilities
					.filenameStrTojdom2Document("../CommonSimulationFramework/bin/org/simulationsystems/csf/common/resources/messageexchange/CsfMessageExchangeTemplate.xml");
		} catch (final Exception e) {
			doc = XMLUtilities
					.filenameStrTojdom2Document("../../CommonSimulationFramework/bin/org/simulationsystems/csf/common/resources/messageexchange/CsfMessageExchangeTemplate.xml");

		}
		return doc;
	}

	/**
	 * Creates the Document from string.
	 * 
	 * @param xmlString
	 *            the xml string
	 * @return the org.jdom2. document
	 * @throws JDOMException
	 *             the JDOM exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	static public org.jdom2.Document createDocumentFromString(final String xmlString)
			throws JDOMException, IOException {
		return XMLUtilities.xmlStringTojdom2Document(xmlString);
	}
}
