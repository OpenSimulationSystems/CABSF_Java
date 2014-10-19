package org.simulationsystems.simulationframework.simulation.api.framework.core.api.api.commonmessaging;

import org.simulationsystems.simulationframework.simulation.api.messaging.FrameworkMessage;

public interface CommonMessagingInterfaceAPI {
	public void sendMessageToDistributedAgents(FrameworkMessage frameworkMessage);
	
}
