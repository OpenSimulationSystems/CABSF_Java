package org.simulationsystems.csf.common.csfmodel.messaging.messages;

import java.io.IOException;
import java.util.UUID;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.simulationsystems.csf.common.csfmodel.FRAMEWORK_COMMAND;
import org.simulationsystems.csf.common.csfmodel.SYSTEM_TYPE;
import org.simulationsystems.csf.common.csfmodel.csfexceptions.CsfCheckedException;
import org.simulationsystems.csf.common.internal.messaging.MessagingUtilities;
import org.simulationsystems.csf.common.internal.messaging.xml.transformers.FrameworkMessageToXMLTransformer;

public class FrameworkMessageImpl implements FrameworkMessage {
	private FrameworkMessageToXMLTransformer frameworkMessageTOXMLTransformer = new FrameworkMessageToXMLTransformer(
			this);
	private Document document;
	private SYSTEM_TYPE sourceSystemType;
	private SYSTEM_TYPE targetSystemType;
	
	@Override
	public FRAMEWORK_COMMAND getFrameworkToSimulationEngineCommand() {
		return frameworkMessageTOXMLTransformer.getFrameworkToSimulationEngineCommand();
	}
	
	@Override
	public void setFrameworkToSimulationEngineCommand(
			FRAMEWORK_COMMAND frameworkToSimulationEngineCommand) {
		frameworkMessageTOXMLTransformer.setFrameworkToSimulationEngineCommnad(frameworkToSimulationEngineCommand);
	}

	@Override
	public STATUS getStatus() {
		return frameworkMessageTOXMLTransformer.getStatus();
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
			SYSTEM_TYPE targetSystemType, String xmlString)
			throws CsfCheckedException {
		super();
		try {
			this.document = MessagingUtilities
					.createDocumentFromString(xmlString);
		} catch (JDOMException | IOException e) {
			// TODO: Where to catch this?
			throw new CsfCheckedException("Unable to parse the message XML", e);
		}

		this.sourceSystemType = sourceSystemType;
		this.targetSystemType = targetSystemType;
	}

	/*
	 * Used for outgoing messages. Pass in the framework message Document
	 * template
	 */
	public FrameworkMessageImpl(SYSTEM_TYPE sourceSystemType,
			SYSTEM_TYPE targetSystemType, Document document
		) {

			this.document = document;

		this.sourceSystemType = sourceSystemType;
		this.targetSystemType = targetSystemType;
	}

	/*
	 * Returns the String representation of the message. For outgoing messages,
	 * this String representation will not always be the version to be sent to
	 * the common messaging interface. All transformations must be performed
	 * before this String representation reflects the version to be sent to the
	 * common messaging interface.
	 */
	@Override
	public String toPrettyPrintedXMLString() {
		return transformToCommonMessagingXMLString(true);

	}

	@Override
	public void setFrameworkToDistributedSystemCommand(
			FRAMEWORK_COMMAND frameworkToDistributedSystemCommand) {
		frameworkMessageTOXMLTransformer
				.setFrameworkToDistributedSystemCommand(frameworkToDistributedSystemCommand);
	}
	
	@Override
	public FRAMEWORK_COMMAND getFrameworkToDistributedSystemCommand() {
		return frameworkMessageTOXMLTransformer.getFrameworkToDistributedSystemCommand();
	}


	@Override
	public String transformToCommonMessagingXMLString(boolean prettyPrint) {
		// TODO: Mapping between agent IDs and UUIDs in this class?
		// Message all of the agents in each each target distributed system
		// for (UUID distributedSystemAgentUUID :
		// distributedSystem.getDistributedAgentUUIDs()) {
		// }

		// LOW: Drive the pretty print parameter from the configuration
		return frameworkMessageTOXMLTransformer.frameworkMessageToXMLString(
				document, prettyPrint);
	}

	@Override
	public void setStatus(STATUS status) {
		frameworkMessageTOXMLTransformer
		.setStatus(status);
	}

}
