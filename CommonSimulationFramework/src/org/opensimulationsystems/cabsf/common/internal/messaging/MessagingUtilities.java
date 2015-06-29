package org.opensimulationsystems.cabsf.common.internal.messaging;

import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.opensimulationsystems.cabsf.common.internal.messaging.xml.XMLUtilities;

// TODO: Auto-generated Javadoc
/**
 * XML Utilities class to be used by the agents.
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
	 * Creates the cached message exchange template JDOM2 Document. Any placeholder values
	 * in the template file are kept in the returned Document.
	 *
	 * @return the org.jdom2. document
	 * @throws JDOMException
	 *             the JDOM exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	static public org.jdom2.Document createCachedMessageExchangeTemplateWithPlaceholders()
			throws JDOMException, IOException {
		Document doc = null;
		try {
			// doc = XMLUtilities
			// .filenameStrTojdom2Document("../../../CommonSimulationFramework/bin/org/opensimulationsystems/cabsf/common/resources/messageexchange/CabsfMessageExchangeTemplate.xml");
			doc = XMLUtilities
					.filenameStrTojdom2Document(
							"org/opensimulationsystems/cabsf/common/resources/messageexchange/CabsfMessageExchangeTemplate.xml",
							true);

		} catch (final Exception e) {
			// doc = XMLUtilities
			// .filenameStrTojdom2Document("../CommonSimulationFramework/bin/org/opensimulationsystems/cabsf/common/resources/messageexchange/CsfMessageExchangeTemplate.xml");
		}
		assert (doc != null);
		return doc;
	}

	/*
	 * static public org.jdom2.Document createDocumentFromFile(final File file) throws
	 * JDOMException, IOException { // FIXME: Use the current path of this java file
	 * Document doc = null; try { doc = XMLUtilities.fileTojdom2Document(file);
	 * 
	 * } catch (final Exception e) { } assert (doc != null); return doc; }
	 */

	/**
	 * Creates the cached CABSF configuration file template as a Document.
	 *
	 * @param filePath
	 *            the file path
	 * @return the org.jdom2. document
	 * @throws JDOMException
	 *             the JDOM exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	static public org.jdom2.Document createDocumentFromFileSystemPath(
			final String filePath) throws JDOMException, IOException {
		// FIXME: Use the current path of this java file
		Document doc = null;
		try {
			doc = XMLUtilities.filenameStrTojdom2Document(filePath, false);

		} catch (final Exception e) {
		}
		assert (doc != null);
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
