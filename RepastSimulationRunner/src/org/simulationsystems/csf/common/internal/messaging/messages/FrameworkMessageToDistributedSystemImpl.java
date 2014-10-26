package org.simulationsystems.csf.common.internal.messaging.messages;

import java.util.UUID;

import org.simulationsystems.csf.common.internal.systems.DistributedSystem;

public class FrameworkMessageToDistributedSystemImpl implements FrameworkMessage {
	private FRAMEWORK_TO_DISTRIBUTEDSYSTEM_COMMAND frameworkToDistributedSystemCommand;

	public FrameworkMessageToDistributedSystemImpl() {
		super();
	}
	
	@Override
	public void setFrameworkToDistributedSystemCommand(
			FRAMEWORK_TO_DISTRIBUTEDSYSTEM_COMMAND frameworkToDistributedSystemCommand) {
		this.frameworkToDistributedSystemCommand = frameworkToDistributedSystemCommand;
	}

	public FRAMEWORK_TO_DISTRIBUTEDSYSTEM_COMMAND getDistributedAgentSetFlag() {
		return frameworkToDistributedSystemCommand;
	}

	public String transformToCommonMessagingXMLString(DistributedSystem distributedSystem) {
		// TODO: Mapping between agent IDs and UUIDs in this class?
		// Message all of the agents in each each target distributed system
		//for (UUID distributedSystemAgentUUID : distributedSystem.getDistributedAgentUUIDs()) {
		//}

		String message = "";
		// TODO: Convert this to XML message. 
		//Hard coding str[0]==1 to start simulation
		if (frameworkToDistributedSystemCommand == FRAMEWORK_TO_DISTRIBUTEDSYSTEM_COMMAND.SIMULATION_RUN_STARTED)
			message += message + "1";
		return message;
	}
}
