package org.simulationsystems.csf.distsys.adapters.jade.api;

import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;

/**
 * The Interface JadeControllerMock used for testing mock JADE agents.
 * 
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public interface JadeControllerMock {

	/**
	 * Receive message.
	 * 
	 * @param message
	 *            the message
	 * @param messageID
	 *            the message id
	 * @param inReplyToMessageID
	 *            the in reply to message id
	 */
	void receiveMessage(FrameworkMessage message, String messageID,
			String inReplyToMessageID);

}
