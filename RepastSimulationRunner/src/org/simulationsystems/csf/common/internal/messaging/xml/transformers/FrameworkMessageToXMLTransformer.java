package org.simulationsystems.csf.common.internal.messaging.xml.transformers;

import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.Filter;
import org.jdom2.output.XMLOutputter;
import org.simulationsystems.csf.common.csfmodel.FRAMEWORK_COMMAND;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.STATUS;
import org.simulationsystems.csf.common.internal.messaging.MessagingUtilities;
import org.simulationsystems.csf.common.internal.messaging.xml.XMLUtilities;

/*
 * Transforms framework messages into XML
 */
public class FrameworkMessageToXMLTransformer {
	FrameworkMessage frameworkMessage;

	public FrameworkMessageToXMLTransformer(FrameworkMessage frameworkMessage) {
		this.frameworkMessage = frameworkMessage;
	}

	public String frameworkMessageToXMLString(Document document,
			boolean prettyPrint) {

		return MessagingUtilities.convertDocumentToXMLString(document,
				prettyPrint); // return new
								// XMLOutputter().outputString(newMessage);
	}

	public void setFrameworkCommandToDistSysInDocument(FRAMEWORK_COMMAND command) {
		Filter<Element> filter = new org.jdom2.filter.ElementFilter();

		@SuppressWarnings("unchecked")
		// TODO: Get the namespace in the configuration. Search for all other
		// places using this method
		//TODO: Make the namespace configurable
		List<Element> xPathSearchedNodes = (List<Element>) XMLUtilities
				.executeXPath(
						frameworkMessage.getDocument(),
						"/x:CsfMessageExchange/x:ReceivingEntities/x:DistributedSystem/x:DistributedAutonomousAgents/x:AllDistributedAutonomousAgents/x:ControlMessages/x:Command",
						"http://www.simulationsystems.org/csf/schemas/CsfMessageExchange/0.1.0",
						filter);
		System.out.println(xPathSearchedNodes.get(0).getValue());

		xPathSearchedNodes.get(0).setText(command.toString());
		System.out.println("New Document: "
				+ MessagingUtilities.convertDocumentToXMLString(
						frameworkMessage.getDocument(), true));
	}
	
	//TODO: extract method from these classes
	//TODO: Make the namespace configurable
	public void setStatusInDocument(STATUS status) {
		Filter<Element> filter = new org.jdom2.filter.ElementFilter();

		@SuppressWarnings("unchecked")
		List<Element> xPathSearchedNodes = (List<Element>) XMLUtilities
				.executeXPath(
						frameworkMessage.getDocument(),
						"/x:CsfMessageExchange/x:Status",
						"http://www.simulationsystems.org/csf/schemas/CsfMessageExchange/0.1.0",
						filter);
		System.out.println(xPathSearchedNodes.get(0).getValue());

		xPathSearchedNodes.get(0).setText(status.toString());
		System.out.println("New Document: "
				+ new XMLOutputter().outputString(frameworkMessage
						.getDocument()));
	}
}
