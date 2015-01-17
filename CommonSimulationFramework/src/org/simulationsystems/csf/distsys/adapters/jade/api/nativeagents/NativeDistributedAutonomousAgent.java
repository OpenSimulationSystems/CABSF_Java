package org.simulationsystems.csf.distsys.adapters.jade.api.nativeagents;

import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.distsys.adapters.jade.api.JadeControllerMock;

public interface NativeDistributedAutonomousAgent {

	/*
	 * Receives the portion of the Message Exchange XML that belongs to this agent
	 */
	public void receiveMessage(FrameworkMessage msg, String messageID,
			String inReplyToMessageID, JadeControllerMock jade_Controller_Agent);
	
	public String getDistributedAutonomousAgentID();
	
	public String getModelName();

	public String getDistributedAutonomousAgentModelID();
}
