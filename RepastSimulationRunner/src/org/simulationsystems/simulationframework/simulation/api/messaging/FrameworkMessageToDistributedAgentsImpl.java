package org.simulationsystems.simulationframework.simulation.api.messaging;

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
