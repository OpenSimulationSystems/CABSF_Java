package org.simulationsystems.csf.common.internal.messaging;

import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.simulationsystems.csf.common.internal.messaging.xml.XMLUtilities;

public class MessagingUtilities {
	static public org.jdom2.Document createCachedMessageExchangeTemplate() throws JDOMException,
			IOException {
		return XMLUtilities
				.filenameStrTojdom2Document("../CommonSimulationFramework_CommonLibrary/bin/org/simulationsystems/csf/common/resources/messageexchange/CsfMessageExchangeTemplate.xml");
	}

	static public org.jdom2.Document createDocumentFromString(String xmlString) throws JDOMException, IOException {
		return XMLUtilities.xmlStringTojdom2Document(xmlString);
	}
	
	static public String convertDocumentToXMLString(Document document, boolean prettyPrint) {
		return XMLUtilities.convertDocumentToXMLString(document, prettyPrint);
	}
}
