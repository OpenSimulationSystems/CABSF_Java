package org.simulationsystems.csf.distsys.mas.mocks;

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
import org.simulationsystems.csf.distsys.adapters.jade.api.JadeControllerMock;
import org.simulationsystems.csf.distsys.adapters.jade.api.nativeagents.NativeDistributedAutonomousAgent;

public class MockHumanJADE_Agent implements NativeDistributedAutonomousAgent {

	private String distributedAutonomousAgentID;
	private String modelName;
	
	public String getModelName() {
		return modelName;
	}
	
	public String getDistributedAutonomousAgentID() {
		return distributedAutonomousAgentID;
	}

	public void setDistributedAutonomousAgentID(String distributedAutonomousAgentID) {
		this.distributedAutonomousAgentID = distributedAutonomousAgentID;
	}

	private String distributedAutonomousAgentModelID;

	// This is not the same instance as the context running in the controller agent due to
	// the fact that these JADE agents are all autononmous. The important here is to
	// provide access to the API to understand the messages.
	JADE_MAS_AgentContext jade_MAS_AgentContext = new JADE_MAS_AgentContext();
	private JZombies_Csf jzombies_CSF = new JZombies_Csf(jade_MAS_AgentContext);
	private String distributedSystemID;
	private String logPrefix=null;

	public MockHumanJADE_Agent(String distributedSystemID, String distributedAutonomousAgentID,
			String distAutAgentModelID, String modelName) {
		try {
			this.distributedSystemID = distributedSystemID;
			this.distributedAutonomousAgentID = distributedAutonomousAgentID;
			this.distributedAutonomousAgentModelID = distAutAgentModelID;
			this.modelName = modelName;
			logPrefix = "[MockHumanJADE_Agent "+ distributedSystemID+" "+distributedAutonomousAgentID+" "+distAutAgentModelID+"]";

			jade_MAS_AgentContext.initializeCsfAgent("TESTconfigFile");
		} catch (JDOMException e) {
			throw new CsfRuntimeException("Error initializing the agent", e);
		} catch (IOException e) {
			throw new CsfRuntimeException("Error initializing the agent", e);
		}
	}
	
	public String getDistributedAutonomousAgentModelID() {
		return distributedAutonomousAgentModelID;
	}

	/*
	 * Receives the portion of the Message Exchange XML that belongs to this agent
	 */
	public void receiveMessage(FrameworkMessage msg, String messageID,
			String inReplyToMessageID, JadeControllerMock jade_Controller_Agent) {
		// converts the distributed autonomous agent document back to the full message
		// exchange document that then gets converted into a FrameworkMessage
		// All of the other information not meant for this distributed autonomous agent is
		// not present either in the original or converted XML

		System.out.println("[NativeDistributedAutonomousAgent "
				+ distributedAutonomousAgentID
				+ "] Received message ID: "
				+ distributedAutonomousAgentID
				+ " Message:"
				+ XMLUtilities.convertDocumentToXMLString(msg.getDocument()
						.getRootElement(), true));

		// The framework message to this distributed autonomous agent can be expected to
		// only contain a single entry for the distributed autonomous agent

		List<String> selfPoint = msg.getSelfLocation(msg);
		for (int i = 0; i < selfPoint.size(); i++) {
			System.out.println(logPrefix+" Self Location: " + String.valueOf(i) + " : "
					+ String.valueOf(selfPoint.get(i)));
		}

		Element distributedAutonomousAgentElement = msg
				.getNextDistributedAutonomousAgent(msg.getDocument(), null);

		List<String> pointWithLeastZombiesPoint = jzombies_CSF.getPointWithLeastZombies(
				distributedAutonomousAgentElement, msg);

		for (int i = 0; i < pointWithLeastZombiesPoint.size(); i++) {
			System.out.println(logPrefix+" Received Zombie location " + String.valueOf(i) + " : "
					+ String.valueOf(pointWithLeastZombiesPoint.get(i)));
		}

		List<String> pointToMoveTo = chooseMoveTowardsLocation(selfPoint,
				pointWithLeastZombiesPoint);
		// Send the decision on where to move to
		String newMessageID = UUID.randomUUID().toString();
		String originalMessageId = messageID;

		msg = jzombies_CSF.convertMoveToPointToFrameworkMessage(pointToMoveTo,
				distributedAutonomousAgentID, distributedAutonomousAgentModelID);
		System.out.println(logPrefix+" Sending move decision to the JADE Controller Agent: "
				+ XMLUtilities.convertDocumentToXMLString(msg.getDocument()
						.getRootElement(), true));

		jade_Controller_Agent.receiveMessage(msg, newMessageID, originalMessageId);

	}

	private List<String> chooseMoveTowardsLocation(List<String> selfPoint,
			List<String> pointWithLeastZombiesPoint) {

		return pointWithLeastZombiesPoint;

	}
}
