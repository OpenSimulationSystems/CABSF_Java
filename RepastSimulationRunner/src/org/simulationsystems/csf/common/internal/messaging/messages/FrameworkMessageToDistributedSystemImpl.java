package org.simulationsystems.csf.common.internal.messaging.messages;

import java.util.UUID;

import org.jdom2.Document;
import org.simulationsystems.csf.common.internal.messaging.xml.transformers.FrameworkMessageToXMLTransformer;
import org.simulationsystems.csf.common.internal.systems.DistributedSystem;

public class FrameworkMessageToDistributedSystemImpl implements FrameworkMessage {
	private FRAMEWORK_TO_DISTRIBUTEDSYSTEM_COMMAND frameworkToDistributedSystemCommand;
	private FrameworkMessageToXMLTransformer frameworkMessageTOXMLTransformer = new FrameworkMessageToXMLTransformer();
	private Document csfMessageExchangeTemplateDocument;
	
	@SuppressWarnings("unused")
	private FrameworkMessageToDistributedSystemImpl() {
		
	}
	
	public FrameworkMessageToDistributedSystemImpl(Document csfMessageExchangeTemplateDocument) {
		super();
		this.csfMessageExchangeTemplateDocument = csfMessageExchangeTemplateDocument;		
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

		return frameworkMessageTOXMLTransformer.frameworkMessageToXMLString(this, csfMessageExchangeTemplateDocument);

	}
}
