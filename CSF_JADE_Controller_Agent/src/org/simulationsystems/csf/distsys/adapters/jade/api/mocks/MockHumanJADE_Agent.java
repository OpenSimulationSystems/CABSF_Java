package org.simulationsystems.csf.distsys.adapters.jade.api.mocks;

import org.jdom2.Document;
import org.simulationsystems.csf.common.csfmodel.SYSTEM_TYPE;
import org.simulationsystems.csf.common.csfmodel.csfexceptions.CsfCheckedException;
import org.simulationsystems.csf.common.csfmodel.csfexceptions.CsfMessagingRuntimeException;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessageImpl;
import org.simulationsystems.csf.distsys.adapters.jade.api.JADE_MAS_AgentContext;

public class MockHumanJADE_Agent {
	private String ID;
	//private JZombies_Csf jzombies_Csf = new JZombies_Csf();
	
	// This is not the same instance as the context running in the controller agent due to
	// the fact that these JADE agents are all autononmous. The important here is to
	// provide access to the API to understand the messages.
	JADE_MAS_AgentContext jade_MAS_AgentContext = new JADE_MAS_AgentContext();
	
	/*
	 * Receives the portion of the Message Exchange XML that belongs to this agent
	 */
	public void receiveMessage(Document distributedAutonomusAgentDocument) {
		//converts the distributed autonomous agent document back to the full message exchange document that then gets converted into a FrameworkMessage
		//All of the other information not meant for this distributed autonomous agent is not present either in the original or converted XML
		FrameworkMessage fm = jade_MAS_AgentContext.convertDocumentToDistributedAutonomousAgentToFrameworkMessage(distributedAutonomusAgentDocument,ID);
		
		
	}
}
