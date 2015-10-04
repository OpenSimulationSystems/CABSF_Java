package jzombies;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.List;

import org.jdom2.Element;
import org.opensimulationsystems.cabsf.common.internal.messaging.xml.XMLUtilities;
import org.opensimulationsystems.cabsf.common.model.SYSTEM_TYPE;
import org.opensimulationsystems.cabsf.common.model.cabsfexceptions.CabsfCheckedException;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FrameworkMessage;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FrameworkMessageImpl;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.Jade_AgentAdapterAPI;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.Jade_AgentContext_Cabsf;

// TODO: Auto-generated Javadoc
/**
 * The JADE class representing the Human in a CABSF-administered
 * JADE-Repast-Simphony-integrated JZombies simulation.
 *
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class HumanJADE extends Agent {
    /**
     * Inner class that specifies the behavior for this agent. See the JADE
     * documentation to an explanation of the JADE behaviors.
     *
     * @author Jorge Calderon
     * @version 0.1
     * @since 0.1
     */
    private class HumanJADE_Server extends CyclicBehaviour {
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

                // Get the current grid location of this agent from the
                // simulation engine/runtime. The result is a list (X, Y)
                final List<String> selfPoint = msg
                        .getThisAgentLocationFromNextSoftwareAgentNextAgentModelActorInFrameworkMessage(msg);
                assert (selfPoint.size() == 2);
                for (int i = 0; i < selfPoint.size(); i++) {
                    System.out.println(logPrefix + " Self Location: " + String.valueOf(i)
                            + " : " + String.valueOf(selfPoint.get(i)));
                }

                // The JADE Controller Agent has already stripped out the part
                // that needed to be routed to this JADE agent, so we can be
                // sure that the very next distributed software agent section of
                // the XML is meant for this agent. the part of the XML that
                // needs to go to this agent.
                final Element distributedAutonomousAgentElement = msg
                        .getNextMsgForDistributedSoftwareAgentElement(msg.getDocument(),
                                null);

                final List<String> pointWithLeastZombiesPoint = jZombies_CABSF_Helper
                        .getPointWithLeastZombies(distributedAutonomousAgentElement, msg,
                                agentModelID);

                for (int i = 0; i < pointWithLeastZombiesPoint.size(); i++) {
                    System.out.println(logPrefix + " Received Zombie location "
                            + String.valueOf(i) + " : "
                            + String.valueOf(pointWithLeastZombiesPoint.get(i)));
                }

                final List<String> pointToMoveTo = chooseMoveTowardsLocation(selfPoint,
                        pointWithLeastZombiesPoint);

                // Send the decision on where to move to
                msg = jZombies_CABSF_Helper.convertMoveToPointToFrameworkMessage(
                        pointToMoveTo, distributedSoftwareAgentID, agentModelID);
                System.out.println(logPrefix
                        + " Sending move decision to the JADE Controller Agent: "
                        + aclMsg.getConversationId()
                        + " Message:"
                        + XMLUtilities.convertDocumentToXMLString(msg.getDocument()
                                .getRootElement(), true));

                // Send message to the JADE Controller Agent
                final ACLMessage response = aclMsg.createReply();
                response.setPerformative(ACLMessage.INFORM);
                response.setContent(msg.toPrettyPrintedXMLString());
                myAgent.send(response);

            } else {
                // This behavior is de-activated until a new message comes into
                // the queue.
                block();
            }

        }

        /**
         * Choose move towards location.
         *
         * @param selfPoint
         *            the self point
         * @param pointWithLeastZombiesPoint
         *            the point with least zombies point
         * @return the list
         */
        private List<String> chooseMoveTowardsLocation(final List<String> selfPoint,
                final List<String> pointWithLeastZombiesPoint) {

            return pointWithLeastZombiesPoint;

        }
    }

    /** The log prefix. */
    private String logPrefix;

    /** The jade_ ma s_ agent context. */
    private Jade_AgentContext_Cabsf jade_AgentContext;

    /** The jZombies_CABSF_Helper */
    private JZombies_CABSF_Helper jZombies_CABSF_Helper;

    /** The distributed autonomous agent id. */
    private String distributedSoftwareAgentID;

    /** The distributed autonomous agent model id. */
    private String agentModelID;

    /** The jade controller agent. */
    private AID jadeControllerAgent;

    private String frameworkConfigurationFileName;

    /*
     * The agent initialization.
     *
     * @see jade.core.Agent#setup()
     */
    @Override
    protected void setup() {
        logPrefix = "[HumanJADE " + getAID().getName() + "]";

        final Object[] args = getArguments();
        if (args != null && args.length > 0) {
            frameworkConfigurationFileName = (String) args[0];
            System.out.println(logPrefix + " Configuration file "
                    + frameworkConfigurationFileName);
        }

        addBehaviour(new HumanJADE_Server());

        /*
         * The Common Agent-Based Simulation Framework (CABSF) context object
         * specific to CABSF JADE agents. Gives the user access to many
         * convenience methods for dealing with XML messages coming from the
         * simulation engine/runtime such as Repast Simphony.
         */
        jade_AgentContext = Jade_AgentAdapterAPI.getInstance().getJadeAgentContext();
        jade_AgentContext.initializeJadeAgentForCabsf(frameworkConfigurationFileName);

        /*
         * This class contains convenience methods specific to this one
         * simulation.
         */
        jZombies_CABSF_Helper = new JZombies_CABSF_Helper(jade_AgentContext,
                frameworkConfigurationFileName);

        /*
         * The mapping between the JADE agent and RepastS agent actually happens
         * on the "distributed autonomous agent model" level. So a JADE agent
         * can theoretically in the future run several "agent models". For now
         * only 1 to 1 is supported, however, the model must still be identified
         */
        // TODO: Support global JADE naming
        agentModelID = getAID().getLocalName() + "MODEL";

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
