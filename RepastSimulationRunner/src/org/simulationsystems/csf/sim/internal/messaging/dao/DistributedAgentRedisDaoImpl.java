package org.simulationsystems.csf.sim.internal.messaging.dao;

import java.util.UUID;

import org.simulationsystems.csf.common.internal.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.internal.systems.DistributedSystem;
import org.simulationsystems.csf.sim.api.SimulationRunContext;

public class DistributedAgentRedisDaoImpl implements DistributedAgentDao {
	static private DistributedAgentRedisDaoImpl distributedAgentRedisDaoImpl = new DistributedAgentRedisDaoImpl();

	/*
	 * Disable this constructor so we can use a Singleton
	 */
	@SuppressWarnings("unused")
	private void DistributedAgentDAO() {
	}

	static public DistributedAgentDao getInstance() {
		return (DistributedAgentDao) distributedAgentRedisDaoImpl;
	}

	// LOW: Add functionality for handling multiple distributed agent systems
	// Call sendMessagesToDistributedAgents for a single distributed agent system from here.

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.simulationsystems.csf.sim.internal.messaging.dao.DistributedAgentDao
	 * #sendMessagesToDistributedAgents(org.simulationsystems.csf.sim.api.
	 * messaging.FrameworkMessage,
	 * org.simulationsystems.csf.sim.api.messaging.DistributedSystemAgentSet,
	 * org.simulationsystems.csf.sim.api.SimulationRunContext)
	 */
	@Override
	public void sendMessagesToDistributedAgents(FrameworkMessage frameworkMessage,
			DistributedSystem distributedSystem,
			SimulationRunContext simulationRunContext) {
		
		//TODO: Mapping between agent IDs and UUIDs in this class?
		//Message all of the agents in each each target distributed system
		for (UUID distributedSystemAgentUUID : distributedSystem.getDistributedAgentUUIDs()) {
			distributedAgentRedisDaoImpl.sendMessagesToDistributedAgents(frameworkMessage, distributedSystem, simulationRunContext);
		}

	}

}
