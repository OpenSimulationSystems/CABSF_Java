package org.simulationsystems.csf.sim.internal.messaging.dao;

import org.simulationsystems.csf.common.internal.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.internal.systems.DistributedSystem;
import org.simulationsystems.csf.sim.api.SimulationRunContext;

public interface DistributedAgentDao {

	public void sendMessagesToDistributedAgents(SimulationRunContext simulationRunContext, FrameworkMessage frameworkMessage,
			DistributedSystem distributedSystem);
	
	public void initializeSimulationFrameworkCommonMessagingInterface(String messagingConnectionString);
}