package org.simulationsystems.csf.common.internal.messaging.xml.transformers;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filter;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FRAMEWORK_COMMAND;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.STATUS;
import org.simulationsystems.csf.common.internal.messaging.MessagingUtilities;
import org.simulationsystems.csf.common.internal.messaging.xml.XMLUtilities;

// TODO: Auto-generated Javadoc
/*
 * Transforms framework messages into XML
 */
/**
 * The helper class for the FrameworkMessageImpl implementation of the FrameworkMessage
 * interface wrapping the CsfMessageExchange XML Document
 * 
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class FrameworkMessageDocumentHelper {

	/** The framework message. */
	FrameworkMessage frameworkMessage;

	/** The framework to distributed system command_ x path. */
	final private String frameworkToDistributedSystemCommand_XPath = "/x:CsfMessageExchange/x:ReceivingEntities/x:DistributedSystem/x:DistributedAutonomousAgents/x:AllDistributedAutonomousAgents/x:ControlMessages/x:Command";

	/** The framework to simulation engine commnad_ x path. */
	final private String frameworkToSimulationEngineCommnad_XPath = "/x:CsfMessageExchange/x:ReceivingEntities/x:SimulationSystem/x:ControlMessages/x:Command";

	/** The framework status_ x path. */
	final private String frameworkStatus_XPath = "/x:CsfMessageExchange/x:Status";

	/** The distributed autonomous agents xpath. */
	final private String distributedAutonomousAgentsXpath = "/x:CsfMessageExchange/x:ReceivingEntities/x:DistributedSystem/x:DistributedAutonomousAgents";
	// TODO: Get this from the configuration
	/** The namespace str. */
	private final String namespaceStr = "http://www.simulationsystems.org/csf/schemas/CsfMessageExchange/0.1.0";

	/** The namespace. */
	private final Namespace namespace = Namespace.getNamespace("x", namespaceStr);

	/** The element filter. */
	private final Filter<Element> elementFilter = new org.jdom2.filter.ElementFilter();

	/**
	 * Instantiates a new framework message document helper.
	 * 
	 * @param frameworkMessage
	 *            the framework message
	 */
	public FrameworkMessageDocumentHelper(final FrameworkMessage frameworkMessage) {
		this.frameworkMessage = frameworkMessage;
	}

	/*
	 * Side effect includes removing the distributedAutononomousAgentElement from the
	 * current Document
	 */
	/**
	 * Adds the distributed autonomous agent to a CsfMessageExchange Document. Side effect
	 * includes detaching the distributedAutononomousAgentElement from the current
	 * Document. Generally you would no longer need to use the original Document. If you
	 * do need to still use the original Document, it is best to clone the distributed
	 * autonomous agent element prior to calling this method.
	 * 
	 * @param doc
	 *            the doc
	 * @param distributedAutononomousAgentElement
	 *            the distributed autononomous agent element
	 * @param removeChildren
	 *            the remove children
	 * @return the document
	 */
	public Document addDistributedAutonomousAgent(final Document doc,
			final Element distributedAutononomousAgentElement,
			final boolean removeChildren) {
		@SuppressWarnings("unchecked")
		final// TODO: Rename methods to differentiate Common environment changes from
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

	/**
	 * Convert string to framework command.
	 * 
	 * @param str
	 *            the str
	 * @return the framework command
	 */
	private FRAMEWORK_COMMAND convertStringToFrameworkCommand(final String str) {
		for (final FRAMEWORK_COMMAND cmd : FRAMEWORK_COMMAND.values()) {
			if (cmd.toString().equalsIgnoreCase(str))
				return cmd;
		}
		return null;
	}

	/**
	 * Convert string to status.
	 * 
	 * @param str
	 *            the str
	 * @return the status
	 */
	public STATUS convertStringToStatus(final String str) {
		for (final STATUS st : STATUS.values()) {
			if (st.toString().equals(str))
				return st;
		}
		return null;
	}

	/**
	 * Framework message to xml string.
	 * 
	 * @param document
	 *            the document
	 * @param prettyPrint
	 *            the pretty print
	 * @return the string
	 */
	public String frameworkMessageToXMLString(final Document document,
			final boolean prettyPrint) {

		return MessagingUtilities.convertDocumentToXMLString(document, prettyPrint); // return
																						// new
																						// XMLOutputter().outputString(newMessage);
	}

	/**
	 * Gets the agent models element from the distributed autonomous agent element in the
	 * XML
	 * 
	 * @param distributedAutonomousAgentElement
	 *            the distributed autonomous agent element
	 * @return the agent models
	 */
	public List<Element> getAgentModels(final Element distributedAutonomousAgentElement) {
		final Element agentModelsElement = distributedAutonomousAgentElement.getChild(
				"AgentModels", namespace);
		return agentModelsElement.getChildren("AgentModel", namespace);
	}

	/**
	 * Gets the list of distributed autonomous agent elements from a CsfMessageExchange
	 * document
	 * 
	 * @param doc
	 *            the doc
	 * @return the distributed autonomous agent elements
	 */
	public List<Element> getDistributedAutonomousAgentElements(final Object doc) {
		@SuppressWarnings("unchecked")
		final// TODO: Rename methods to differentiate Common environment changes from
		// simulation-specific.
		List<Element> distributedAutonomousAgentsElements = (List<Element>) XMLUtilities
				.executeXPath(doc, distributedAutonomousAgentsXpath, namespaceStr,
						elementFilter);

		return distributedAutonomousAgentsElements.get(0).getChildren(
				"DistributedAutonomousAgent", namespace);
	}

	/**
	 * Gets the distributed autonomous agent id.
	 * 
	 * @param distributedAutononomousAgentElement
	 *            the distributed autononomous agent element
	 * @return the distributed autonomous agent id
	 */
	public String getDistributedAutonomousAgentID(
			final Element distributedAutononomousAgentElement) {

		return distributedAutononomousAgentElement.getChild("ID", namespace).getValue();

	}

	/**
	 * Gets the distributed autonomous agents element.
	 * 
	 * @param doc
	 *            the doc
	 * @return the distributed autonomous agents element
	 */
	public Element getDistributedAutonomousAgentsElement(final Object doc) {
		// TODO: Rename methods to differentiate Common environment changes from
		// simulation-specific.
		@SuppressWarnings("unchecked")
		final List<Element> distributedAutonomousAgentsElements = (List<Element>) XMLUtilities
				.executeXPath(doc, distributedAutonomousAgentsXpath, namespaceStr,
						elementFilter);

		return distributedAutonomousAgentsElements.get(0);
	}

	/**
	 * Gets the document.
	 * 
	 * @return the document
	 */
	public Document getDocument() {
		return frameworkMessage.getDocument();
	}

	/**
	 * Gets the first agent model actor agent model id.
	 * 
	 * @param agentModel
	 *            the agent model
	 * @return the first agent model actor agent model id
	 */
	public String getFirstAgentModelActorAgentModelID(final Element agentModel) {
		final Element actorElement = agentModel.getChild("Actor", namespace);
		return actorElement.getChild("ID", namespace).getValue();
	}

	/**
	 * Gets the framework to distributed system command.
	 * 
	 * @return the framework to distributed system command
	 */
	public FRAMEWORK_COMMAND getFrameworkToDistributedSystemCommand() {
		@SuppressWarnings("unchecked")
		final// TODO: Get the namespace in the configuration. Search for all other
		// places using this method
		// TODO: Make the namespace configurable
		List<Element> xPathSearchedNodes = (List<Element>) XMLUtilities.executeXPath(
				frameworkMessage.getDocument(),
				frameworkToDistributedSystemCommand_XPath, namespaceStr, elementFilter);
		System.out.println(xPathSearchedNodes.get(0).getValue());

		final String commandStr = xPathSearchedNodes.get(0).getValue();
		return convertStringToFrameworkCommand(commandStr);

	}

	/**
	 * Gets the framework to simulation engine command.
	 * 
	 * @return the framework to simulation engine command
	 */
	public FRAMEWORK_COMMAND getFrameworkToSimulationEngineCommand() {
		@SuppressWarnings("unchecked")
		final// TODO: Get the namespace in the configuration. Search for all other
		// places using this method
		// TODO: Make the namespace configurable
		List<Element> xPathSearchedNodes = (List<Element>) XMLUtilities.executeXPath(
				frameworkMessage.getDocument(), frameworkToSimulationEngineCommnad_XPath,
				namespaceStr, elementFilter);
		System.out.println(xPathSearchedNodes.get(0).getValue());

		final String commandStr = xPathSearchedNodes.get(0).getValue();
		return convertStringToFrameworkCommand(commandStr);

	}

	// FIXME: Check with an id?
	/**
	 * Gets the next agent model actor in the distributed autonomous agent element. If the
	 * cachedAgentModelTemplate is null, it get the first element and returns it.
	 * Otherwise, it creates a new element and returns it.
	 * 
	 * @param distributedAutonomousAgent
	 *            the distributed autononomous agent
	 * @param cachedAgentModelTemplate
	 *            the cached agent model template
	 * @return the next agent model actor
	 */
	public Element getNextAgentModelActor(final Object distributedAutonomousAgent,
			final Element cachedAgentModelTemplate) {
		@SuppressWarnings("unchecked")
		final List<Element> agentModelsElements = (List<Element>) XMLUtilities
				.executeXPath(distributedAutonomousAgent, "./x:AgentModels",
						namespaceStr, elementFilter);
		Element agentModelActor = null;
		if (cachedAgentModelTemplate != null) {
			final Element agentModel = new Element("AgentModel", namespace);
			agentModelActor = cachedAgentModelTemplate;
			agentModelsElements.get(0).addContent(agentModel);
			agentModel.addContent(agentModelActor);
			return agentModelActor;
		} else
			agentModelActor = agentModelsElements.get(0)
					.getChild("AgentModel", namespace).getChild("Actor", namespace);
		return agentModelActor;
	}

	/*
	 * if cachedDistributedAutonomousAgentTemplate is blank, don't add the element
	 */
	/**
	 * Gets the next distributed autonomous agent. If the
	 * cachedDistributedAutonomousAgentTemplate is null, it get the first element and
	 * returns it. Otherwise, it creates a new element and returns it.
	 * 
	 * @param doc
	 *            the doc
	 * @param cachedDistributedAutonomousAgentTemplate
	 *            the cached distributed autonomous agent template
	 * @return the next distributed autonomous agent
	 */
	public Element getNextDistributedAutonomousAgent(final Object doc,
			final Element cachedDistributedAutonomousAgentTemplate) {
		@SuppressWarnings("unchecked")
		final Element distributedAutonomousAgentsElement = getDistributedAutonomousAgentsElement(doc);

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

	/**
	 * Get the location element in the simulation-specific section for the next agent
	 * model actor.
	 * 
	 * @param actor
	 *            the actor
	 * @param cachedLocationTemplate
	 *            the cached location template
	 * @return the next agent model actor's location elementr
	 */
	public Element getNextNonSelfSimulationDefinedLocationForActor(final Element actor,
			final Element cachedLocationTemplate) {
		@SuppressWarnings("unchecked")
		final// TODO: Rename methods to differentiate Common environment changes from
		// simulation-specific.
		List<Element> simulationDefinedEnvironmentChangesElements = (List<Element>) XMLUtilities
				.executeXPath(actor,
						"./x:EnvironmentChanges/x:SimulationDefinedEnvironmentChanges",
						namespaceStr, elementFilter);
		final Element newLocation = cachedLocationTemplate;
		simulationDefinedEnvironmentChangesElements.get(0).addContent(newLocation);
		return newLocation;
	}

	/**
	 * Gets the actor's own location element in the simulation-specific section for the
	 * next agent model actor for a given distributed autonomous agent element.
	 * 
	 * @param distributedAutononomousAgentElement
	 *            the distributed autononomous agent element
	 * @param msg
	 *            the msg
	 * @return the self location
	 */
	public List<String> getSelfLocationFromFirstAgentModel(
			final Element distributedAutononomousAgentElement, final FrameworkMessage msg) {
		// FIXME: Why does this have to be an element, and not Document?
		final Element agentModelActor = msg.getNextAgentModelActor(
				distributedAutononomousAgentElement, null);
		final List<Element> commonEnvironmentChangesElements = (List<Element>) XMLUtilities
				.executeXPath(agentModelActor,
						"./x:EnvironmentChanges/x:CommonEnvironmentChanges",
						namespaceStr, elementFilter);

		// FIXME: check the attributes
		final List<Element> locations = (List<Element>) XMLUtilities.executeXPath(
				commonEnvironmentChangesElements.get(0),
				"./x:EnvironmentChange/x:Location[@id='self']", namespaceStr,
				elementFilter);
		final Element location = locations.get(0);

		XMLUtilities.convertDocumentToXMLString(agentModelActor, true);

		XMLUtilities.convertDocumentToXMLString(agentModelActor, true);

		final String xValue = location.getChild("GridPointX", namespace).getText();
		final String yValue = location.getChild("GridPointY", namespace).getText();
		System.out.println("Self Grid Point X: " + xValue);
		System.out.println("Self Grid Point Y: " + yValue);
		final List<String> coordinate = new ArrayList<String>();
		coordinate.add(xValue);
		coordinate.add(yValue);

		return coordinate;

	}

	// TODO: Handle messages with multiple distributed autonomous agent elemements
	/**
	 * Gets the actor's own location element in the simulation-specific section for the
	 * next agent model actor in the next distributed autonomous agent. The actor's own
	 * location is in the CommonEnvironmentChanges section.
	 * 
	 * @param msg
	 *            the msg
	 * @return the self location
	 */
	public List<String> getSelfLocationFromNextDistributedAutonomousAgentNextAgentModelActor(
			final FrameworkMessage msg) {
		final Element distributedAutonomousAgentElement = msg
				.getNextDistributedAutonomousAgent(msg.getDocument(), null);

		return getSelfLocationFromFirstAgentModel(distributedAutonomousAgentElement, msg);
	}

	/**
	 * Gets the simulation defined environment changes element.
	 * 
	 * @param actor
	 *            the actor
	 * @return the simulation defined environment changes element
	 */
	public Element getSimulationDefinedEnvironmentChangesElement(final Element actor) {
		@SuppressWarnings("unchecked")
		final// TODO: Rename methods to differentiate Common environment changes from
		// simulation-specific.
		List<Element> simulationDefinedEnvironmentChangesElements = (List<Element>) XMLUtilities
				.executeXPath(actor,
						"./x:EnvironmentChanges/x:SimulationDefinedEnvironmentChanges",
						namespaceStr, elementFilter);
		return simulationDefinedEnvironmentChangesElements.get(0);
	}

	/**
	 * Gets the status.
	 * 
	 * @return the status
	 */
	public STATUS getStatus() {
		@SuppressWarnings("unchecked")
		final// TODO: Get the namespace in the configuration. Search for all other
		// places using this method
		// TODO: Make the namespace configurable
		List<Element> xPathSearchedNodes = (List<Element>) XMLUtilities.executeXPath(
				frameworkMessage.getDocument(), frameworkStatus_XPath,
				"http://www.simulationsystems.org/csf/schemas/CsfMessageExchange/0.1.0",
				elementFilter);
		System.out.println(xPathSearchedNodes.get(0).getValue());

		final String status = xPathSearchedNodes.get(0).getValue();
		return convertStringToStatus(status);

	}

	// TODO: Extents/multiple dimensions
	// TODO: Set the ID
	/**
	 * Populate this actor location in agent model.
	 * 
	 * @param actor
	 *            the actor
	 * @param gridPointX
	 *            the grid point x
	 * @param gridPointY
	 *            the grid point y
	 * @param cachedLocationTemplate
	 *            the cached location template
	 * @return the element
	 */
	public Element populateThisActorLocationInAgentModel(final Element actor,
			final String gridPointX, final String gridPointY,
			final Element cachedLocationTemplate) {
		@SuppressWarnings("unchecked")
		final// TODO: Support multiple actors
		List<Element> commonEnvironmentChangesElements = (List<Element>) XMLUtilities
				.executeXPath(actor, "./x:EnvironmentChanges/x:CommonEnvironmentChanges",
						namespaceStr, elementFilter);
		// cachedLocationTemplate
		// /x:EnvironmentChange/x:Location[@id='self']

		commonEnvironmentChangesElements.get(0).addContent(cachedLocationTemplate);

		final Element locationElement = cachedLocationTemplate.getChild("Location",
				namespace);
		locationElement.getChild("GridPointX", namespace).setText(gridPointX);
		locationElement.getChild("GridPointY", namespace).setText(gridPointY);
		return actor;
	}

	/**
	 * Removes the distributed autonomous agents from a Document
	 * 
	 * @param doc
	 *            the doc
	 */
	public void removeDistributedAutonomousAgents(final Document doc) {
		final List<Element> distributedAutonomousAgentElements = (List<Element>) XMLUtilities
				.executeXPath(doc, distributedAutonomousAgentsXpath
						+ "/x:DistributedAutonomousAgent", namespaceStr, elementFilter);
		for (final Element distributedAutonomousAgentElement : distributedAutonomousAgentElements) {
			distributedAutonomousAgentElement.detach();
		}
	}

	/**
	 * Populate distributed autonomous agent.
	 * 
	 * @param distributedAutonomousAgent
	 *            the distributed autonomous agent
	 * @param ID
	 *            the id
	 * @return the element
	 */
	public Element setDistributedAutonomousAgentID(
Element distributedAutonomousAgent, String ID) {
		setIDinDistributedAutononomousAgent(distributedAutonomousAgent, ID);
		return distributedAutonomousAgent;
	}

	/**
	 * Sets the framework to distributed system command.
	 * 
	 * @param command
	 *            the new framework to distributed system command
	 */
	public void setFrameworkToDistributedSystemCommand(final FRAMEWORK_COMMAND command) {
		@SuppressWarnings("unchecked")
		final// TODO: Get the namespace in the configuration. Search for all other
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

	/**
	 * Sets the framework to simulation engine commnad.
	 * 
	 * @param command
	 *            the new framework to simulation engine commnad
	 */
	public void setFrameworkToSimulationEngineCommnad(final FRAMEWORK_COMMAND command) {
		@SuppressWarnings("unchecked")
		final// TODO: Get the namespace in the configuration. Search for all other
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

	/**
	 * Sets the id for actor in agent model.
	 * 
	 * @param actor
	 *            the actor
	 * @param ID
	 *            the id
	 */
	public void setIDForActorInAgentModel(final Element actor, final String ID) {
		// TODO: Support multiple actors
		final List<Element> agentModelID = (List<Element>) XMLUtilities.executeXPath(
				actor, "./x:ID", namespaceStr, elementFilter);
		if (agentModelID.size() > 0) {
			final Element e = agentModelID.get(0);
			e.setText(ID);
		} else {
			final Element newElement = new Element("ID");
			actor.addContent(newElement);
		}
	}

	/**
	 * Sets the i din distributed autononomous agent.
	 * 
	 * @param distributedAutonomousAgent
	 *            the distributed autonomous agent
	 * @param ID
	 *            the id
	 */
	public void setIDinDistributedAutononomousAgent(
			final Element distributedAutonomousAgent, final String ID) {
		// TODO: Support multiple actors
		final List<Element> distributedAutonomousAgentID = (List<Element>) XMLUtilities
				.executeXPath(distributedAutonomousAgent, "./x:ID", namespaceStr,
						elementFilter);
		if (distributedAutonomousAgentID.size() > 0) {
			final Element e = distributedAutonomousAgentID.get(0);
			e.setText(ID);
		} else {
			final Element newElement = new Element("ID");
			distributedAutonomousAgent.addContent(newElement);
		}

	}

	/**
	 * Sets the status.
	 * 
	 * @param status
	 *            the new status
	 */
	public void setStatus(final STATUS status) {
		@SuppressWarnings("unchecked")
		final// TODO: Get the namespace in the configuration. Search for all other
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

}
