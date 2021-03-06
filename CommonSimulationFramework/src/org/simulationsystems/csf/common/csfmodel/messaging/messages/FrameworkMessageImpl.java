package org.simulationsystems.csf.common.csfmodel.messaging.messages;

import java.io.IOException;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.simulationsystems.csf.common.csfmodel.SYSTEM_TYPE;
import org.simulationsystems.csf.common.csfmodel.csfexceptions.CsfCheckedException;
import org.simulationsystems.csf.common.internal.messaging.MessagingUtilities;
import org.simulationsystems.csf.common.internal.messaging.xml.transformers.FrameworkMessageDocumentHelper;

/**
 * The implementation class for the FrameworkMessage interface which wraps the
 * CsfMessageExchange XML Document
 * 
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class FrameworkMessageImpl implements FrameworkMessage {

	/** The framework message document helper. */
	private final FrameworkMessageDocumentHelper frameworkMessageDocumentHelper = new FrameworkMessageDocumentHelper(
			this);

	/** The document. */
	private Document document;

	/** The source system type. */
	private SYSTEM_TYPE sourceSystemType;

	/** The target system type. */
	private SYSTEM_TYPE targetSystemType;

	/**
	 * Instantiates a new framework message impl.
	 */
	@SuppressWarnings("unused")
	private FrameworkMessageImpl() {

	}

	/*
	 * Used for outgoing messages. Pass in the framework message Document template
	 */
	/**
	 * Instantiates a new framework message impl.
	 * 
	 * @param sourceSystemType
	 *            the source system type
	 * @param targetSystemType
	 *            the target system type
	 * @param document
	 *            the document
	 */
	public FrameworkMessageImpl(final SYSTEM_TYPE sourceSystemType,
			final SYSTEM_TYPE targetSystemType, final Document document) {

		this.document = document;

		this.sourceSystemType = sourceSystemType;
		this.targetSystemType = targetSystemType;
	}

	/*
	 * Used for incoming messages. Pass in the XML String from the framework
	 */
	/**
	 * Instantiates a new framework message impl.
	 * 
	 * @param sourceSystemType
	 *            the source system type
	 * @param targetSystemType
	 *            the target system type
	 * @param xmlString
	 *            the xml string
	 * @throws CsfCheckedException
	 *             the csf checked exception
	 */
	public FrameworkMessageImpl(final SYSTEM_TYPE sourceSystemType,
			final SYSTEM_TYPE targetSystemType, final String xmlString)
			throws CsfCheckedException {
		super();
		try {
			this.document = MessagingUtilities.createDocumentFromString(xmlString);
		} catch (JDOMException | IOException e) {
			// TODO: Where to catch this?
			throw new CsfCheckedException("Unable to parse the message XML", e);
		}

		this.sourceSystemType = sourceSystemType;
		this.targetSystemType = targetSystemType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage#
	 * addDistributedAutonomousAgent(org.jdom2.Document, org.jdom2.Element, boolean)
	 */
	@Override
	public Document addDistributedAutonomousAgent(final Document csfMessageExchangeDoc,
			final Element distributedAutononomousAgentElement,
			final boolean removeChildren) {
		return frameworkMessageDocumentHelper.addDistributedAutonomousAgent(
				csfMessageExchangeDoc, distributedAutononomousAgentElement,
				removeChildren);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage#
	 * getAgentModels(org.jdom2.Element)
	 */
	@Override
	public List<Element> getAgentModels(final Element distributedAutonomousAgentElement) {
		return frameworkMessageDocumentHelper
				.getAgentModels(distributedAutonomousAgentElement);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage#
	 * getDistributedAutonomousAgentElements(java.lang.Object)
	 */
	@Override
	public List<Element> getDistributedAutonomousAgentElements(final Object doc) {
		return frameworkMessageDocumentHelper.getDistributedAutonomousAgentElements(doc);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage#
	 * getDistributedAutonomousAgentID(org.jdom2.Element)
	 */
	@Override
	public String getDistributedAutonomousAgentID(
			final Element distributedAutononomousAgentElement) {
		return frameworkMessageDocumentHelper
				.getDistributedAutonomousAgentID(distributedAutononomousAgentElement);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage#
	 * getDocument()
	 */
@Override
public Document getDocument() {
		return document;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage#
	 * getFirstAgentModelActorAgentModelID(org.jdom2.Element)
	 */
	@Override
	public String getFirstAgentModelActorAgentModelID(final Element agentModel) {
		return frameworkMessageDocumentHelper
				.getFirstAgentModelActorAgentModelID(agentModel);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage#
	 * getFrameworkToDistributedSystemCommand()
	 */
	@Override
	public FRAMEWORK_COMMAND getFrameworkToDistributedSystemCommand() {
		return frameworkMessageDocumentHelper.getFrameworkToDistributedSystemCommand();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage#
	 * getFrameworkToSimulationEngineCommand()
	 */
	@Override
	public FRAMEWORK_COMMAND getFrameworkToSimulationEngineCommand() {
		return frameworkMessageDocumentHelper.getFrameworkToSimulationEngineCommand();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage#
	 * getNextAgentModelActor(java.lang.Object, org.jdom2.Element)
	 */
	@Override
	public Element getNextAgentModelActor(final Object distributedAutonomousAgent,
			final Element cachedAgentModelTemplate) {
		return frameworkMessageDocumentHelper.getNextAgentModelActor(
				distributedAutonomousAgent, cachedAgentModelTemplate);
	}

	// FIXME: Need to to rename these two getNext Function so it's obvious they are only
	// used for writing messages
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage#
	 * getNextDistributedAutonomousAgent(org.jdom2.Document, org.jdom2.Element)
	 */
	@Override
	public Element getNextDistributedAutonomousAgent(final Document doc,
			final Element cacheDistributedAutonomousAgentTemplate) {
		return frameworkMessageDocumentHelper.getNextDistributedAutonomousAgent(doc,
				cacheDistributedAutonomousAgentTemplate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage#
	 * getNextNonSelfSimulationDefinedLocationForActor(org.jdom2.Element,
	 * org.jdom2.Element)
	 */
	@Override
	public Element getNextNonSelfSimulationDefinedLocationForActor(final Element actor,
			final Element cachedLocationTemplate) {
		return frameworkMessageDocumentHelper
				.getNextNonSelfSimulationDefinedLocationForActor(actor,
						cachedLocationTemplate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage#
	 * getSelfLocation(org.jdom2.Element,
	 * org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage)
	 */
	@Override
	public List<String> getSelfLocationFromFirstAgentModel(
			final Element distributedAutononomousAgentElement, final FrameworkMessage msg) {
		return frameworkMessageDocumentHelper.getSelfLocationFromFirstAgentModel(
				distributedAutononomousAgentElement, msg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage#
	 * getSelfLocation
	 * (org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage)
	 */
	@Override
	public List<String> getSelfLocationFromNextDistributedAutonomousAgentNextAgentModelActor(
			final FrameworkMessage msg) {
		return frameworkMessageDocumentHelper
				.getSelfLocationFromNextDistributedAutonomousAgentNextAgentModelActor(msg);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage#
	 * getSimulationDefinedEnvironmentChangesElement(org.jdom2.Element)
	 */
	@Override
	public Element getSimulationDefinedEnvironmentChangesElement(final Element actor) {
		return frameworkMessageDocumentHelper
				.getSimulationDefinedEnvironmentChangesElement(actor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage#getStatus
	 * ()
	 */
	@Override
	public STATUS getStatus() {
		return frameworkMessageDocumentHelper.getStatus();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage#
	 * populateThisLocationInAgentModelActor(org.jdom2.Element, java.lang.String,
	 * java.lang.String, org.jdom2.Element)
	 */
	@Override
	public Element populateThisLocationInAgentModelActor(final Element actor,
			final String gridPointX, final String gridPointY,
			final Element cachedLocationTemplate) {
		return frameworkMessageDocumentHelper.populateThisActorLocationInAgentModel(
				actor, gridPointX, gridPointY, cachedLocationTemplate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage#
	 * removeDistributedAutonomousAgents(org.jdom2.Document)
	 */
	@Override
	public void removeDistributedAutonomousAgents(final Document csfMessageExchangeDoc) {
		frameworkMessageDocumentHelper
				.removeDistributedAutonomousAgents(csfMessageExchangeDoc);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage#
	 * setDistributedAutonomousAgentID(org.jdom2.Element, java.lang.String)
	 */
	@Override
	public Element setDistributedAutonomousAgentID(
			final Element distributedAutonomousAgent, final String ID) {
		return frameworkMessageDocumentHelper.setDistributedAutonomousAgentID(
				distributedAutonomousAgent, ID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage#
	 * setFrameworkToDistributedSystemCommand
	 * (org.simulationsystems.csf.common.csfmodel.messaging.messages.FRAMEWORK_COMMAND)
	 */
	@Override
	public void setFrameworkToDistributedSystemCommand(
			final FRAMEWORK_COMMAND frameworkToDistributedSystemCommand) {
		frameworkMessageDocumentHelper
				.setFrameworkToDistributedSystemCommand(frameworkToDistributedSystemCommand);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage#
	 * setFrameworkToSimulationEngineCommand
	 * (org.simulationsystems.csf.common.csfmodel.messaging.messages.FRAMEWORK_COMMAND)
	 */
	@Override
	public void setFrameworkToSimulationEngineCommand(
			final FRAMEWORK_COMMAND frameworkToSimulationEngineCommand) {
		frameworkMessageDocumentHelper
				.setFrameworkToSimulationEngineCommnad(frameworkToSimulationEngineCommand);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage#
	 * setIDForActorInAgentModel(org.jdom2.Element, java.lang.String)
	 */
	@Override
	public void setIDForActorInAgentModel(final Element actor, final String ID) {
		frameworkMessageDocumentHelper.setIDForActorInAgentModel(actor, ID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage#setStatus
	 * (org.simulationsystems.csf.common.csfmodel.messaging.messages.STATUS)
	 */
	@Override
	public void setStatus(final STATUS status) {
		frameworkMessageDocumentHelper.setStatus(status);
	}

	/*
	 * Returns the String representation of the message. For outgoing messages, this
	 * String representation will not always be the version to be sent to the common
	 * messaging interface. All transformations must be performed before this String
	 * representation reflects the version to be sent to the common messaging interface.
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage#
	 * toPrettyPrintedXMLString()
	 */
	@Override
	public String toPrettyPrintedXMLString() {
		return transformToCommonMessagingXMLString(true);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage#
	 * transformToCommonMessagingXMLString(boolean)
	 */
	@Override
	public String transformToCommonMessagingXMLString(final boolean prettyPrint) {
		// TODO: Mapping between agent IDs and UUIDs in this class?
		// Message all of the agents in each each target distributed system
		// for (UUID distributedSystemAgentUUID :
		// distributedSystem.getDistributedAgentUUIDs()) {
		// }

		// LOW: Drive the pretty print parameter from the configuration
		return frameworkMessageDocumentHelper.frameworkMessageToXMLString(document,
				prettyPrint);
	}
}
