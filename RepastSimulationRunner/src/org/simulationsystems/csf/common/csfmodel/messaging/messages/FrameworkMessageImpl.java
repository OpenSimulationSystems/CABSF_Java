package org.simulationsystems.csf.common.csfmodel.messaging.messages;

import java.io.IOException;
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
	public Element processActorForAgentModel(Element actor, String ID, String gridPointX,
			String gridPointY) {
		return frameworkMessageDocumentHelper.processActorForAgentModel(actor, ID,
				gridPointX, gridPointY);
	}

	@Override
	public Element populatePointWithLeastZombies(Element agentModelActor,
			String GridPointX, String GridPointY, Element cachedLocationTemplate) {
		return frameworkMessageDocumentHelper.populatePointWithLeastZombies(
				agentModelActor, GridPointX, GridPointY, cachedLocationTemplate);
	}

	@Override
	public Element getNextAgentModelActor(Object doc, Element cachedAgentModelTemplate) {
		return frameworkMessageDocumentHelper.getNextAgentModelActor(doc,
				cachedAgentModelTemplate);
	}

}
