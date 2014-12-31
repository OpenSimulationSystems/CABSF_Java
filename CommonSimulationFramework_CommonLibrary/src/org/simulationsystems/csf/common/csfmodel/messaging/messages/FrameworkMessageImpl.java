package org.simulationsystems.csf.common.csfmodel.messaging.messages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.simulationsystems.csf.common.csfmodel.SYSTEM_TYPE;
import org.simulationsystems.csf.common.csfmodel.csfexceptions.CsfCheckedException;
import org.simulationsystems.csf.common.internal.messaging.MessagingUtilities;
import org.simulationsystems.csf.common.internal.messaging.xml.XMLUtilities;
import org.simulationsystems.csf.common.internal.messaging.xml.transformers.FrameworkMessageDocumentHelper;

public class FrameworkMessageImpl implements FrameworkMessage {
	private FrameworkMessageDocumentHelper frameworkMessageDocumentHelper = new FrameworkMessageDocumentHelper(
			this);
	private Document document;
	private SYSTEM_TYPE sourceSystemType;
	private SYSTEM_TYPE targetSystemType;

	@Override
	public FRAMEWORK_COMMAND getFrameworkToSimulationEngineCommand() {
		return frameworkMessageDocumentHelper.getFrameworkToSimulationEngineCommand();
	}

	@Override
	public void setFrameworkToSimulationEngineCommand(
			FRAMEWORK_COMMAND frameworkToSimulationEngineCommand) {
		frameworkMessageDocumentHelper
				.setFrameworkToSimulationEngineCommnad(frameworkToSimulationEngineCommand);
	}

	@Override
	public STATUS getStatus() {
		return frameworkMessageDocumentHelper.getStatus();
	}

	@Override
	public Document getDocument() {
		return document;
	}

	@SuppressWarnings("unused")
	private FrameworkMessageImpl() {

	}

	/*
	 * Used for incoming messages. Pass in the XML String from the framework
	 */
	public FrameworkMessageImpl(SYSTEM_TYPE sourceSystemType,
			SYSTEM_TYPE targetSystemType, String xmlString) throws CsfCheckedException {
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
	 * Used for outgoing messages. Pass in the framework message Document template
	 */
	public FrameworkMessageImpl(SYSTEM_TYPE sourceSystemType,
			SYSTEM_TYPE targetSystemType, Document document) {

		this.document = document;

		this.sourceSystemType = sourceSystemType;
		this.targetSystemType = targetSystemType;
	}

	/*
	 * Returns the String representation of the message. For outgoing messages, this
	 * String representation will not always be the version to be sent to the common
	 * messaging interface. All transformations must be performed before this String
	 * representation reflects the version to be sent to the common messaging interface.
	 */
	@Override
	public String toPrettyPrintedXMLString() {
		return transformToCommonMessagingXMLString(true);

	}

	@Override
	public void setFrameworkToDistributedSystemCommand(
			FRAMEWORK_COMMAND frameworkToDistributedSystemCommand) {
		frameworkMessageDocumentHelper
				.setFrameworkToDistributedSystemCommand(frameworkToDistributedSystemCommand);
	}

	@Override
	public FRAMEWORK_COMMAND getFrameworkToDistributedSystemCommand() {
		return frameworkMessageDocumentHelper.getFrameworkToDistributedSystemCommand();
	}

	@Override
	public String transformToCommonMessagingXMLString(boolean prettyPrint) {
		// TODO: Mapping between agent IDs and UUIDs in this class?
		// Message all of the agents in each each target distributed system
		// for (UUID distributedSystemAgentUUID :
		// distributedSystem.getDistributedAgentUUIDs()) {
		// }

		// LOW: Drive the pretty print parameter from the configuration
		return frameworkMessageDocumentHelper.frameworkMessageToXMLString(document,
				prettyPrint);
	}

	@Override
	public void setStatus(STATUS status) {
		frameworkMessageDocumentHelper.setStatus(status);
	}

	@Override
	public Element populateThisLocationInAgentModelActor(Element actor, String ID,
			String gridPointX, String gridPointY, Element cachedLocationTemplate) {
		return frameworkMessageDocumentHelper.populateThisActorLocationInAgentModel(
				actor, ID, gridPointX, gridPointY,cachedLocationTemplate);
	}

	@Override
	public Element getNextAgentModelActor(Object distributedAutononomousAgent,
			Element cachedAgentModelTemplate) {
		return frameworkMessageDocumentHelper.getNextAgentModelActor(
				distributedAutononomousAgent, cachedAgentModelTemplate);
	}

	@Override
	public Element getNextNonSelfLocationForActor(Element actor,
			Element cachedLocationTemplate) {
		return frameworkMessageDocumentHelper.getNextNonSelfLocationForActor(actor,
				cachedLocationTemplate);
	}

	@Override
	public Element setDistributedAutonomousAgent(Element distributedAutonomousAgent,
			String ID) {
		return frameworkMessageDocumentHelper.populateDistributedAutonomousAgent(
				distributedAutonomousAgent, ID);
	}

	// FIXME: Need to to rename these two getNext Function so it's obvious they are only
	// used for writing messages
	@Override
	public Element getNextDistributedAutonomousAgent(Object csfMessageExchangeDoc,
			Element cacheDistributedAutonomousAgentTemplate) {
		return frameworkMessageDocumentHelper.getNextDistributedAutonomousAgent(csfMessageExchangeDoc,
				cacheDistributedAutonomousAgentTemplate);
	}
	
	@Override
	public List<Element> getDistributedAutonomousAgentElements(Object doc) {
		return frameworkMessageDocumentHelper.getDistributedAutonomousAgentElements(doc);
	}

	@Override
	public String getDistributedAutonomousAgentElementID(Element distributedAutononomousAgentElement) {
		return frameworkMessageDocumentHelper.getDistributedAutonomousAgentElementID(distributedAutononomousAgentElement);
	}

	@Override
	public List<Element> getAgentModels(Element distributedAutonomousAgentElement) {
		return frameworkMessageDocumentHelper.getAgentModels(distributedAutonomousAgentElement);
	}

	@Override
	public String getAgentModelID(Element agentModel) {
		return frameworkMessageDocumentHelper.getAgentModelID(agentModel);
	}

	@Override
	/*
	 * Side effect includes removing the distributedAutononomousAgentElement from the current Document
	 */
	public Document addDistributedAutonomousAgent(Document csfMessageExchangeDoc, Element distributedAutononomousAgentElement, boolean removeChildren) {
		return frameworkMessageDocumentHelper.addDistributedAutonomousAgent(csfMessageExchangeDoc,
				distributedAutononomousAgentElement, removeChildren);
	}

	@Override
	public void removeDistributedAutonomousAgents(Document csfMessageExchangeDoc) {
		frameworkMessageDocumentHelper.removeDistributedAutonomousAgents(csfMessageExchangeDoc);
		
	}
	
	@Override
	public List<String> getSelfLocation(Element distributedAutononomousAgentElement,
			FrameworkMessage msg) {
		return frameworkMessageDocumentHelper.getSelfLocation(distributedAutononomousAgentElement, msg);
	}

}
