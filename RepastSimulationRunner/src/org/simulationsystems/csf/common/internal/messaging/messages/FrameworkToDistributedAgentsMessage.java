package org.simulationsystems.csf.common.internal.messaging.messages;


public class FrameworkToDistributedAgentsMessage implements FrameworkMessage {
	FRAMEWORK_TO_DISTRIBUTEDAGENTS_MESSAGE_TYPE messageType;

	public FrameworkToDistributedAgentsMessage(FRAMEWORK_TO_DISTRIBUTEDAGENTS_MESSAGE_TYPE messageType) {
		this.messageType = messageType;

	}

}
