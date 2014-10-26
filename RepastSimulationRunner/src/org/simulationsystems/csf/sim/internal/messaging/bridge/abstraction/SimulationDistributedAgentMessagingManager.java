package org.simulationsystems.csf.sim.internal.messaging.bridge.abstraction;

import org.simulationsystems.csf.common.internal.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.sim.internal.messaging.bridge.implementation.CommonMessagingAPI;

/*
 * The refined abstraction class in  the Bridge Pattern to represent the platform-independent interface to the Simulation-Toolkit side of the Common Simulation Framework.  The interface could use Redis or (in the future) web services.  This class uses the Bridge Pattern and acts as the refined abstraction, so the implementation for the type of interface to common messaging part of the framework is hidden from this class.
 */
public class SimulationDistributedAgentMessagingManager extends DistributedAgentMessagingAbstraction {

	SimulationDistributedAgentMessagingManager(
			CommonMessagingAPI commonMessagingAPI) {
		super(commonMessagingAPI);
	}

	@Override
	public void initializeSimulationFrameworkCommonMessagingInterface() {

	}

	@Override
	public void sendMessageToDistributedAgents(FrameworkMessage frameworkMessage) {
		// commonMessagingAPI set in the RunGroup initialization using reflection, as
		// specified in the common framework configuration.
		
		//FIXME
		//commonMessagingAPI.sendMessagesToDistributedAgents(frameworkMessage);
	}

}
