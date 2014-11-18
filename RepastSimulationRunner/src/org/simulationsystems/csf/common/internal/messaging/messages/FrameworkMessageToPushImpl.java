package org.simulationsystems.csf.common.internal.messaging.messages;

import java.io.IOException;
import java.util.UUID;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.simulationsystems.csf.common.csfmodel.FRAMEWORK_COMMAND;
import org.simulationsystems.csf.common.csfmodel.CsfSimulationCheckedException;
import org.simulationsystems.csf.common.csfmodel.SYSTEM_TYPE;
import org.simulationsystems.csf.common.internal.messaging.MessagingUtilities;
import org.simulationsystems.csf.common.internal.messaging.xml.transformers.FrameworkMessageToXMLTransformer;
import org.simulationsystems.csf.common.internal.systems.DistributedSystem;

public class FrameworkMessageToPushImpl implements FrameworkMessage {
	private FRAMEWORK_TO_DISTRIBUTEDSYSTEM_COMMAND frameworkToDistributedSystemCommand;
	private FrameworkMessageToXMLTransformer frameworkMessageTOXMLTransformer = new FrameworkMessageToXMLTransformer();
	private Document document;
	private SYSTEM_TYPE sourceSystemType;
	private SYSTEM_TYPE targetSystemType;
	private FRAMEWORK_COMMAND frameworkCommand;
	
	//FIXME: Move things to abstract class/share fields?
	private FRAMEWORK_COMMAND command;
	
	@SuppressWarnings("unused")
	private FrameworkMessageToPushImpl() {
		
	}
	
	public FrameworkMessageToPushImpl(SYSTEM_TYPE sourceSystemType, SYSTEM_TYPE targetSystemType, Document csfMessageExchangeTemplateDocument) {
		super();
		this.document = csfMessageExchangeTemplateDocument;
		this.sourceSystemType = sourceSystemType;
		this.targetSystemType = targetSystemType;
	}
	
	@Override
	public void setFrameworkToDistributedSystemCommand(
			FRAMEWORK_TO_DISTRIBUTEDSYSTEM_COMMAND frameworkToDistributedSystemCommand) {
		this.frameworkToDistributedSystemCommand = frameworkToDistributedSystemCommand;
	}

	@Override
	public FRAMEWORK_TO_DISTRIBUTEDSYSTEM_COMMAND getFrameworkToDistributedSystemCommand() {
		return frameworkToDistributedSystemCommand;
	}

	@Override
	public String transformToCommonMessagingXMLString(DistributedSystem distributedSystem) {
		// TODO: Mapping between agent IDs and UUIDs in this class?
		// Message all of the agents in each each target distributed system
		//for (UUID distributedSystemAgentUUID : distributedSystem.getDistributedAgentUUIDs()) {
		//}

		return frameworkMessageTOXMLTransformer.frameworkMessageToXMLString(this, document);

	}
	

	@Override
	public FRAMEWORK_COMMAND getFrameworkCommand() {
		return frameworkCommand;
	}
}
