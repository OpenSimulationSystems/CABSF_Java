package org.simulationsystems.csf.sim.internal.messaging.bridge.abstraction;

import org.simulationsystems.csf.common.internal.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.internal.systems.DistributedSystem;
import org.simulationsystems.csf.sim.api.SimulationRunContext;
import org.simulationsystems.csf.sim.internal.messaging.bridge.implementation.CommonMessagingImplementationAPI;

/*
 * The abstract class in the Bridge pattern that specifies the Common Simulation Framework. This
 * class uses the Bridge Pattern so that clients can switch the messaging interface as needed. The
 * actual interface is Redis or (in the future) web services.
 */
public abstract class DistributedAgentMessagingAbstraction {
	protected CommonMessagingImplementationAPI commonMessagingImplementationAPI;

	abstract public void initializeSimulationFrameworkCommonMessagingInterface();

	abstract public void sendMessageToDistributedAgents(FrameworkMessage frameworkMessage,
			DistributedSystem distributedSystem, SimulationRunContext simulationRunContext);

	DistributedAgentMessagingAbstraction(CommonMessagingImplementationAPI commonMessagingImplementationAPI) {
		this.commonMessagingImplementationAPI = commonMessagingImplementationAPI;
	}
}
