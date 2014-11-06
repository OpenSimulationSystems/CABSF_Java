package org.simulationsystems.csf.common.internal.messaging.bridge.abstraction;

import org.simulationsystems.csf.common.internal.messaging.bridge.implementation.CommonMessagingImplementationAPI;
import org.simulationsystems.csf.common.internal.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.internal.systems.DistributedSystem;
import org.simulationsystems.csf.sim.api.SimulationRunContext;

/*
 * The abstract class in the Bridge pattern that specifies the Common Simulation Framework. This
 * class uses the Bridge Pattern so that clients can switch the messaging interface as needed. The
 * actual interface is Redis or (in the future) web services.
 */
public abstract class CommonMessagingAbstraction {
	protected CommonMessagingImplementationAPI commonMessagingImplementationAPI;
	// LOW: Generalize this, maybe make it an object.
	protected String messagingConnectionString; //The messaging connection string, such as the Redis connection string.

	abstract public void initializeSimulationFrameworkCommonMessagingInterface(String messagingConnectionString);

	abstract public void sendMessageToDistributedAgents(FrameworkMessage frameworkMessage,
			DistributedSystem distributedSystem, SimulationRunContext simulationRunContext);

	protected CommonMessagingAbstraction(CommonMessagingImplementationAPI commonMessagingImplementationAPI, String messagingConnectionString) {
		this.commonMessagingImplementationAPI = commonMessagingImplementationAPI;
		this.messagingConnectionString = messagingConnectionString;
	}
}
