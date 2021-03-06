package prisonersdilemma;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import org.jdom2.Element;
import org.simulationsystems.csf.common.csfmodel.SYSTEM_TYPE;
import org.simulationsystems.csf.common.csfmodel.csfexceptions.CsfCheckedException;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessageImpl;
import org.simulationsystems.csf.common.internal.messaging.xml.XMLUtilities;
import org.simulationsystems.csf.distsys.adapters.jade.api.JADE_MAS_AgentContext;

/**
 * The JADE class representing the Player in a CSF-administered
 * JADE-Repast-Simphony-integrated Prisoner's Dilemma game theory tournament.
 *
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class PlayerJADE extends Agent {

	/**
	 * Inner class that specifies the behavior for this agent. See the JADE
	 * documentation to an explanation of the JADE behaviors.
	 *
	 * @author Jorge Calderon
	 * @version 0.1
	 * @since 0.1
	 */
	private class PrisonersDilemmaJADE_Server extends CyclicBehaviour {
		/*
		 * Called every time a message comes into the queue to this agent. This
		 * is done sequentially due to the fact that a JADE agent only runs a
		 * single thread.
		 * 
		 * @see jade.core.behaviours.Behaviour#action()
		 */
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate
					.MatchPerformative(ACLMessage.INFORM);
			// Check the queue. null means no message has come in matching the
			// template.
			ACLMessage aclMsg = myAgent.receive(mt);

			if (aclMsg != null) {
				// Message matching the template has come in. Get the XML
				// String.
				String msgStr = aclMsg.getContent();

				FrameworkMessage msg = null;
				// Convert the message content from an XML string to a (CSF)
				// FrameworkMessage to gain access to convenience methods.
				try {
					msg = new FrameworkMessageImpl(
							SYSTEM_TYPE.SIMULATION_ENGINE,
							SYSTEM_TYPE.DISTRIBUTED_SYSTEM, msgStr);
				} catch (CsfCheckedException e) {
					System.out
					.println(logPrefix
							+ " Error parsing message from the JADE Controller Agent to this agent.  Message: "
							+ msgStr);
					doDelete(); // Cleanup and remove this agent from the MAS.
					e.printStackTrace();
				}

				System.out.println(logPrefix + " Received Message: "
						+ aclMsg.getConversationId() + " "
						+ aclMsg.getReplyWith() + " Message:" + msgStr);

				// Get the distributed autonomous agent element and set the ID
				Element distributedAutonomousAgentElement = msg
						.getNextDistributedAutonomousAgent(msg.getDocument(),
								null);
				msg.setDistributedAutonomousAgentID(
						distributedAutonomousAgentElement,
						distributedAutonomousAgentID);

				// Get the agent model actor and set the ID
				Element agentModelActor = msg.getNextAgentModelActor(
						distributedAutonomousAgentElement, null);
				msg.setIDForActorInAgentModel(agentModelActor,
						distributedAutonomousAgentModelID);

				int round = prisonersDilemma_CSF.getRoundNumber(
						distributedAutonomousAgentElement, msg);
				DECISION otherPlayerLastRoundDecision = prisonersDilemma_CSF
						.getOtherPlayerDecision(
								distributedAutonomousAgentElement, msg);

				System.out.println(logPrefix + " Round: "
						+ String.valueOf(round)
						+ " Received Last Round's Other Player's Decision: "
						+ otherPlayerLastRoundDecision);

				DECISION myDecision = makeDecision(round,
						otherPlayerLastRoundDecision);
				System.out.println(logPrefix + " Decided: "
						+ myDecision.toString());

				FrameworkMessage replyMsg = new FrameworkMessageImpl(
						SYSTEM_TYPE.DISTRIBUTED_SYSTEM,
						SYSTEM_TYPE.SIMULATION_ENGINE,
						jade_MAS_AgentContext
						.getBlankCachedMessageExchangeTemplate());

				// Get the distributed autonomous agent and set the ID
				distributedAutonomousAgentElement = replyMsg
						.getNextDistributedAutonomousAgent(replyMsg
								.getDocument(), jade_MAS_AgentContext
								.getCachedDistributedAutonomousAgentTemplate());
				replyMsg.setDistributedAutonomousAgentID(
						distributedAutonomousAgentElement,
						distributedAutonomousAgentID);

				// Get the agent model actor and set the ID
				agentModelActor = replyMsg.getNextAgentModelActor(
						distributedAutonomousAgentElement,
						jade_MAS_AgentContext
						.getCachedAgentModelActorTemplate());
				replyMsg.setIDForActorInAgentModel(agentModelActor,
						distributedAutonomousAgentModelID);

				replyMsg = prisonersDilemma_CSF
						.populatePrisonersDilemmaFrameworkMessage(replyMsg,
								agentModelActor, round, null, myDecision);

				System.out
				.println(logPrefix
						+ " Sending move decision to the JADE Controller Agent: "
						+ aclMsg.getConversationId()
						+ " Message:"
						+ XMLUtilities
						.convertDocumentToXMLString(
								replyMsg.getDocument()
								.getRootElement(), true));

				// Send message to the JADE Controller Agent
				ACLMessage response = aclMsg.createReply();
				response.setPerformative(ACLMessage.INFORM);
				response.setContent(replyMsg.toPrettyPrintedXMLString());
				myAgent.send(response);

			} else {
				// This behavior is de-activated until a new message comes into
				// the queue.
				block();
			}

		}

		/**
		 * The method called to determine whether the Player should COOPERATE or
		 * DEFECT for a given round. JADE agent authors should use this method
		 * to store the decision history of their opponent and to make the new
		 * decision.
		 *
		 * @param round
		 *            the round
		 * @param otherPlayerLastRoundDecision
		 *            the other player last round decision
		 * @return the decision
		 */
		private DECISION makeDecision(int round,
				DECISION otherPlayerLastRoundDecision) {

			return DECISION.COOPERATE;

		}
	}

	/** The log prefix. */
	private String logPrefix;

	/** The JADE_MAS_AgentContext context. */
	private JADE_MAS_AgentContext jade_MAS_AgentContext;

	/** The PrisonersDilemma_CSF. */
	private PrisonersDilemma_CSF prisonersDilemma_CSF;

	/** The distributed autonomous agent id. */
	private String distributedAutonomousAgentID;

	/** The distributed autonomous agent model id. */
	private String distributedAutonomousAgentModelID;

	/** The jade controller agent. */
	private AID jadeControllerAgent;

	/*
	 * The agent initialization.
	 * 
	 * @see jade.core.Agent#setup()
	 */
	@Override
	protected void setup() {
		logPrefix = "[PlayerJADE " + getAID().getName() + "]";
		addBehaviour(new PrisonersDilemmaJADE_Server());

		/*
		 * The Common Simulation Framework (CSF) context object specific to CSF
		 * JADE agents. Gives the user access to many convenience methods for
		 * dealing with XML messages coming from the simulation engine/runtime
		 * such as Repast Simphony.
		 */
		jade_MAS_AgentContext = new JADE_MAS_AgentContext();
		/*
		 * This class contains convenience methods specific to this one
		 * simulation.
		 */
		prisonersDilemma_CSF = new PrisonersDilemma_CSF(jade_MAS_AgentContext);
		// TODO: Support global JADE naming
		/*
		 * The mapping between the JADE agent and RepastS agent actually happens
		 * on the "distributed autonomous agent model" level. So a JADE agent
		 * can theoretically in the future run several "agent models". For now
		 * only 1 to 1 is supported, however, the model must still be identified
		 */
		distributedAutonomousAgentModelID = getAID().getLocalName() + "MODEL";

		/*
		 * Register this agent as one of the distributed JADE agents in the
		 * simulation in the yellow pages
		 */
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("jade-csf-agents");
		sd.setName("non-admin-agents"); // All JADE agents other than the JADE
		// Controller Agent
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			System.out.println(logPrefix
					+ " Error registering this JADE agent in the yellow pages");
			doDelete(); // Cleanup and remove this agent from the MAS.
			fe.printStackTrace();
		}

		// Find the JADE Controller Agent
		System.out
		.println(logPrefix + " Looking for the JADE Controller Agent");
		DFAgentDescription template = new DFAgentDescription();
		sd = new ServiceDescription();
		sd.setType("jade-csf-agents");
		sd.setName("jade-controller-agent");
		template.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(this, template);
			assert (result.length == 1);
			System.out.println(logPrefix + " Found the JADE Controller Agent: "
					+ result[0].getName().getName());
			jadeControllerAgent = result[0].getName(); // Only one JADE
			// Controller Agent should exist.

		} catch (FIPAException fe) {
			System.out
			.println("[JADE Controller Agent] FIPA error in finding the JADE Controller Agent.  Terminating.");
			doDelete();
			fe.printStackTrace();
		}
	}
}
