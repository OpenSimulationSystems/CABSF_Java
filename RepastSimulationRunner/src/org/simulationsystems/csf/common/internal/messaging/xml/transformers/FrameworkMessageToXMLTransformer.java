package org.simulationsystems.csf.common.internal.messaging.xml.transformers;

import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.Filter;
import org.jdom2.output.XMLOutputter;
import org.simulationsystems.csf.common.internal.messaging.MessagingUtilities;
import org.simulationsystems.csf.common.internal.messaging.messages.FRAMEWORK_TO_DISTRIBUTEDSYSTEM_COMMAND;
import org.simulationsystems.csf.common.internal.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.internal.messaging.xml.XMLUtilities;

/*
 * Transforms framework messages into XML
 */
public class FrameworkMessageToXMLTransformer {
	private Document newMessage;
	
	public String frameworkMessageToXMLString(FrameworkMessage frameworkMessage,
			Document csfMessageExchangeTemplateDocument) {
		this.newMessage = csfMessageExchangeTemplateDocument.clone();
		
		//TODO: Add constructor argument?
		setCommand(frameworkMessage.getFrameworkToDistributedSystemCommand());
		
		return new XMLOutputter().outputString(newMessage);
	}
	
	private void setCommand(FRAMEWORK_TO_DISTRIBUTEDSYSTEM_COMMAND command) {
		Filter<Element> filter = new org.jdom2.filter.ElementFilter();
		
		@SuppressWarnings("unchecked")
		List<Element> xPathSearchedNodes = (List<Element>) XMLUtilities.executeXPath(newMessage, "/x:CsfMessageExchange/x:ReceivingEntities/x:MessageToAllDistributedSystems/x:ControlMessages/x:ControlMessage","http://www.simulationsystems.org/csf/schemas/CsfMessageExchange/0.1",filter);
		System.out.println(xPathSearchedNodes.get(0).getValue());
		
		xPathSearchedNodes.get(0).setText("SIMULATION_STARTED");
		System.out.println("New Document: " + new XMLOutputter().outputString(newMessage));
		
	}
}
