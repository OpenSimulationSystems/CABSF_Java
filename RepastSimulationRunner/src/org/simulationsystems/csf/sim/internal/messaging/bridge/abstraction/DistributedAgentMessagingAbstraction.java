package org.simulationsystems.csf.sim.internal.messaging.bridge.abstraction;

import org.simulationsystems.csf.common.internal.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.sim.internal.messaging.bridge.implementation.CommonMessagingAPI;

/*
 * The abstract class in the Bridge pattern that specifies the Common Simulation Framework.  This class uses the Bridge Pattern so that clients can switch the messaging interface as needed.  The actual interface is Redis or (in the future) web services.
 */
public abstract class DistributedAgentMessagingAbstraction {
	protected CommonMessagingAPI commonMessagingAPI;
	
	abstract public void initializeSimulationFrameworkCommonMessagingInterface();
	abstract public void sendMessageToDistributedAgents(FrameworkMessage frameworkMessage);

	DistributedAgentMessagingAbstraction(CommonMessagingAPI commonMessagingAPI) {
		this.commonMessagingAPI = commonMessagingAPI;
	}
}
