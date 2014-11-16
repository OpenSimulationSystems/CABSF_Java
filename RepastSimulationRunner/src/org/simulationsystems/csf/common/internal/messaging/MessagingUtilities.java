package org.simulationsystems.csf.common.internal.messaging;

import java.io.IOException;

import org.jdom2.JDOMException;
import org.simulationsystems.csf.common.internal.messaging.xml.XMLUtilities;

public class MessagingUtilities {
	static public org.jdom2.Document createCachedMessageExchangeTemplate() throws JDOMException, IOException {
		return XMLUtilities.filenameStrTojdom2Document("resources/org/simulationsystems/csf/common/messageexchange/CsfMessageExchangeTemplate.xml");
	}
}
