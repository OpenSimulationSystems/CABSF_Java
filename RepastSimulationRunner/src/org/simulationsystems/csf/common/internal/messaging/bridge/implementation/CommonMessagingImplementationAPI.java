package org.simulationsystems.csf.common.internal.messaging.bridge.implementation;

import org.simulationsystems.csf.common.csfmodel.FRAMEWORK_COMMAND;
import org.simulationsystems.csf.common.csfmodel.SYSTEM_TYPE;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.internal.systems.DistributedSystem;
import org.simulationsystems.csf.sim.api.SimulationRunContext;

/*
 * The implementor in the Bridge pattern, for the Common Simulation Framework's messaging
 */
public interface CommonMessagingImplementationAPI {
	public void readMessagesFromDistributedAgents(FrameworkMessage frameworkMessage);

	void sendMessagesToDistributedAgents(FrameworkMessage frameworkMessage,
			DistributedSystem distributedSystem, SimulationRunContext simulationRunContext);

	public void initializeSimulationFrameworkCommonMessagingInterface(String messagingConnectionString);

	public void closeInterface();

	public void listenForCommandsFromSimulationAdministrator(String clientID);

	FRAMEWORK_COMMAND listenForCommandsFromSimulationEngine(SYSTEM_TYPE targetSystemType, String clientID);

	void listenForCommandsFromDistributedSystem(String clientID);
}
