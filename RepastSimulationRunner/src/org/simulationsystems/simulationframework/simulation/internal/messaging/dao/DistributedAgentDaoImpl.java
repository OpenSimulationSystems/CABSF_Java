package org.simulationsystems.simulationframework.simulation.internal.messaging.dao;

import java.util.UUID;

import org.simulationsystems.simulationframework.simulation.api.SimulationRunContext;
import org.simulationsystems.simulationframework.simulation.api.messaging.DistributedSystemAgentSet;
import org.simulationsystems.simulationframework.simulation.api.messaging.FrameworkMessage;

public class DistributedAgentDaoImpl implements DistributedAgentDao {
	static private DistributedAgentDaoImpl instance = new DistributedAgentDaoImpl();



	/*
	 * Disable this constructor so we can use a Singleton
	 */
	@SuppressWarnings("unused")
	private void DistributedAgentDAO() {
	}

	static public DistributedAgentDao getInstance() {
		return (DistributedAgentDao) instance;
	}

	@Override
	public void sendMessagesToDistributedAgents(FrameworkMessage frameworkMessage,
			DistributedSystemAgentSet distributedSystemAgentSet,
			SimulationRunContext simulationRunContext) {
		frameworkMessage
		
	}

}
