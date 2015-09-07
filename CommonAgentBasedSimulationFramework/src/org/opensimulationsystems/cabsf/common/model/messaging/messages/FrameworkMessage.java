package org.opensimulationsystems.cabsf.common.model.messaging.messages;

import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;

/**
 * The Interface used throughout the CABSF to represent the XML Document
 * (CabsfMessageExchange). This interface contains several convenience methods for
 * accessing and populating different parts of the FrameworkMessage.
 *
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public interface FrameworkMessage {

	/**
	 * Adds the distributed autonomous agent to a CabsfMessageExchange Document. Side
	 * effect includes detaching the distributedAutononomousAgentElement from the current
	 * Document. Generally you would no longer need to use the original Document. If you
	 * do need to still use the original Document, it is best to clone the distributed
	 * autonomous agent element prior to calling this method.
	 *
	 * @param CabsfMessageExchangeDoc
	 *            the document to add the distributed autonomous agent element to.
	 * @param distributedAutononomousAgentElement
	 *            the distributed autonomous agent element
	 * @param removeChildren
	 *            whether to remove the distributed autonomous agents first from this
	 *            Document before adding the new distributed autonomous agents.
	 * @return the document
	 */
	public Document addDistributedAutonomousAgent(Document CabsfMessageExchangeDoc,
			Element distributedAutononomousAgentElement, boolean removeChildren);

	/**
	 * Gets the agent models element from the distributed autonomous agent element in the
	 * XML
	 *
	 * @param distributedAutonomousAgentElement
	 *            the distributed autonomous agent element
	 * @return the agent models
	 */
	List<Element> getAgentModels(Element distributedAutonomousAgentElement);

	/**
	 * Gets the list of distributed autonomous agent elements from a CabsfMessageExchange
	 * document
	 *
	 * @param CabsfMessageExchangeDoc
	 *            the cabsf message exchange doc
	 * @return the distributed autonomous agent elements
	 */
	List<Element> getDistributedAutonomousAgentElements(Object CabsfMessageExchangeDoc);

	/**
	 * Gets the distributed autonomous agent id from a distributed autonomous agent
	 * element.
	 *
	 * @param distributedAutononomousAgentElement
	 *            the distributed autononomous agent element
	 * @return the distributed autonomous agent id
	 */
	String getDistributedAutonomousAgentID(Element distributedAutononomousAgentElement);

	/**
	 * Gets the CabsfMessageExchange XML Document which thie FrameworkMessage wraps
	 *
	 * @return the document
	 */
	Document getDocument();

	/**
	 * Gets the first agent model actor agent model ID from an agent model element
	 *
	 * @param agentModel
	 *            the agent model
	 * @return the first agent model actor agent model id
	 */
	String getFirstAgentModelActorAgentModelID(Element agentModel);

	/**
	 * Gets the CABSF framework to distributed system command.
	 *
	 * @return the framework to distributed system command
	 */
	FRAMEWORK_COMMAND getFrameworkToDistributedSystemCommand();

	/**
	 * Gets the CABSF framework to simulation engine command.
	 *
	 * @return the framework to simulation engine command
	 */
	FRAMEWORK_COMMAND getFrameworkToSimulationEngineCommand();

	/**
	 * Gets the next agent model actor in the distributed autonomous agent element. If the
	 * cachedAgentModelTemplate is null, it get the first element and returns it.
	 * Otherwise, it creates a new element and returns it.
	 *
	 * @param distributedAutonomousAgent
	 *            the distributed autonomous agent element
	 * @param cachedAgentModelTemplate
	 *            the cached agent model template
	 * @return the next agent model actor
	 */
	Element getNextAgentModelActor(Object distributedAutonomousAgent,
			Element cachedAgentModelTemplate);

	/**
	 * Gets the next distributed autonomous agent in the CabsfMessageExchange Document. If
	 * the cachedDistributedAutonomousAgentTemplate is null, it get the first element and
	 * returns it. Otherwise, it creates a new element and returns it.
	 *
	 * @param doc
	 *            the CabsfMessageExchange Document
	 * @param cachedDistributedAutonomousAgentTemplate
	 *            the cache distributed autonomous agent template
	 * @return the next distributed autonomous agent
	 */
	Element getNextDistributedSoftwareAgentElement(Document doc,
			Element cachedDistributedAutonomousAgentTemplate);

	/**
	 * Gets the location element in the simulation-specific section for the next agent
	 * model actor.
	 *
	 * @param actor
	 *            the actor
	 * @param cachedLocationTemplate
	 *            the cached location template
	 * @return the next agent model actor's location element
	 */
	// FIXME: Add validation to check for existing locations other than self location?
	Element getNextNonSelfSimulationDefinedLocationForActor(Element actor,
			Element cachedLocationTemplate);

	/**
	 * Gets the actor's own location element in the simulation-specific section for the
	 * next agent model actor for a given distributed autonomous agent element. The
	 * actor's own location is in the CommonEnvironmentChanges section.
	 *
	 * @param distributedSoftwareAgentElement
	 *            the distributed autononomous agent element
	 * @param msg
	 *            the FrameworkMessage
	 * @return the self location
	 */
	List<String> getSelfLocationFromFirstAgentModel(
			Element distributedSoftwareAgentElement, FrameworkMessage msg);

	/**
	 * Gets the actor's own location element in the simulation-specific section for the
	 * next agent model actor in the next distributed autonomous agent. The actor's own
	 * location is in the CommonEnvironmentChanges section.
	 *
	 * @param msg
	 *            the FrameworkMessage
	 * @return the self location list of grid points
	 */
	// FIXME: Add validation to check for existing locations other than self location?
	List<String> getThisAgentLocationFromNextSoftwareAgentNextAgentModelActorInFrameworkMessage(
			FrameworkMessage msg);

	/**
	 * Gets the simulation defined environment changes element.
	 *
	 * @param actor
	 *            the actor
	 * @return the simulation defined environment changes element
	 */
	Element getSimulationDefinedEnvironmentChangesElement(Element actor);

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	STATUS getStatus();

	/**
	 * Populate this location in agent model actor.
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
	// FIXME: Remove the cachedLocationTemplate fron the signature if it's always going to
	// be populated?
	Element populateThisLocationInAgentModelActor(Element actor, String gridPointX,
			String gridPointY, Element cachedLocationTemplate);

	/**
	 * Removes the distributed autonomous agents from a Document
	 *
	 * @param CabsfMessageExchangeDoc
	 *            the cabsf message exchange doc
	 */
	public void removeDistributedAutonomousAgents(Document CabsfMessageExchangeDoc);

	/**
	 * Sets the distributed autonomous agent id.
	 *
	 * @param distributedAutonomousAgent
	 *            the distributed autonomous agent
	 * @param ID
	 *            the id
	 * @return the element
	 */
	Element setDistributedAutonomousAgentID(Element distributedAutonomousAgent, String ID);

	/**
	 * Sets the framework to distributed system command.
	 *
	 * @param frameworkToDistributedSystemCommand
	 *            the new framework to distributed system command
	 */
	void setFrameworkToDistributedSystemCommand(
			FRAMEWORK_COMMAND frameworkToDistributedSystemCommand);

	/**
	 * Sets the framework to simulation engine command.
	 *
	 * @param frameworkToDistributedSystemCommand
	 *            the new framework to simulation engine command
	 */
	void setFrameworkToSimulationEngineCommand(
			FRAMEWORK_COMMAND frameworkToDistributedSystemCommand);

	/**
	 * Sets the ID for actor in agent model.
	 *
	 * @param actor
	 *            the actor
	 * @param ID
	 *            the ID
	 */
	void setIDForActorInAgentModel(Element actor, String ID);

	/**
	 * Sets the status.
	 *
	 * @param readyToStartSimulation
	 *            the new status
	 */
	public void setStatus(STATUS readyToStartSimulation);

	/**
	 * To pretty printed xml string.
	 *
	 * @return the string
	 */
	String toPrettyPrintedXMLString();

	/**
	 * Transform to common messaging xml string.
	 *
	 * @param prettyPrint
	 *            the pretty print
	 * @return the string
	 */
	public String transformToCommonMessagingXMLString(boolean prettyPrint);
}
