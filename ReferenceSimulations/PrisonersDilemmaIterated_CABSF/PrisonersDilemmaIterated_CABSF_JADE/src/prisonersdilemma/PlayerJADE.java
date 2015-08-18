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
import org.opensimulationsystems.cabsf.common.internal.messaging.xml.XMLUtilities;
import org.opensimulationsystems.cabsf.common.model.SYSTEM_TYPE;
import org.opensimulationsystems.cabsf.common.model.cabsfexceptions.CabsfCheckedException;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FrameworkMessage;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FrameworkMessageImpl;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.JADE_MAS_AgentContext;

/**
 * The JADE class representing the Player in a CABSF-administered
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
            final MessageTemplate mt = MessageTemplate
                    .MatchPerformative(ACLMessage.INFORM);
            // Check the queue. null means no message has come in matching the
            // template.
            final ACLMessage aclMsg = myAgent.receive(mt);

            if (aclMsg != null) {
                // Message matching the template has come in. Get the XML
                // String.
                final String msgStr = aclMsg.getContent();

                FrameworkMessage msg = null;
                // Convert the message content from an XML string to a (CABSF)
                // FrameworkMessage to gain access to convenience methods.
                try {
                    msg = new FrameworkMessageImpl(SYSTEM_TYPE.SIMULATION_ENGINE,
                            SYSTEM_TYPE.DISTRIBUTED_SYSTEM, msgStr);
                } catch (final CabsfCheckedException e) {
                    System.out
                    .println(logPrefix
                            + " Error parsing message from the JADE Controller Agent to this agent.  Message: "
                            + msgStr);
                    doDelete(); // Cleanup and remove this agent from the MAS.
                    e.printStackTrace();
                }

                System.out.println(logPrefix + " Received Message: "
                        + aclMsg.getConversationId() + " " + aclMsg.getReplyWith()
                        + " Message:" + msgStr);

                // Get the distributed autonomous agent element and set the ID
                Element distributedAutonomousAgentElement = msg
                        .getNextDistributedAutonomousAgent(msg.getDocument(), null);
                msg.setDistributedAutonomousAgentID(distributedAutonomousAgentElement,
                        distributedAutonomousAgentID);

                // Get the agent model actor and set the ID
                Element agentModelActor = msg.getNextAgentModelActor(
                        distributedAutonomousAgentElement, null);
                msg.setIDForActorInAgentModel(agentModelActor,
                        distributedAutonomousAgentModelID);

                final int round = prisonersDilemma_CABSF_Helper.getRoundNumber(
                        distributedAutonomousAgentElement, msg);
                final DECISION otherPlayerLastRoundDecision = prisonersDilemma_CABSF_Helper
                        .getOtherPlayerDecision(distributedAutonomousAgentElement, msg);

                System.out.println(logPrefix + " Round: " + String.valueOf(round)
                        + " Received Last Round's Other Player's Decision: "
                        + otherPlayerLastRoundDecision);

                final DECISION myDecision = makeDecision(round,
                        otherPlayerLastRoundDecision);
                System.out.println(logPrefix + " Decided: " + myDecision.toString());

                FrameworkMessage replyMsg = new FrameworkMessageImpl(
                        SYSTEM_TYPE.DISTRIBUTED_SYSTEM, SYSTEM_TYPE.SIMULATION_ENGINE,
                        jade_MAS_AgentContext.getBlankCachedMessageExchangeTemplate());

                // Get the distributed autonomous agent and set the ID
                distributedAutonomousAgentElement = replyMsg
                        .getNextDistributedAutonomousAgent(replyMsg.getDocument(),
                                jade_MAS_AgentContext
                                .getCachedDistributedAutonomousAgentTemplate());
                replyMsg.setDistributedAutonomousAgentID(
                        distributedAutonomousAgentElement, distributedAutonomousAgentID);

                // Get the agent model actor and set the ID
                agentModelActor = replyMsg.getNextAgentModelActor(
                        distributedAutonomousAgentElement,
                        jade_MAS_AgentContext.getCachedAgentModelActorTemplate());
                replyMsg.setIDForActorInAgentModel(agentModelActor,
                        distributedAutonomousAgentModelID);

                replyMsg = prisonersDilemma_CABSF_Helper
                        .populatePrisonersDilemmaFrameworkMessage(replyMsg,
                                agentModelActor, round, null, myDecision);

                System.out.println(logPrefix
                        + " Sending move decision to the JADE Controller Agent: "
                        + aclMsg.getConversationId()
                        + " Message:"
                        + XMLUtilities.convertDocumentToXMLString(replyMsg.getDocument()
                                .getRootElement(), true));

                // Send message to the JADE Controller Agent
                final ACLMessage response = aclMsg.createReply();
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
        private DECISION makeDecision(final int round,
                final DECISION otherPlayerLastRoundDecision) {

            return DECISION.COOPERATE;

        }
    }

    /** The log prefix. */
    private String logPrefix;

    /** The JADE_MAS_AgentContext context. */
    private JADE_MAS_AgentContext jade_MAS_AgentContext;

    /** The PrisonersDilemma_CABSF_Helper. */
    private PrisonersDilemma_CABSF_Helper prisonersDilemma_CABSF_Helper;

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
         * The Common Agent-Based Simulation Framework (CABSF) context object
         * specific to CABSF JADE agents. Gives the user access to many
         * convenience methods for dealing with XML messages coming from the
         * simulation engine/runtime such as Repast Simphony.
         */
        jade_MAS_AgentContext = new JADE_MAS_AgentContext();
        /*
         * This class contains convenience methods specific to this one
         * simulation.
         */
        prisonersDilemma_CABSF_Helper = new PrisonersDilemma_CABSF_Helper(
                jade_MAS_AgentContext);
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
        final DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("jade-CABSF-agents");
        sd.setName("non-admin-agents"); // All JADE agents other than the JADE
        // Controller Agent
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (final FIPAException fe) {
            System.out.println(logPrefix
                    + " Error registering this JADE agent in the yellow pages");
            doDelete(); // Cleanup and remove this agent from the MAS.
            fe.printStackTrace();
        }

        // Find the JADE Controller Agent
        System.out.println(logPrefix + " Looking for the JADE Controller Agent");
        final DFAgentDescription template = new DFAgentDescription();
        sd = new ServiceDescription();
        sd.setType("jade-CABSF-agents");
        sd.setName("jade-controller-agent");
        template.addServices(sd);
        while (true) {
            try {
                final DFAgentDescription[] result = DFService.search(this, template);
                if (result.length == 1) {
                    System.out.println(logPrefix + " Found the JADE Controller Agent: "
                            + result[0].getName().getName());
                    jadeControllerAgent = result[0].getName();
                    // Only one JADE
                    // Controller Agent should exist.
                    break;
                } else {
                    Thread.sleep(1000);
                }
            } catch (final FIPAException fe) {
                System.out
                .println("[JADE Agent "
                        + getAID().getName()
                        + "] FIPA error in finding the JADE Controller Agent.  Terminating.");
                doDelete();
                fe.printStackTrace();
            } catch (final InterruptedException e) {
                System.out.println("[JADE Agent " + getAID().getName()
                        + "] Thread interrupted.  Terminating.");
                doDelete();
                e.printStackTrace();
            }
        }
    }
}
