package org.simulationsystems.csf.distsys.adapters.jade.api.nativeagents;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

import java.util.UUID;

import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.distsys.adapters.jade.api.JadeControllerMock;

public class CsfDistributedJADEagentWrapper implements NativeDistributedAutonomousAgent {

	private AID aid;
	private String distributedSystemID;
	private String distributedAutonomousAgentID;
	private String distAutAgentModelID;
	private String modelName;
	private Agent jadeControllerAgent;

	public CsfDistributedJADEagentWrapper(AID aid, String distributedSystemID,
			String distributedAutonomousAgentID, String distAutAgentModelID,
			String modelName, Agent jadeControllerAgent) {
		this.aid = aid;
		this.distributedSystemID = distributedSystemID;
		this.distributedAutonomousAgentID = distributedAutonomousAgentID;
		this.distAutAgentModelID = distAutAgentModelID;
		this.modelName = modelName;
		this.jadeControllerAgent = jadeControllerAgent;
	}

	public String getDistributedAutonomousAgentID() {
		return distributedAutonomousAgentID;
	}

	public String getModelName() {
		return modelName;
	}

	public String getDistributedAutonomousAgentModelID() {
		return distAutAgentModelID;
	}

	@Override
	/*
	 * Takes a message submitted by the JADE Controller Agent (via the JADE-CSF API)
	 * whichs is bound for a JADE agent, and actually sends it to the correct JADE agent
	 * using native JADE communication. It is important to note that while this object
	 * represents a distributed JADE agent, it is really an object contained within the
	 * JADE Controller Agent's memory. Therefore, any references to the JADE agent or
	 * myAgent is the JADE Controller agent. JadeControllerMock is only used with mocks.
	 */
	public void receiveMessage(FrameworkMessage msg, String messageID,
			String inReplyToMessageID, JadeControllerMock jade_ControllerMock) {
		ACLMessage aclMsg = new ACLMessage(ACLMessage.INFORM);
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
