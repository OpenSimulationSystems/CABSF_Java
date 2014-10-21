package org.simulationsystems.simulationframework.simulation.internal.messaging.dao;

import org.simulationsystems.simulationframework.simulation.api.SimulationRunContext;
import org.simulationsystems.simulationframework.simulation.api.messaging.DistributedSystemAgentSet;
import org.simulationsystems.simulationframework.simulation.api.messaging.FrameworkMessage;

public interface DistributedAgentDao {

	public void sendMessagesToDistributedAgents(FrameworkMessage frameworkMessage,
			DistributedSystemAgentSet distributedSystemAgentSet,
			SimulationRunContext simulationRunContext);

}