package org.simulationsystems.csf.common.internal.messaging.bridge.abstraction;

import org.simulationsystems.csf.common.csfmodel.SYSTEM_TYPE;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FRAMEWORK_COMMAND;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.internal.messaging.bridge.implementation.CommonMessagingImplementationAPI;
import org.simulationsystems.csf.common.internal.systems.DistributedSystem;
import org.simulationsystems.csf.distsys.core.api.DistSysRunContext;
import org.simulationsystems.csf.sim.core.api.SimulationRunContext;

/*
 * The abstract class in the Bridge pattern that specifies the Common Simulation Framework. This
 * class uses the Bridge Pattern so that clients can switch the messaging interface as needed. The
 * actual interface is Redis or (in the future) web services.
 */
public abstract class CommonMessagingAbstraction {
	protected CommonMessagingImplementationAPI commonMessagingImplementationAPI;
	// LOW: Generalize this, maybe make it an object.
	protected String messagingConnectionString; //The messaging connection string, such as the Redis connection string.
	 //ID of the client of this class (simulation engine or distributed system).  Used for routing purposes.
	protected String commonMessagingClientId;
	
	abstract public void initializeSimulationFrameworkCommonMessagingInterface(String messagingConnectionString);

	abstract public void sendMessageToDistributedAgents(FrameworkMessage frameworkMessage,
			DistributedSystem distributedSystem, SimulationRunContext simulationRunContext);

	protected CommonMessagingAbstraction(CommonMessagingImplementationAPI commonMessagingImplementationAPI, String messagingConnectionString, String commonMessagingClientId) {
		this.commonMessagingImplementationAPI = commonMessagingImplementationAPI;
		this.messagingConnectionString = messagingConnectionString;
		this.commonMessagingClientId = commonMessagingClientId;
	}
	
	public void closeInterface() {
		commonMessagingImplementationAPI.closeInterface();
	}

	public FrameworkMessage readFrameworkMessageFromSimulationAdministrator(String clientID) {
		return commonMessagingImplementationAPI.readFrameworkMessageFromSimulationAdministrator(clientID);
		
	};
	
	public FrameworkMessage readFrameworkMessageFromDistributedSystem(String clientID) {
		return commonMessagingImplementationAPI.readFrameworkMessageFromDistributedSystem(clientID);
		
	};

	
	public FrameworkMessage listenForMessageFromSimulationEngine(SYSTEM_TYPE targetSystemType, String clientID) {
		return commonMessagingImplementationAPI.listenForMessageFromSimulationEngine(targetSystemType, clientID);
		
	}
	
	public FrameworkMessage requestEnvironmentInformation(
			SYSTEM_TYPE targetSystemType, String clientID) {
		return commonMessagingImplementationAPI.requestEnvironmentInformation(targetSystemType, clientID);
	}

	public abstract void sendMessageToSimulationEngine(FrameworkMessage frameworkMessage,
			DistSysRunContext distSysRunContext, String simulationEngineID);
	
	
}