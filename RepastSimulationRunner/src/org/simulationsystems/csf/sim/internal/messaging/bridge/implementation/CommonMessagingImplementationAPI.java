package org.simulationsystems.csf.sim.internal.messaging.bridge.implementation;

import org.simulationsystems.csf.common.internal.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.internal.systems.DistributedSystem;
import org.simulationsystems.csf.sim.api.SimulationRunContext;

/*
 * The implementor in the Bridge pattern, for the Common Simulation Framework's messaging
 */
public interface CommonMessagingImplementationAPI {
	public void readMessagesFromDistributedAgents(FrameworkMessage frameworkMessage);

	void sendMessagesToDistributedAgents(FrameworkMessage frameworkMessage,
			DistributedSystem distributedSystem, SimulationRunContext simulationRunContext);

}
