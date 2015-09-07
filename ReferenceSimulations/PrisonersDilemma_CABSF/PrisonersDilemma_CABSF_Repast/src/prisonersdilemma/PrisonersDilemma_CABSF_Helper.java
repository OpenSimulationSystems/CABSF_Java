package prisonersdilemma;

import java.io.IOException;
import java.util.List;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.Filter;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.opensimulationsystems.cabsf.common.internal.messaging.xml.XMLUtilities;
import org.opensimulationsystems.cabsf.common.model.AgentMapping;
import org.opensimulationsystems.cabsf.common.model.SYSTEM_TYPE;
import org.opensimulationsystems.cabsf.common.model.cabsfexceptions.CabsfInitializationRuntimeException;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FrameworkMessage;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FrameworkMessageImpl;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.Jade_AgentContext_Cabsf;
import org.opensimulationsystems.cabsf.sim.adapters.simengines.repastS.api.RepastS_AgentContext_Cabsf;
import org.opensimulationsystems.cabsf.sim.core.api.distributedsystems.SimulationDistributedSystemManager;

// TODO: Auto-generated Javadoc
/**
 *
 * The simulation-specific convenience class for the PrisonersDilemma_CABSF
 * simulation. Contains methods to populate, read, and send FrameworkMessage
 * messages. This helper method is used by both the RepastS and JADE agents for
 * populating the messages in the XML.
 *
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class PrisonersDilemma_CABSF_Helper {

    /** The element filter used when querying the XML. */
    private final Filter<Element> elementFilter = new org.jdom2.filter.ElementFilter();

    /** The RepastS_AgentContext_Cabsf context. */
    private RepastS_AgentContext_Cabsf repastS_AgentContext_Cabsf;

    // TODO: Get this from the configuration
    private final String namespaceStr = "http://www.opensimulationsystems.org/cabsf/schemas/CabsfMessageExchange/0.1.0";
    private final Namespace namespace = Namespace.getNamespace("x", namespaceStr);

    /** The Jade_AgentContext_Cabsf context. */
    private Jade_AgentContext_Cabsf jade_AgentContext;

    private String frameworkConfigurationFileName;

    /**
     * Instantiates a new PrisonersDilemma_CABSF_Helper for the calling JADE
     * agent. This method should only be called by JADE agents.
     *
     * @param jade_AgentContext
     *            the Jade_AgentContext_Cabsf
     * @param frameworkConfigurationFileName
     *            the framework configuration file name. May be null, as the
     *            configuration file is not required in the JADE agents, only
     *            the JCA and the RepastS simulation.
     */
    public PrisonersDilemma_CABSF_Helper(final Jade_AgentContext_Cabsf jade_MAS_AgentContext,
            final String frameworkConfigurationFileName) {
        this.jade_AgentContext = jade_MAS_AgentContext;
        this.frameworkConfigurationFileName = frameworkConfigurationFileName;

        try {
            jade_MAS_AgentContext
            .initializeJadeAgentForCabsf(frameworkConfigurationFileName);
        } catch (final JDOMException | IOException e) {
            throw new CabsfInitializationRuntimeException(
                    "Error initializing JADE agent", e);

        }
    }

    /**
     * Instantiates a new PrisonersDilemma_CABSF_Helper for the calling RepastS
     * agent. This method should only be called by RepastS agents.
     * <p/>
     * The framework configuration filename is not parameter as that
     * configuration is handled by the RSSR, for the simulation side.
     *
     * @param repastS_AgentContext_Cabsf
     *            the RepastS_AgentContext_Cabsf context
     */
    public PrisonersDilemma_CABSF_Helper(
            final RepastS_AgentContext_Cabsf repastS_AgentContext_Cabsf) {
        this.repastS_AgentContext_Cabsf = repastS_AgentContext_Cabsf;
    }

    /**
     * Gets the other player's decision from the XML.
     *
     * @param distributedAutonomousAgentElement
     *            the distributed autonomous agent element
     * @param msg
     *            the msg
     * @return the other player decision
     */
    public DECISION getOtherPlayerDecision(
            final Element distributedAutonomousAgentElement, final FrameworkMessage msg) {
        // FIXME: Why does this have to be an element, and not Document?
        final Element agentModelActor = msg.getNextAgentModelActor(
                distributedAutonomousAgentElement, null);
        final List<Element> simulationDefinedEnvironmentChanges = (List<Element>) XMLUtilities
                .executeXPath(agentModelActor,
                        "./x:EnvironmentChanges/x:SimulationDefinedEnvironmentChanges",
                        namespaceStr, elementFilter);

        final Element otherPlayerDecisionElement = simulationDefinedEnvironmentChanges
                .get(0).getChild("OtherPlayerDecision", namespace);
        String decisionStr = null;
        if (otherPlayerDecisionElement != null) {
            decisionStr = otherPlayerDecisionElement.getText();
        } else {
            return null;
        }

        return DECISION.valueOf(decisionStr);
    }

    /**
     * Gets the current round number.
     *
     * @param distributedAutonomousAgentElement
     *            the distributed autonomous agent element
     * @param msg
     *            the msg
     * @return the round number
     */
    public Integer getRoundNumber(final Element distributedAutonomousAgentElement,
            final FrameworkMessage msg) {
        // FIXME: Why does this have to be an element, and not Document?
        final Element agentModelActor = msg.getNextAgentModelActor(
                distributedAutonomousAgentElement, null);
        final List<Element> simulationDefinedEnvironmentChanges = (List<Element>) XMLUtilities
                .executeXPath(agentModelActor,
                        "./x:EnvironmentChanges/x:SimulationDefinedEnvironmentChanges",
                        namespaceStr, elementFilter);

        final String roundNumberStr = simulationDefinedEnvironmentChanges.get(0)
                .getChild("RoundNumber", namespace).getText();

        return Integer.parseInt(roundNumberStr);
    }

    // Used by the mock. This code is left here for now as the mock code may be
    // migrated
    // to a generic Java adapter for the CABSF.
    /*
     * public void sendMessageToSimulationAgent(JadeControllerInterface
     * jade_Controller_Agent, FrameworkMessage msg, String messageID, String
     * inReplyToMessageID) { jade_Controller_Agent.receiveMessage(msg,
     * messageID, inReplyToMessageID); }
     */

    /**
     * Gets the this player decision from the XML.
     *
     * @param distributedAutonomousAgentElement
     *            the distributed autonomous agent element
     * @param msg
     *            the FrameworkMessage wrapping the XML message
     * @return the this player decision
     */
    public DECISION getThisPlayerDecision(
            final Element distributedAutonomousAgentElement, final FrameworkMessage msg) {
        // FIXME: Why does this have to be an element, and not Document?
        final Element agentModelActor = msg.getNextAgentModelActor(
                distributedAutonomousAgentElement, null);
        final List<Element> simulationDefinedEnvironmentChanges = (List<Element>) XMLUtilities
                .executeXPath(agentModelActor,
                        "./x:EnvironmentChanges/x:SimulationDefinedEnvironmentChanges",
                        namespaceStr, elementFilter);

        final String decisionStr = simulationDefinedEnvironmentChanges.get(0)
                .getChild("ThisPlayerDecision", namespace).getText();

        return DECISION.valueOf(decisionStr);
    }

    /**
     * Populate prisoners dilemma decision and round elements.
     *
     * @param msg
     *            the FrameworkMessage wrapping the XML
     * @param agentModelActor
     *            the agent model actor XML element
     * @param round
     *            the round number
     * @param otherPlayerLastDecision
     *            the other player's last decision
     * @param myDecision
     *            this agent's decision
     * @return the simulationDefinedEnvironmentChange parent element that holds
     *         the Prisoner's-Dilemma-specific DECISION values.
     */
    public Element populatePrisonersDilemmaDecisionAndRoundElements(
            final FrameworkMessage msg, final Element agentModelActor, final int round,
            final DECISION otherPlayerLastDecision, final DECISION myDecision) {
        final Element simulationDefinedEnvironmentChange = msg
                .getSimulationDefinedEnvironmentChangesElement(agentModelActor);

        final Element roundElement = new Element("RoundNumber", namespace);
        roundElement.setText(String.valueOf(round));
        simulationDefinedEnvironmentChange.addContent(roundElement);

        if (otherPlayerLastDecision != null) {
            final Element decisionElement = new Element("OtherPlayerDecision", namespace);
            decisionElement.setText(otherPlayerLastDecision.toString());
            simulationDefinedEnvironmentChange.addContent(decisionElement);

        }
        if (myDecision != null) {
            final Element decisionElement = new Element("ThisPlayerDecision", namespace);
            decisionElement.setText(myDecision.toString());
            simulationDefinedEnvironmentChange.addContent(decisionElement);
        }

        return simulationDefinedEnvironmentChange;
    }

    /**
     * Populate Prison�r's Dilemma FrameworkMessage which wraps the XML message.
     *
     * @param msg
     *            the FrameworkMessage to update
     * @param agentModelActor
     *            the agent model actor
     * @param round
     *            the round
     * @param otherPlayerLastDecision
     *            the other player last decision
     * @param myDecision
     *            this agent's decision
     * @return the updated FrameworkMessage
     */
    public FrameworkMessage populatePrisonersDilemmaFrameworkMessage(
            final FrameworkMessage msg, final Element agentModelActor, final int round,
            final DECISION otherPlayerLastDecision, final DECISION myDecision) {

        populatePrisonersDilemmaDecisionAndRoundElements(msg, agentModelActor, round,
                otherPlayerLastDecision, myDecision);

        final XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        final String xmlString = outputter.outputString(msg.getDocument());
        System.out.println("Populated Prisoner's Dilemma Message: " + xmlString);

        return msg;
    }

    /**
     * Send message to distributed autonomous agent model from simulation agent.
     *
     * @param loggingPrefix
     *            the logging prefix
     * @param simAgent
     *            the native agent object (RepastS agent) that was mapped by the
     *            CABSF.
     * @param round
     *            the round
     * @param otherPlayerLastDecision
     *            the other player last decision
     * @param myDecision
     *            the my decision
     */
    public void sendMsgFromSimAgentToDistributedAgentModel(final String loggingPrefix,
            final Object simAgent, final int round,
            final DECISION otherPlayerLastDecision, final DECISION myDecision) {
        // TODO: Add support for multiple distributed systems
        // Get the Agent Mapping
        final SimulationDistributedSystemManager dsm = repastS_AgentContext_Cabsf
                .getRepastS_SimulationRunContext().getSimulationDistributedSystemManager(
                        simAgent);
        final AgentMapping am = dsm.getAgentMappingForObject(simAgent);
        // TODO: Add validation here
        assert (am != null);

        // Construct FrameworkMessage to send to the distributed agent
        final FrameworkMessage msg = new FrameworkMessageImpl(
                SYSTEM_TYPE.SIMULATION_ENGINE, SYSTEM_TYPE.DISTRIBUTED_SYSTEM,
                jade_AgentContext.getBlankCachedMessageExchangeTemplate());
        assert (repastS_AgentContext_Cabsf.getRepastS_SimulationRunContext()
                .getCachedDistributedAutonomousAgentTemplate() != null);

        // Get the distributed autonomous agent and set the ID
        final Element distributedAutonomousAgentElement = msg
                .getNextDistributedSoftwareAgentElement(msg.getDocument(),
                        jade_AgentContext.getCachedDistributedAutonomousAgentTemplate());
        msg.setDistributedAutonomousAgentID(distributedAutonomousAgentElement,
                am.getSoftwareAgentID());

        // Get the agent model actor and set the ID
        final Element agentModelActor = msg.getNextAgentModelActor(
                distributedAutonomousAgentElement,
                jade_AgentContext.getCachedAgentModelActorTemplate());
        msg.setIDForActorInAgentModel(agentModelActor, am.getAgentModelID());

        // TODO: First get the distributed system manager section.
        // TODO: Add validation here
        assert (am.getAgentModelID() != null);

        // Populate the Decision and Round Info
        populatePrisonersDilemmaFrameworkMessage(msg, agentModelActor, round,
                otherPlayerLastDecision, myDecision);

        System.out.println(loggingPrefix
                + "Sending Message to Distributed System: "
                + XMLUtilities.convertDocumentToXMLString(msg.getDocument()
                        .getRootElement(), true));

        // The message has been constructed, now send it over the wire
        repastS_AgentContext_Cabsf.getRepastS_SimulationRunContext()
        .messageDistributedSystems(
                msg,
                repastS_AgentContext_Cabsf.getRepastS_SimulationRunContext()
                .getSimulationRunContext());
    }

}
