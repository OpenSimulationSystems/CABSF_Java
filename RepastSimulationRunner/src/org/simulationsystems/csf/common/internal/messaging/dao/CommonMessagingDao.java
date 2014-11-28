package org.simulationsystems.csf.common.internal.messaging.dao;

import org.simulationsystems.csf.common.csfmodel.FRAMEWORK_COMMAND;
import org.simulationsystems.csf.common.csfmodel.SYSTEM_TYPE;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.internal.systems.DistributedSystem;
import org.simulationsystems.csf.sim.api.SimulationRunContext;

public interface CommonMessagingDao {
	
	public void sendMessagesToDistributedAgents(SimulationRunContext simulationRunContext, FrameworkMessage frameworkMessage,
			DistributedSystem distributedSystem);
	
	public void initializeSimulationFrameworkCommonMessagingInterface(String messagingConnectionString);

	public void closeInterface();

	public FrameworkMessage readFrameworkMessageFromSimulationAdministrator(String clientID);

	public FrameworkMessage readFrameworkMessageFromDistributedSystem(String clientID);

	public FrameworkMessage requestEnvironmentInformation(
			SYSTEM_TYPE targetSystemType, String clientID);

	FrameworkMessage listenForMessageFromSimulationEngine(SYSTEM_TYPE targetSystemType, String clientID);
}