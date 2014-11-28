package org.simulationsystems.csf.common.internal.messaging.bridge.implementation;

import org.simulationsystems.csf.common.csfmodel.FRAMEWORK_COMMAND;
import org.simulationsystems.csf.common.csfmodel.SYSTEM_TYPE;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.internal.systems.DistributedSystem;
import org.simulationsystems.csf.distsys.api.DistSysRunContext;
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

	public FrameworkMessage readFrameworkMessageFromSimulationAdministrator(String clientID);

	public FrameworkMessage readFrameworkMessageFromDistributedSystem(String clientID);

	public FrameworkMessage requestEnvironmentInformation(
			SYSTEM_TYPE targetSystemType, String clientID);

	FrameworkMessage listenForMessageFromSimulationEngine(
			SYSTEM_TYPE targetSystemType, String clientID);

	public void sendMessageToSimulationEngine(FrameworkMessage frameworkMessage,
			DistSysRunContext distSysRunContext, String simulationEngineID);

}
