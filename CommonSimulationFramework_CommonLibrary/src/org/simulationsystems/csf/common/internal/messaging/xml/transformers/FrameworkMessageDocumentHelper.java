package org.simulationsystems.csf.common.internal.messaging.xml.transformers;

import java.util.List;

import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filter;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FRAMEWORK_COMMAND;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.STATUS;
import org.simulationsystems.csf.common.internal.messaging.MessagingUtilities;
import org.simulationsystems.csf.common.internal.messaging.xml.XMLUtilities;

/*
 * Transforms framework messages into XML
 */
public class FrameworkMessageDocumentHelper {
	FrameworkMessage frameworkMessage;

	final private String frameworkToDistributedSystemCommand_XPath = "/x:CsfMessageExchange/x:ReceivingEntities/x:DistributedSystem/x:DistributedAutonomousAgents/x:AllDistributedAutonomousAgents/x:ControlMessages/x:Command";
	final private String frameworkToSimulationEngineCommnad_XPath = "/x:CsfMessageExchange/x:ReceivingEntities/x:SimulationSystem/x:ControlMessages/x:Command";
	final private String frameworkStatus_XPath = "/x:CsfMessageExchange/x:Status";
	final private String distributedAutonomousAgentsXpath = "/x:CsfMessageExchange/x:ReceivingEntities/x:DistributedSystem/x:DistributedAutonomousAgents";
	// TODO: Get this from the configuration
	private String namespaceStr = "http://www.simulationsystems.org/csf/schemas/CsfMessageExchange/0.1.0";
	private Namespace namespace = Namespace.getNamespace("x", namespaceStr);
	private Filter<Element> elementFilter = new org.jdom2.filter.ElementFilter();

	// Used to determine
	// FIXME: Need a better way to determine whether the actor has already been used
	private boolean firstAgentModelActorPopulated;

	private boolean firstDistributedAutonomousAgentPopulated;

	public FrameworkMessageDocumentHelper(FrameworkMessage frameworkMessage) {
		this.frameworkMessage = frameworkMessage;
	}

	public String frameworkMessageToXMLString(Document document, boolean prettyPrint) {

		return MessagingUtilities.convertDocumentToXMLString(document, prettyPrint); // return
																						// new
																						// XMLOutputter().outputString(newMessage);
	}

	public void setFrameworkToDistributedSystemCommand(FRAMEWORK_COMMAND command) {
		@SuppressWarnings("unchecked")
		// TODO: Get the namespace in the configuration. Search for all other
		// places using this method
		// TODO: Make the namespace configurable
		List<Element> xPathSearchedNodes = (List<Element>) XMLUtilities.executeXPath(
				frameworkMessage.getDocument(),
				frameworkToDistributedSystemCommand_XPath, namespaceStr, elementFilter);
		System.out.println(xPathSearchedNodes.get(0).getValue());

		xPathSearchedNodes.get(0).setText(command.toString());
		System.out.println("Set framework to distributed system command: "
				+ command.toString()
				+ MessagingUtilities.convertDocumentToXMLString(
						frameworkMessage.getDocument(), true));
	}

	public FRAMEWORK_COMMAND getFrameworkToDistributedSystemCommand() {
		@SuppressWarnings("unchecked")
		// TODO: Get the namespace in the configuration. Search for all other
		// places using this method
		// TODO: Make the namespace configurable
		List<Element> xPathSearchedNodes = (List<Element>) XMLUtilities.executeXPath(
				frameworkMessage.getDocument(),
				frameworkToDistributedSystemCommand_XPath, namespaceStr, elementFilter);
		System.out.println(xPathSearchedNodes.get(0).getValue());

		String commandStr = xPathSearchedNodes.get(0).getValue();
		return convertStringToFrameworkCommand(commandStr);

	}

	public FRAMEWORK_COMMAND getFrameworkToSimulationEngineCommand() {
		@SuppressWarnings("unchecked")
		// TODO: Get the namespace in the configuration. Search for all other
		// places using this method
		// TODO: Make the namespace configurable
		List<Element> xPathSearchedNodes = (List<Element>) XMLUtilities.executeXPath(
				frameworkMessage.getDocument(), frameworkToSimulationEngineCommnad_XPath,
				namespaceStr, elementFilter);
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

	public void setFrameworkToSimulationEngineCommnad(FRAMEWORK_COMMAND command) {
		@SuppressWarnings("unchecked")
		// TODO: Get the namespace in the configuration. Search for all other
		// places using this method
		// TODO: Make the namespace configurable
		List<Element> xPathSearchedNodes = (List<Element>) XMLUtilities.executeXPath(
				frameworkMessage.getDocument(), frameworkToSimulationEngineCommnad_XPath,
				"http://www.simulationsystems.org/csf/schemas/CsfMessageExchange/0.1.0",
				elementFilter);
		System.out.println(xPathSearchedNodes.get(0).getValue());

		xPathSearchedNodes.get(0).setText(command.toString());
		System.out.println("Set framework to simulation engine command: "
				+ command.toString()
				+ MessagingUtilities.convertDocumentToXMLString(
						frameworkMessage.getDocument(), true));
	}

	public STATUS getStatus() {
		@SuppressWarnings("unchecked")
		// TODO: Get the namespace in the configuration. Search for all other
		// places using this method
		// TODO: Make the namespace configurable
		List<Element> xPathSearchedNodes = (List<Element>) XMLUtilities.executeXPath(
				frameworkMessage.getDocument(), frameworkStatus_XPath,
				"http://www.simulationsystems.org/csf/schemas/CsfMessageExchange/0.1.0",
				elementFilter);
		System.out.println(xPathSearchedNodes.get(0).getValue());

		String status = xPathSearchedNodes.get(0).getValue();
		return convertStringToStatus(status);

	}

	public STATUS convertStringToStatus(String str) {
		for (STATUS st : STATUS.values()) {
			if (st.toString().equals(str))
				return st;
		}
		return null;
	}

	public void setStatus(STATUS status) {
		@SuppressWarnings("unchecked")
		// TODO: Get the namespace in the configuration. Search for all other
		// places using this method
		// TODO: Make the namespace configurable
		List<Element> xPathSearchedNodes = (List<Element>) XMLUtilities.executeXPath(
				frameworkMessage.getDocument(), frameworkStatus_XPath, namespaceStr,
				elementFilter);
		System.out.println(xPathSearchedNodes.get(0).getValue());

		xPathSearchedNodes.get(0).setText(status.toString());
		System.out.println("Set Status: "
				+ status.toString()
				+ MessagingUtilities.convertDocumentToXMLString(
						frameworkMessage.getDocument(), true));
	}

	public Document getDocument() {
		return frameworkMessage.getDocument();
	}

	// TODO: Extents/multiple dimensions
	private Element populateThisActorLocationInAgentModel(Element actor,
			String gridPointX, String gridPointY) {
		@SuppressWarnings("unchecked")
		// TODO: Support multiple actors
		List<Element> thisAgentLocation = (List<Element>) XMLUtilities
				.executeXPath(
						actor,
						"./x:EnvironmentChanges/x:CommonEnvironmentChanges/x:EnvironmentChange/x:Location[@id='self']",
						namespaceStr, elementFilter);

		thisAgentLocation.get(0).getChild("GridPointX", namespace).setText(gridPointX);
		thisAgentLocation.get(0).getChild("GridPointY", namespace).setText(gridPointY);
		XMLUtilities.convertElementToXMLString(thisAgentLocation.get(0), true);
		return actor;
	}

	public void setIDForActorInAgentModel(Element actor, String ID) {
		// TODO: Support multiple actors
		List<Element> agentModelID = (List<Element>) XMLUtilities.executeXPath(actor,
				"./x:ID", namespaceStr, elementFilter);
		if (agentModelID.size() > 0) {
			Element e = agentModelID.get(0);
			e.setText(ID);
		} else {
			Element newElement = new Element("ID");
			actor.addContent(newElement);
		}
	}

	public void setIDinDistributedAutononomousAgent(Element distributedAutonomousAgent,
			String ID) {
		// TODO: Support multiple actors
		List<Element> distributedAutonomousAgentID = (List<Element>) XMLUtilities
				.executeXPath(distributedAutonomousAgent, "./x:ID", namespaceStr,
						elementFilter);
		if (distributedAutonomousAgentID.size() > 0) {
			Element e = distributedAutonomousAgentID.get(0);
			e.setText(ID);
		} else {
			Element newElement = new Element("ID");
			distributedAutonomousAgent.addContent(newElement);
		}

	}

	public Element getNextAgentModelActor(Object distributedAutononomousAgent,
			Element cachedAgentModelTemplate) {
		@SuppressWarnings("unchecked")
		List<Element> agentModelElements = (List<Element>) XMLUtilities.executeXPath(
				distributedAutononomousAgent, "./x:AgentModels/x:AgentModel",
				namespaceStr, elementFilter);
		if (!firstAgentModelActorPopulated) {
			firstAgentModelActorPopulated = true;
			return agentModelElements.get(0).getChild("Actor", namespace);
		} else {
			Element agentModelActor = cachedAgentModelTemplate;
			agentModelElements.get(0).addContent(agentModelActor);
			return agentModelActor;
		}
	}

	public Element getNextDistributedAutonomousAgent(Object doc,
			Element cachedDistributedAutonomousAgentTemplate) {
		@SuppressWarnings("unchecked")
		List<Element> distributedAutonomousAgentsElements = (List<Element>) XMLUtilities
				.executeXPath(
						doc,
						distributedAutonomousAgentsXpath,
						namespaceStr, elementFilter);
		if (!firstDistributedAutonomousAgentPopulated) {
			firstDistributedAutonomousAgentPopulated = true;
			return distributedAutonomousAgentsElements.get(0).getChild(
					"DistributedAutonomousAgent", namespace);
		} else {
			Element distributedAutononomousAgent = cachedDistributedAutonomousAgentTemplate;
			distributedAutonomousAgentsElements.get(0).addContent(
					distributedAutononomousAgent);
			return distributedAutononomousAgent;
		}
	}

	public Element getNextNonSelfLocationForActor(Element actor,
			Element cachedLocationTemplate) {
		@SuppressWarnings("unchecked")
		// TODO: Rename methods to differentiate Common environment changes from
		// simulation-specific.
		List<Element> environmentChange = (List<Element>) XMLUtilities
				.executeXPath(
						actor,
						"./x:EnvironmentChanges/x:SimulationDefinedEnvironmentChanges/x:EnvironmentChange",
						namespaceStr, elementFilter);
		Element newLocation = cachedLocationTemplate;
		environmentChange.get(0).addContent(newLocation);
		return newLocation;
	}

	/*
	 * Returns an
	 */
	public Element populateThisActorLocationInAgentModel(Element actor, String ID,
			String gridPointX, String gridPointY) {
		// Select Agent Model

		// Go ahead and populate the already created agent model from the Document
		// template
		setIDForActorInAgentModel(actor, ID);
		return populateThisActorLocationInAgentModel(actor, gridPointX, gridPointY);
	}

	public Element populateDistributedAutonomousAgent(Element distributedAutonomousAgent, String ID) {
		setIDinDistributedAutononomousAgent(distributedAutonomousAgent, ID);
		return distributedAutonomousAgent;
	}

	public List<Element> getDistributedAutonomousAgents(Object doc) {
		@SuppressWarnings("unchecked")
		// TODO: Rename methods to differentiate Common environment changes from
		// simulation-specific.
		List<Element> distributedAutonomousAgentsElements = (List<Element>) XMLUtilities
				.executeXPath(
						doc,
						distributedAutonomousAgentsXpath,
						namespaceStr, elementFilter);
		
		return distributedAutonomousAgentsElements.get(0).getChildren("DistributedAutonomousAgent");
	}

	public String getDistributedAutonomousAgentElementID(
			Element distributedAutononomousAgentElement) {
		
		return distributedAutononomousAgentElement.getChild("ID").getValue();

	}

	public List<Element> getAgentModels(Element distributedAutonomousAgentElement) {
		return distributedAutonomousAgentElement.getChildren("AgentModel");

	}

	public String getAgentModelID(Element agentModel) {
		return agentModel.getChild("ID").getValue();
	}
}
