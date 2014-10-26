package org.simulationsystems.csf.common.internal.messaging.messages;

public class FrameworkMessageToDistributedAgentsImpl implements FrameworkMessage {
	private FRAMEWORK_TO_DISTRIBUTEDAGENTS_MESSAGE_TYPE frameworkToDistributedAgentsType;

	public FrameworkMessageToDistributedAgentsImpl() {
		super();
	}

	public void setDistributedAgentSetFlag(
			FRAMEWORK_TO_DISTRIBUTEDAGENTS_MESSAGE_TYPE frameworkToDistributedAgentsType) {
		this.frameworkToDistributedAgentsType = frameworkToDistributedAgentsType;
	}

	public FRAMEWORK_TO_DISTRIBUTEDAGENTS_MESSAGE_TYPE getDistributedAgentSetFlag() {
		return frameworkToDistributedAgentsType;
	}
}
