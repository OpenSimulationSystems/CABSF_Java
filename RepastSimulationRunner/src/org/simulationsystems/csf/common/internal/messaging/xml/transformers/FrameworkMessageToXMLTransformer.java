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
	
	final private String frameworkToDistributedSystemCommand_XPath = "/x:CsfMessageExchange/x:ReceivingEntities/x:DistributedSystem/x:DistributedAutonomousAgents/x:AllDistributedAutonomousAgents/x:ControlMessages/x:Command";
	final private String frameworkToSimulationEngineCommnad_XPath = "/x:CsfMessageExchange/x:ReceivingEntities/x:SimulationSystem/x:ControlMessages/x:Command";
	final private String frameworkStatus_XPath = "/x:CsfMessageExchange/x:Status";
	
	public FrameworkMessageToXMLTransformer(FrameworkMessage frameworkMessage) {
		this.frameworkMessage = frameworkMessage;
	}

	public String frameworkMessageToXMLString(Document document,
			boolean prettyPrint) {

		return MessagingUtilities.convertDocumentToXMLString(document,
				prettyPrint); // return new
								// XMLOutputter().outputString(newMessage);
	}

	public void setFrameworkToDistributedSystemCommand(FRAMEWORK_COMMAND command) {
		Filter<Element> filter = new org.jdom2.filter.ElementFilter();

		@SuppressWarnings("unchecked")
		// TODO: Get the namespace in the configuration. Search for all other
		// places using this method
		//TODO: Make the namespace configurable
		List<Element> xPathSearchedNodes = (List<Element>) XMLUtilities
				.executeXPath(
						frameworkMessage.getDocument(),
						frameworkToDistributedSystemCommand_XPath,
						"http://www.simulationsystems.org/csf/schemas/CsfMessageExchange/0.1.0",
						filter);
		System.out.println(xPathSearchedNodes.get(0).getValue());

		xPathSearchedNodes.get(0).setText(command.toString());
		System.out.println("Set framework to distributed system command: " + command.toString()
				+ MessagingUtilities.convertDocumentToXMLString(
						frameworkMessage.getDocument(), true));
	}

	public FRAMEWORK_COMMAND getFrameworkToDistributedSystemCommand() {
		Filter<Element> filter = new org.jdom2.filter.ElementFilter();

		@SuppressWarnings("unchecked")
		// TODO: Get the namespace in the configuration. Search for all other
		// places using this method
		//TODO: Make the namespace configurable
		List<Element> xPathSearchedNodes = (List<Element>) XMLUtilities
				.executeXPath(
						frameworkMessage.getDocument(),
						frameworkToDistributedSystemCommand_XPath,
						"http://www.simulationsystems.org/csf/schemas/CsfMessageExchange/0.1.0",
						filter);
		System.out.println(xPathSearchedNodes.get(0).getValue());

		String commandStr = xPathSearchedNodes.get(0).getValue();
		return convertStringToFrameworkCommand(commandStr);
		
	}
	
	public FRAMEWORK_COMMAND getFrameworkToSimulationEngineCommand() {
		Filter<Element> filter = new org.jdom2.filter.ElementFilter();

		@SuppressWarnings("unchecked")
		// TODO: Get the namespace in the configuration. Search for all other
		// places using this method
		//TODO: Make the namespace configurable
		List<Element> xPathSearchedNodes = (List<Element>) XMLUtilities
				.executeXPath(
						frameworkMessage.getDocument(),
						frameworkToSimulationEngineCommnad_XPath,
						"http://www.simulationsystems.org/csf/schemas/CsfMessageExchange/0.1.0",
						filter);
		System.out.println(xPathSearchedNodes.get(0).getValue());

		String commandStr = xPathSearchedNodes.get(0).getValue();
		return convertStringToFrameworkCommand(commandStr);
		
	}
	
	private FRAMEWORK_COMMAND convertStringToFrameworkCommand(String str) {
		for (FRAMEWORK_COMMAND cmd : FRAMEWORK_COMMAND.values()) {
			if (cmd.toString().equalsIgnoreCase(str))
				return cmd;
		}
		return null;
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
		System.out.println("Set Status: "+ status.toString()
				+ new XMLOutputter().outputString(frameworkMessage
						.getDocument()));
	}

	public void setFrameworkToSimulationEngineCommnad(FRAMEWORK_COMMAND command) {
		Filter<Element> filter = new org.jdom2.filter.ElementFilter();

		@SuppressWarnings("unchecked")
		// TODO: Get the namespace in the configuration. Search for all other
		// places using this method
		//TODO: Make the namespace configurable
		List<Element> xPathSearchedNodes = (List<Element>) XMLUtilities
				.executeXPath(
						frameworkMessage.getDocument(),
						frameworkToSimulationEngineCommnad_XPath,
						"http://www.simulationsystems.org/csf/schemas/CsfMessageExchange/0.1.0",
						filter);
		System.out.println(xPathSearchedNodes.get(0).getValue());

		xPathSearchedNodes.get(0).setText(command.toString());
		System.out.println("Set framework to simulation engine command: "+command.toString()
				+ MessagingUtilities.convertDocumentToXMLString(
						frameworkMessage.getDocument(), true));
	}

	public STATUS getStatus() {
		Filter<Element> filter = new org.jdom2.filter.ElementFilter();

		@SuppressWarnings("unchecked")
		// TODO: Get the namespace in the configuration. Search for all other
		// places using this method
		//TODO: Make the namespace configurable
		List<Element> xPathSearchedNodes = (List<Element>) XMLUtilities
				.executeXPath(
						frameworkMessage.getDocument(),
						frameworkStatus_XPath,
						"http://www.simulationsystems.org/csf/schemas/CsfMessageExchange/0.1.0",
						filter);
		System.out.println(xPathSearchedNodes.get(0).getValue());

		String status = xPathSearchedNodes.get(0).getValue();
		return convertStringToStatus(status);
		
	}
	
	
	private STATUS convertStringToStatus(String str) {
		for (STATUS st : STATUS.values()) {
			if (st.toString().equals(str))
				return st;
		}
		return null;
	}

	public void setStatus(STATUS status) {
		Filter<Element> filter = new org.jdom2.filter.ElementFilter();

		@SuppressWarnings("unchecked")
		// TODO: Get the namespace in the configuration. Search for all other
		// places using this method
		//TODO: Make the namespace configurable
		List<Element> xPathSearchedNodes = (List<Element>) XMLUtilities
				.executeXPath(
						frameworkMessage.getDocument(),
						frameworkStatus_XPath,
						"http://www.simulationsystems.org/csf/schemas/CsfMessageExchange/0.1.0",
						filter);
		System.out.println(xPathSearchedNodes.get(0).getValue());

		xPathSearchedNodes.get(0).setText(status.toString());
		System.out.println("Set Status: "+status.toString()
				+ MessagingUtilities.convertDocumentToXMLString(
						frameworkMessage.getDocument(), true));
	}
}
