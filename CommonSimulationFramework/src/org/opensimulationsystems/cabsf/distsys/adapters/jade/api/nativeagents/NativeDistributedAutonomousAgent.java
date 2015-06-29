package org.opensimulationsystems.cabsf.distsys.adapters.jade.api.nativeagents;

import org.opensimulationsystems.cabsf.common.model.messaging.messages.FrameworkMessage;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.JadeControllerMock;

// TODO: Auto-generated Javadoc
/**
 * The Interface for representations of the native distributed autonomous agent (such as a
 * JADE agent).
 * 
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public interface NativeDistributedAutonomousAgent {
	/**
	 * Gets the distributed autonomous agent id.
	 * 
	 * @return the distributed autonomous agent id
	 */
	public String getDistributedAutonomousAgentID();

	/**
	 * Gets the distributed autonomous agent model id.
	 * 
	 * @return the distributed autonomous agent model id
	 */
	public String getDistributedAutonomousAgentModelID();

	/**
	 * Gets the model name.
	 * 
	 * @return the model name
	 */
	public String getModelName();

	/**
	 * Receives the portion of the Message Exchange XML that belongs to this agent
	 * 
	 * @param msg
	 *            the msg
	 * @param messageID
	 *            the message id
	 * @param inReplyToMessageID
	 *            the in reply to message id
	 * @param jade_Controller_Agent
	 *            the jade_ controller_ agent
	 */
	public void receiveMessage(FrameworkMessage msg, String messageID,
			String inReplyToMessageID, JadeControllerMock jade_Controller_Agent);
}
