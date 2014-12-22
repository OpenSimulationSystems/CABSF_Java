package org.simulationsystems.csf.distsys.adapters.jade.api.mocks;

import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.simulationsystems.csf.common.csfmodel.SYSTEM_TYPE;
import org.simulationsystems.csf.common.csfmodel.csfexceptions.CsfCheckedException;
import org.simulationsystems.csf.common.csfmodel.csfexceptions.CsfMessagingRuntimeException;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessageImpl;
import org.simulationsystems.csf.common.internal.messaging.xml.XMLUtilities;
import org.simulationsystems.csf.distsys.adapters.jade.api.JADE_MAS_AgentContext;

public class MockHumanJADE_Agent {

	// This is not the same instance as the context running in the controller agent due to
	// the fact that these JADE agents are all autononmous. The important here is to
	// provide access to the API to understand the messages.
	JADE_MAS_AgentContext jade_MAS_AgentContext = new JADE_MAS_AgentContext();

	private String ID;
	private JZombies_JADE_Csf jzombies_JADE_Csf = new JZombies_JADE_Csf(
			jade_MAS_AgentContext);

	/*
	 * Receives the portion of the Message Exchange XML that belongs to this agent
	 */
	public void receiveMessage(Document distributedAutononomousAgent) {
		// converts the distributed autonomous agent document back to the full message
		// exchange document that then gets converted into a FrameworkMessage
		// All of the other information not meant for this distributed autonomous agent is
		// not present either in the original or converted XML
		System.out.println("[MockHumanJADE_Agent "
				+ ID
				+ "] Received message: "
				+ XMLUtilities.convertDocumentToXMLString(
						distributedAutononomousAgent.getRootElement(), true));

		jade_MAS_AgentContext.initializeCsfAgent("TESTconfigFile", false);

		// convertDocumentSentToDistributedAutonomousAgentToFrameworkMessage removes the root element from the distributedAutononomousAgent Documenbt and so
		// we need to hold on to the element before calling the method.  The old document will end up blank and a new Document will exist in the FrameworkMessage
		Element distributedAutononomousAgentElement = distributedAutononomousAgent.getRootElement();
		FrameworkMessage fm = jade_MAS_AgentContext
				.convertDocumentSentToDistributedAutonomousAgentToFrameworkMessage(
						distributedAutononomousAgent, ID);

		List<String> pointWithLeaseZombiesPoint = jzombies_JADE_Csf
				.getPointWithLeastZombies(distributedAutononomousAgentElement,
						fm, jade_MAS_AgentContext);

		for (int i = 0; i < pointWithLeaseZombiesPoint.size(); i++) {
			System.out.println("[MockHumanJADE_Agent" + ID
					+ "] Received Zombie location " + String.valueOf(i) + " : "
					+ String.valueOf(pointWithLeaseZombiesPoint.get(i)));
		}

XMLUtilities.convertElementToXMLString(fm.getDocument().getRootElement(),true);
		List<String> selfPoint = jzombies_JADE_Csf.getSelfLocation(
				distributedAutononomousAgentElement, fm, jade_MAS_AgentContext);
		for (int i = 0; i < selfPoint.size(); i++) {
			System.out.println("[MockHumanJADE_Agent ID " + ID + "] Self Location: "
					+ String.valueOf(i) + " : " + String.valueOf(selfPoint.get(i)));
		}
	}
}
