package org.simulationsystems.simulationframework.simulation.api.framework.core.api;

import org.simulationsystems.simulationframework.simulation.api.messaging.FrameworkMessage;

/*
 * Represents the platform-independent interface to the Simulation-Toolkit side of the Common Simulation Framework.  The interface could use Redis or (in the future) web services.  This class uses the Bridge Pattern and acts as the refined abstraction, so the implementation for the type of interface to common messaging part of the framework is hidden from this class.
 */
public class SimulationDistributedAgentMessagingManager extends DistributedAgentMessaging {

	@Override
	public void initializeSimulationFrameworkCommonMessagingInterface() {

	}

	@Override
	public void sendMessageToDistributedAgents(FrameworkMessage frameworkMessage) {

	}

}
