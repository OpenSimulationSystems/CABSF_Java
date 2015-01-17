package org.simulationsystems.csf.common.internal.messaging;

import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.simulationsystems.csf.common.internal.messaging.xml.XMLUtilities;

public class MessagingUtilities {
	static public org.jdom2.Document createCachedMessageExchangeTemplateWithPlaceholders
	() throws JDOMException,
			IOException {
		//FIXME: Use the current path of this java file
		Document doc = null;
		try {
		doc = XMLUtilities
				.filenameStrTojdom2Document("../CommonSimulationFramework/bin/org/simulationsystems/csf/common/resources/messageexchange/CsfMessageExchangeTemplate.xml");
		}
		catch (Exception e) {
			doc = XMLUtilities
					.filenameStrTojdom2Document("../../CommonSimulationFramework/bin/org/simulationsystems/csf/common/resources/messageexchange/CsfMessageExchangeTemplate.xml");

		}
		return doc;
	}

	static public org.jdom2.Document createDocumentFromString(String xmlString) throws JDOMException, IOException {
		return XMLUtilities.xmlStringTojdom2Document(xmlString);
	}
	
	static public String convertDocumentToXMLString(Document document, boolean prettyPrint) {
		return XMLUtilities.convertDocumentToXMLString(document.getRootElement(), prettyPrint);
	}
}
