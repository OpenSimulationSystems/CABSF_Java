package org.simulationsystems.simulationframework.simulation.api.messaging;

public class FrameworkToDistributedAgentsMessage implements FrameworkMessage {
	FRAMEWORK_TO_DISTRIBUTEDAGENTS_MESSAGE_TYPE messageType;

	public FrameworkToDistributedAgentsMessage(FRAMEWORK_TO_DISTRIBUTEDAGENTS_MESSAGE_TYPE messageType) {
		this.messageType = messageType;

	}

}
