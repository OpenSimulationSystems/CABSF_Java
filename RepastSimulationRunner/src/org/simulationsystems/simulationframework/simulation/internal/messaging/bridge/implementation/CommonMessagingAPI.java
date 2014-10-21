package org.simulationsystems.simulationframework.simulation.internal.messaging.bridge.implementation;

import org.simulationsystems.simulationframework.simulation.api.messaging.FrameworkMessage;

/*
 * The implementor in the Bridge pattern, for the Common Simulation Framework's messaging
 */
public interface CommonMessagingAPI {
	public void sendMessagesToDistributedAgents(FrameworkMessage frameworkMessage);
	public void readMessagesFromDistributedAgents(FrameworkMessage frameworkMessage);

}
