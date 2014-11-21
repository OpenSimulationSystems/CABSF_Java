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
	private Document document;
	
	public String frameworkMessageToXMLString(FrameworkMessage frameworkMessage,
			Document document, boolean prettyPrint) {
		this.document = document;
			
		return MessagingUtilities.convertDocumentToXMLString(document, prettyPrint); //return new XMLOutputter().outputString(newMessage);
	}
	
	public void setFrameworkCommandToDistSysInDocument(FRAMEWORK_COMMAND command) {
		Filter<Element> filter = new org.jdom2.filter.ElementFilter();
		
		@SuppressWarnings("unchecked")
		//TODO: Get the namespace in the configuration.  Search for all other places using this method
		List<Element> xPathSearchedNodes = (List<Element>) XMLUtilities.executeXPath(document, "/x:CsfMessageExchange/x:ReceivingEntities/x:DistributedSystem/x:DistributedAutonomousAgents/x:AllDistributedAutonomousAgents/x:ControlMessages/x:Command","http://www.simulationsystems.org/csf/schemas/CsfMessageExchange/0.1",filter);
		System.out.println(xPathSearchedNodes.get(0).getValue());
		
		xPathSearchedNodes.get(0).setText("SIMULATION_STARTED");
		System.out.println("New Document: " + new XMLOutputter().outputString(document));
	}
	
	public void setStatusInDocument(STATUS status) {
		Filter<Element> filter = new org.jdom2.filter.ElementFilter();
		
		@SuppressWarnings("unchecked")
		List<Element> xPathSearchedNodes = (List<Element>) XMLUtilities.executeXPath(document, "/x:CsfMessageExchange/x:Status","http://www.simulationsystems.org/csf/schemas/CsfMessageExchange/0.1",filter);
		System.out.println(xPathSearchedNodes.get(0).getValue());
		
		xPathSearchedNodes.get(0).setText("SIMULATION_STARTED");
		System.out.println("New Document: " + new XMLOutputter().outputString(document));
	}
}
