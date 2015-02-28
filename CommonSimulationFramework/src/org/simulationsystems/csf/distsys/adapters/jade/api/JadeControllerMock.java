package org.simulationsystems.csf.distsys.adapters.jade.api;

import java.util.List;

import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;

public interface JadeControllerMock {

	void receiveMessage(FrameworkMessage message, String messageID,
			String inReplyToMessageID);

}