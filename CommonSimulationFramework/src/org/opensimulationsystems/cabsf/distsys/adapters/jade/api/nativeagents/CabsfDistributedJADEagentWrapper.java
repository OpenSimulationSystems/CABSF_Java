package org.opensimulationsystems.cabsf.distsys.adapters.jade.api.nativeagents;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

import java.util.UUID;

import org.opensimulationsystems.cabsf.common.model.messaging.messages.FrameworkMessage;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.JadeControllerInterface;

/**
 * The wrapper for holding on to the IDs to uniquely identify a distributed autonomous
 * agent within a distributed system.
 *
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class CabsfDistributedJADEagentWrapper implements NativeDistributedAutonomousAgent {

	/** The aid. */
	private final AID aid;

	/** The distributed system id. */
	private final String distributedSystemID;

	/** The distributed autonomous agent id. */
	private final String distributedAutonomousAgentID;

	/** The dist aut agent model id. */
	private final String distAutAgentModelID;

	/** The model name. */
	private final String modelName;

	/** The jade controller agent. */
	private final Agent jadeControllerAgent;

	/**
	 * Instantiates a new cabsf distributed jade agent wrapper.
	 *
	 * @param aid
	 *            the aid
	 * @param distributedSystemID
	 *            the distributed system id
	 * @param distributedAutonomousAgentID
	 *            the distributed autonomous agent id
	 * @param distAutAgentModelID
	 *            the dist aut agent model id
	 * @param modelName
	 *            the model name
	 * @param jadeControllerAgent
	 *            the jade controller agent
	 */
	public CabsfDistributedJADEagentWrapper(final AID aid,
			final String distributedSystemID, final String distributedAutonomousAgentID,
			final String distAutAgentModelID, final String modelName,
			final Agent jadeControllerAgent) {
		this.aid = aid;
		this.distributedSystemID = distributedSystemID;
		this.distributedAutonomousAgentID = distributedAutonomousAgentID;
		this.distAutAgentModelID = distAutAgentModelID;
		this.modelName = modelName;
		this.jadeControllerAgent = jadeControllerAgent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opensimulationsystems.cabsf.distsys.adapters.jade.api.nativeagents.
	 * NativeDistributedAutonomousAgent#getDistributedAutonomousAgentID()
	 */
	@Override
	public String getDistributedAutonomousAgentID() {
		return distributedAutonomousAgentID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opensimulationsystems.cabsf.distsys.adapters.jade.api.nativeagents.
	 * NativeDistributedAutonomousAgent#getDistributedAutonomousAgentModelID()
	 */
	@Override
	public String getDistributedAutonomousAgentModelID() {
		return distAutAgentModelID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opensimulationsystems.cabsf.distsys.adapters.jade.api.nativeagents.
	 * NativeDistributedAutonomousAgent#getModelName()
	 */
	@Override
	public String getModelName() {
		return modelName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opensimulationsystems.cabsf.distsys.adapters.jade.api.nativeagents.
	 * NativeDistributedAutonomousAgent
	 * #receiveMessage(org.opensimulationsystems.cabsf.common.model
	 * .messaging.messages.FrameworkMessage, java.lang.String, java.lang.String,
	 * org.opensimulationsystems.cabsf.distsys.adapters.jade.api.JadeControllerInterface)
	 */
	@Override
	public void receiveMessage(final FrameworkMessage msg, final String messageID,
			final String inReplyToMessageID, final JadeControllerInterface jade_ControllerMock) {
		final ACLMessage aclMsg = new ACLMessage(ACLMessage.INFORM);
		aclMsg.addReceiver(aid);
		aclMsg.setContent(msg.toPrettyPrintedXMLString());
		aclMsg.setConversationId("jade-controller");
		aclMsg.setReplyWith(UUID.randomUUID().toString());

		System.out
				.println("[JADE Controller Agent ] Sending message to distributed JADE Agent: "
						+ distributedSystemID
						+ " "
						+ distributedAutonomousAgentID
						+ " "
						+ distAutAgentModelID);
		jadeControllerAgent.send(aclMsg);

	}
}
