package org.simulationsystems.simulationframework.simulation.api.framework.core.api;

import org.simulationsystems.simulationframework.simulation.api.framework.core.api.api.commonmessaging.CommonMessagingInterfaceAPI;

/*
 * The abstract class specifying the Common Simulation Framework.  This class uses the Bridge Pattern so that clients can switch the messaging interface as needed.  The actual interface is Redis or (in the future) web services.
 */
public abstract class DistributedAgentMessaging {
	private CommonMessagingInterfaceAPI commonMessagingInterfaceAPI;
	
	abstract public void initializeSimulationFrameworkCommonMessagingInterface();


}
