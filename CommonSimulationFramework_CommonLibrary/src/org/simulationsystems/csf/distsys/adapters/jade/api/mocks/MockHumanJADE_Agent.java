package org.simulationsystems.csf.distsys.adapters.jade.api.mocks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jzombies.JZombies_Csf;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.simulationsystems.csf.common.csfmodel.SYSTEM_TYPE;
import org.simulationsystems.csf.common.csfmodel.csfexceptions.CsfCheckedException;
import org.simulationsystems.csf.common.csfmodel.csfexceptions.CsfMessagingRuntimeException;
import org.simulationsystems.csf.common.csfmodel.csfexceptions.CsfRuntimeException;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessageImpl;
import org.simulationsystems.csf.common.internal.messaging.xml.XMLUtilities;
import org.simulationsystems.csf.distsys.adapters.jade.api.JADE_MAS_AgentContext;
import org.simulationsystems.csf.distsys.adapters.jade.api.JadeController;

public class MockHumanJADE_Agent {

	private String distributedAutonomousAgentID;
	private String distAutAgentModelID;

	// This is not the same instance as the context running in the controller agent due to
	// the fact that these JADE agents are all autononmous. The important here is to
	// provide access to the API to understand the messages.
	JADE_MAS_AgentContext jade_MAS_AgentContext = new JADE_MAS_AgentContext();
	private JZombies_Csf jzombies_CSF = new JZombies_Csf(jade_MAS_AgentContext);

	public MockHumanJADE_Agent(String distributedAutonomousAgentID,
			String distAutAgentModelID) {
		try {
			this.distributedAutonomousAgentID = distributedAutonomousAgentID;
			this.distAutAgentModelID = distAutAgentModelID;

			jade_MAS_AgentContext.initializeCsfAgent("TESTconfigFile");
		} catch (JDOMException e) {
			throw new CsfRuntimeException("Error initializing the agent", e);
		} catch (IOException e) {
			throw new CsfRuntimeException("Error initializing the agent", e);
		}
	}

	/*
	 * Receives the portion of the Message Exchange XML that belongs to this agent
	 */
	public void receiveMessage(FrameworkMessage msg, String messageID,
			String inReplyToMessageID, JadeController jade_Controller_Agent) {
		// converts the distributed autonomous agent document back to the full message
		// exchange document that then gets converted into a FrameworkMessage
		// All of the other information not meant for this distributed autonomous agent is
		// not present either in the original or converted XML
		// jzombies_JADE_Csf.setJadeController(jade_Controller_Agent);

		System.out.println("[MockHumanJADE_Agent "
				+ distributedAutonomousAgentID
				+ "] Received message ID: "
				+ distributedAutonomousAgentID
				+ " Message:"
				+ XMLUtilities.convertDocumentToXMLString(msg.getDocument()
						.getRootElement(), true));

		// The framework message to this distributed autonomous agent can be expected to
		// only contain a single entry for the distributed autonomous agent
/*		Element distributedAutonomousAgentElement = msg
				.getNextDistributedAutonomousAgent(msg.getDocument(), null);

		FrameworkMessage fm = jade_MAS_AgentContext
				.convertDocumentSentToDistributedAutonomousAgentToFrameworkMessage(
						distributedAutonomousAgentElement, distributedAutonomousAgentID,
						SYSTEM_TYPE.SIMULATION_ENGINE, SYSTEM_TYPE.DISTRIBUTED_SYSTEM);

		List<String> selfPoint = fm
				.getSelfLocation(distributedAutonomousAgentElement, fm);*/
		
		List<String> selfPoint = msg.getSelfLocation(msg);
		for (int i = 0; i < selfPoint.size(); i++) {
			System.out.println("[MockHumanJADE_Agent ID: " + distributedAutonomousAgentID
					+ "] Self Location: " + String.valueOf(i) + " : "
					+ String.valueOf(selfPoint.get(i)));
		}
		
		Element distributedAutonomousAgentElement = msg
				.getNextDistributedAutonomousAgent(msg.getDocument(), null);
		
		List<String> pointWithLeastZombiesPoint = jzombies_CSF
				.getPointWithLeastZombies(distributedAutonomousAgentElement, msg);

		for (int i = 0; i < pointWithLeastZombiesPoint.size(); i++) {
			System.out.println("[MockHumanJADE_Agent ID: " + distributedAutonomousAgentID
					+ "] Received Zombie location " + String.valueOf(i) + " : "
					+ String.valueOf(pointWithLeastZombiesPoint.get(i)));
		}

		List<String> pointToMoveTo = decideWhereToMoveTo(selfPoint,
				pointWithLeastZombiesPoint);
		// Send the decision on where to move to
		String newMessageID = UUID.randomUUID().toString();
		String originalMessageId = messageID;
		
		msg = jzombies_CSF
				.convertMoveToPointToFrameworkMessage(new ArrayList<String>(),
						new ArrayList<String>(), pointToMoveTo,
						distributedAutonomousAgentID, distAutAgentModelID);
		System.out.println("[MockHumanJADE_Agent ID: " + distributedAutonomousAgentID
				+ "] Sending move decision to the JADE Controller Agent: "+ XMLUtilities.convertDocumentToXMLString(msg.getDocument().getRootElement(),true));
		
		jade_Controller_Agent.receiveMessage(msg, newMessageID,
				originalMessageId);
		

	}

	private List<String> decideWhereToMoveTo(List<String> selfPoint,
			List<String> pointWithLeastZombiesPoint) {

		return pointWithLeastZombiesPoint;

	}
}
