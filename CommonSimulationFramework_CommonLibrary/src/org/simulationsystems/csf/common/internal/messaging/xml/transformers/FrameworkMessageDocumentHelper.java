package org.simulationsystems.csf.common.internal.messaging.xml.transformers;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filter;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.simulationsystems.csf.common.csfmodel.SYSTEM_TYPE;
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
	// TODO: Set the ID
	private Element populateThisActorLocationInAgentModel(Element actor,
			String gridPointX, String gridPointY, Element cachedLocationTemplate) {
		@SuppressWarnings("unchecked")
		// TODO: Support multiple actors
		List<Element> commonEnvironmentChangesElements = (List<Element>) XMLUtilities
				.executeXPath(
						actor,
						"./x:EnvironmentChanges/x:CommonEnvironmentChanges",
						namespaceStr, elementFilter);
		//cachedLocationTemplate
		///x:EnvironmentChange/x:Location[@id='self']
		
		commonEnvironmentChangesElements.get(0).addContent(cachedLocationTemplate);
		
		Element locationElement =  cachedLocationTemplate.getChild("Location",namespace);
		locationElement.getChild("GridPointX", namespace).setText(gridPointX);
		locationElement.getChild("GridPointY", namespace).setText(gridPointY);
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

	// FIXME: Check with an id?
	public Element getNextAgentModelActor(Object distributedAutononomousAgent,
			Element cachedAgentModelTemplate) {
		@SuppressWarnings("unchecked")
		List<Element> agentModelsElements = (List<Element>) XMLUtilities.executeXPath(
				distributedAutononomousAgent, "./x:AgentModels",
				namespaceStr, elementFilter);
		Element agentModelActor = null;
		if (cachedAgentModelTemplate != null) {
			Element agentModel = new Element("AgentModel", namespace);
			agentModelActor = cachedAgentModelTemplate;
			agentModelsElements.get(0).addContent(agentModel);
			agentModel.addContent(agentModelActor);
			return agentModelActor;
		} else
			agentModelActor = agentModelsElements.get(0).getChild("AgentModel", namespace).getChild("Actor", namespace);
		return agentModelActor;
	}

	/*
	 * if cachedDistributedAutonomousAgentTemplate is blank, don't add the element
	 */
	public Element getNextDistributedAutonomousAgent(Object doc,
			Element cachedDistributedAutonomousAgentTemplate) {
		@SuppressWarnings("unchecked")
		Element distributedAutonomousAgentsElement = getDistributedAutonomousAgentsElement(doc);
		List<Element> distributedAutonomousAgentElements = getDistributedAutonomousAgentElements(doc);

		Element distributedAutononomousAgent = null;
		if (cachedDistributedAutonomousAgentTemplate != null) {
			distributedAutononomousAgent = cachedDistributedAutonomousAgentTemplate;
			distributedAutonomousAgentsElement.addContent(distributedAutononomousAgent);
			return distributedAutononomousAgent;
		} else
			distributedAutononomousAgent = distributedAutonomousAgentsElement.getChild(
					"DistributedAutonomousAgent", namespace);
		return distributedAutononomousAgent;
	}

	public Element getNextNonSelfLocationForActor(Element actor,
			Element cachedLocationTemplate) {
		@SuppressWarnings("unchecked")
		// TODO: Rename methods to differentiate Common environment changes from
		// simulation-specific.
		List<Element> simulationDefinedEnvironmentChangesElements = (List<Element>) XMLUtilities
				.executeXPath(
						actor,
						"./x:EnvironmentChanges/x:SimulationDefinedEnvironmentChanges",
						namespaceStr, elementFilter);
		Element newLocation = cachedLocationTemplate;
		simulationDefinedEnvironmentChangesElements.get(0).addContent(newLocation);
		return newLocation;
	}

	/*
	 * Returns an
	 */
	public Element populateThisActorLocationInAgentModel(Element actor, String ID,
			String gridPointX, String gridPointY, Element cachedLocationTemplate) {
		// Select Agent Model

		// Go ahead and populate the already created agent model from the Document
		// template
		setIDForActorInAgentModel(actor, ID);
		return populateThisActorLocationInAgentModel(actor, gridPointX, gridPointY, cachedLocationTemplate);
	}

	public Element populateDistributedAutonomousAgent(Element distributedAutonomousAgent,
			String ID) {
		setIDinDistributedAutononomousAgent(distributedAutonomousAgent, ID);
		return distributedAutonomousAgent;
	}

	public Element getDistributedAutonomousAgentsElement(Object doc) {
		@SuppressWarnings("unchecked")
		// TODO: Rename methods to differentiate Common environment changes from
		// simulation-specific.
		List<Element> distributedAutonomousAgentsElements = (List<Element>) XMLUtilities
				.executeXPath(doc, distributedAutonomousAgentsXpath, namespaceStr,
						elementFilter);

		return distributedAutonomousAgentsElements.get(0);
	}

	public List<Element> getDistributedAutonomousAgentElements(Object doc) {
		@SuppressWarnings("unchecked")
		// TODO: Rename methods to differentiate Common environment changes from
		// simulation-specific.
		List<Element> distributedAutonomousAgentsElements = (List<Element>) XMLUtilities
				.executeXPath(doc, distributedAutonomousAgentsXpath, namespaceStr,
						elementFilter);

		return distributedAutonomousAgentsElements.get(0).getChildren(
				"DistributedAutonomousAgent", namespace);
	}

	public String getDistributedAutonomousAgentID(
			Element distributedAutononomousAgentElement) {

		return distributedAutononomousAgentElement.getChild("ID", namespace).getValue();

	}

	public List<Element> getAgentModels(Element distributedAutonomousAgentElement) {
		 Element agentModelsElement = distributedAutonomousAgentElement.getChild("AgentModels",namespace);
		 return agentModelsElement.getChildren("AgentModel", namespace);
	}

	public String getFirstAgentModelActorAgentModelID(Element agentModel) {
		Element actorElement = agentModel.getChild("Actor", namespace);
		return actorElement.getChild("ID", namespace).getValue();
	}

	/*
	 * Side effect includes removing the distributedAutononomousAgentElement from the
	 * current Document
	 */
	public Document addDistributedAutonomousAgent(Document doc,
			Element distributedAutononomousAgentElement, boolean removeChildren) {
		@SuppressWarnings("unchecked")
		// TODO: Rename methods to differentiate Common environment changes from
		// simulation-specific.
		List<Element> distributedAutonomousAgents = (List<Element>) XMLUtilities
				.executeXPath(doc, distributedAutonomousAgentsXpath, namespaceStr,
						elementFilter);
		if (removeChildren)
			distributedAutonomousAgents.get(0).removeContent();
		distributedAutonomousAgents.get(0).addContent(
				distributedAutononomousAgentElement.detach());
		return doc;
	}

	public void removeDistributedAutonomousAgents(Document doc) {
		List<Element> distributedAutonomousAgentElements = (List<Element>) XMLUtilities
				.executeXPath(doc, distributedAutonomousAgentsXpath+"/x:DistributedAutonomousAgent", namespaceStr,
						elementFilter);
		for (Element distributedAutonomousAgentElement : distributedAutonomousAgentElements) {
			distributedAutonomousAgentElement.detach();
		}
		
	}

	public List<String> getSelfLocation(Element distributedAutononomousAgentElement,
			FrameworkMessage msg) {
		// FIXME: Why does this have to be an element, and not Document?
		Element agentModelActor = msg.getNextAgentModelActor(
				distributedAutononomousAgentElement, null);
		List<Element> commonEnvironmentChangesElements = (List<Element>) XMLUtilities
				.executeXPath(agentModelActor,
						"./x:EnvironmentChanges/x:CommonEnvironmentChanges",
						namespaceStr, elementFilter);

		// FIXME: check the attributes
		List<Element> locations = (List<Element>) XMLUtilities.executeXPath(
				commonEnvironmentChangesElements.get(0),
				"./x:EnvironmentChange/x:Location[@id='self']", namespaceStr,
				elementFilter);
		Element location = locations.get(0);

		XMLUtilities.convertDocumentToXMLString(agentModelActor, true);

		XMLUtilities.convertDocumentToXMLString(agentModelActor, true);

		String xValue = location.getChild("GridPointX", namespace).getText();
		String yValue = location.getChild("GridPointY", namespace).getText();
		System.out.println("Self Grid Point X: " + xValue);
		System.out.println("Self Grid Point Y: " + yValue);
		List<String> coordinate = new ArrayList<String>();
		coordinate.add(xValue);
		coordinate.add(yValue);

		/*
		 * location.setAttribute("category", "neighborhood");
		 * location.setAttribute("includecenter", "true");
		 * location.setAttribute("entitytype", "Zombie");
		 */
		return coordinate;
		
	}
		
	//TODO: Handle messages with multiple distributed autonomous agent elemements
	public List<String> getSelfLocation(FrameworkMessage msg) {
		Element distributedAutonomousAgentElement = msg
				.getNextDistributedAutonomousAgent(msg.getDocument(), null);

		return getSelfLocation(distributedAutonomousAgentElement,
				 msg);
	}
	
}
