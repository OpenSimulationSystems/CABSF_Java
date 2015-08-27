package org.opensimulationsystems.cabsf.distsys.mas.mocks;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import jzombies.JZombies_CABSF_Helper;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.opensimulationsystems.cabsf.common.internal.messaging.xml.XMLUtilities;
import org.opensimulationsystems.cabsf.common.model.cabsfexceptions.CabsfRuntimeException;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FrameworkMessage;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.JADE_MAS_AgentContext;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.JadeControllerInterface;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.nativeagents.NativeDistributedAutonomousAgent;

public class MockHumanJADE_Agent implements NativeDistributedAutonomousAgent {

	private String distributedAutonomousAgentID;
	private String modelName;

	private String distributedAutonomousAgentModelID;

	// This is not the same instance as the context running in the controller agent due to
	// the fact that these JADE agents are all autononmous. The important here is to
	// provide access to the API to understand the messages.
	JADE_MAS_AgentContext jade_MAS_AgentContext = new JADE_MAS_AgentContext();

	private final JZombies_CABSF_Helper jZombies_CABSF_Helper = new JZombies_CABSF_Helper(
			jade_MAS_AgentContext);

	private String distributedSystemID;

	private String logPrefix = null;

	public MockHumanJADE_Agent(final String distributedSystemID,
			final String distributedAutonomousAgentID, final String distAutAgentModelID,
			final String modelName) {
		try {
			this.distributedSystemID = distributedSystemID;
			this.distributedAutonomousAgentID = distributedAutonomousAgentID;
			this.distributedAutonomousAgentModelID = distAutAgentModelID;
			this.modelName = modelName;
			logPrefix = "[MockHumanJADE_Agent " + distributedSystemID + " "
					+ distributedAutonomousAgentID + " " + distAutAgentModelID + "]";

			jade_MAS_AgentContext.initializeCabsfAgent("TESTconfigFile");
		} catch (final JDOMException e) {
			throw new CabsfRuntimeException("Error initializing the agent", e);
		} catch (final IOException e) {
			throw new CabsfRuntimeException("Error initializing the agent", e);
		}
	}

	private List<String> chooseMoveTowardsLocation(final List<String> selfPoint,
			final List<String> pointWithLeastZombiesPoint) {

		return pointWithLeastZombiesPoint;

	}

	public String getDistributedAutonomousAgentID() {
		return distributedAutonomousAgentID;
	}

	public String getDistributedAutonomousAgentModelID() {
		return distributedAutonomousAgentModelID;
	}

	public String getModelName() {
		return modelName;
	}

	/*
	 * Receives the portion of the Message Exchange XML that belongs to this agent
	 */
	public void receiveMessage(FrameworkMessage msg, final String messageID,
			final String inReplyToMessageID,
			final JadeControllerInterface jade_Controller_Agent) {
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

		final List<String> selfPoint = msg
				.getThisAgentLocationFromNextSoftwareAgentNextAgentModelActorInFrameworkMessage(msg);
		for (int i = 0; i < selfPoint.size(); i++) {
			System.out.println(logPrefix + " Self Location: " + String.valueOf(i) + " : "
					+ String.valueOf(selfPoint.get(i)));
		}

		final Element distributedAutonomousAgentElement = msg
				.getNextDistributedAutonomousAgent(msg.getDocument(), null);

		final List<String> pointWithLeastZombiesPoint = jZombies_CABSF_Helper
				.getPointWithLeastZombies(distributedAutonomousAgentElement, msg);

		for (int i = 0; i < pointWithLeastZombiesPoint.size(); i++) {
			System.out.println(logPrefix + " Received Zombie location "
					+ String.valueOf(i) + " : "
					+ String.valueOf(pointWithLeastZombiesPoint.get(i)));
		}

		final List<String> pointToMoveTo = chooseMoveTowardsLocation(selfPoint,
				pointWithLeastZombiesPoint);
		// Send the decision on where to move to
		final String newMessageID = UUID.randomUUID().toString();
		final String originalMessageId = messageID;

		msg = jZombies_CABSF_Helper.convertMoveToPointToFrameworkMessage(pointToMoveTo,
				distributedAutonomousAgentID, distributedAutonomousAgentModelID);
		System.out.println(logPrefix
				+ " Sending move decision to the JADE Controller Agent: "
				+ XMLUtilities.convertDocumentToXMLString(msg.getDocument()
						.getRootElement(), true));

		jade_Controller_Agent.receiveMessage(msg, newMessageID, originalMessageId);

	}

	public void setDistributedAutonomousAgentID(final String distributedAutonomousAgentID) {
		this.distributedAutonomousAgentID = distributedAutonomousAgentID;
	}
}
