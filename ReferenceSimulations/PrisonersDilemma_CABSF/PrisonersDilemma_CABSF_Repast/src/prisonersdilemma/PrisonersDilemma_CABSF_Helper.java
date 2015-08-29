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
import org.opensimulationsystems.cabsf.common.model.context.AgentContext;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FrameworkMessage;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FrameworkMessageImpl;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.JADE_MAS_AgentContext;
import org.opensimulationsystems.cabsf.sim.adapters.simengines.repastS.api.CabsfRepastS_AgentContext;
import org.opensimulationsystems.cabsf.sim.core.api.distributedsystems.SimulationDistributedSystemManager;

/**
 * Convenience class provided to the simulation and agent authors. Unlike all
 * other classes, this class has references to both the Repast Agent Context and
 * JADE Agent Context. This allows all common shared JZombies-specific code to
 * be in one place.
 *
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class PrisonersDilemma_CABSF_Helper {

    /** The element filter used when querying the XML. */
    private final Filter<Element> elementFilter = new org.jdom2.filter.ElementFilter();

    /** The CabsfRepastS_AgentContext context. */
    private CabsfRepastS_AgentContext cabsfRepastS_AgentContext;

    /** The namespace string. */
    private final String namespaceStr = "http://www.opensimulationsystems.org/cabsf/schemas/CabsfMessageExchange/0.1.0";

    // LOW: Add support in the API for only needing to specify either the
    // namespace or
    // namespace string.
    /** The namespace. */
    private final Namespace namespace = Namespace.getNamespace("x", namespaceStr);

    /** The JADE_MAS_AgentContext context. */
    private JADE_MAS_AgentContext jade_MAS_AgentContext;

    /** The agent context. */
    private final AgentContext agentContext;

    /**
     * Instantiates a new PrisonersDilemma_CABSF_Helper.
     *
     * @param cabsfRepastS_AgentContext
     *            the CabsfRepastS_AgentContext context
     */
    public PrisonersDilemma_CABSF_Helper(
            final CabsfRepastS_AgentContext cabsfRepastS_AgentContext) {
        this.cabsfRepastS_AgentContext = cabsfRepastS_AgentContext;
        agentContext = cabsfRepastS_AgentContext;
    }

    /**
     * Instantiates a new PrisonersDilemma_CABSF_Helper.
     *
     * @param jade_MAS_AgentContext
     *            the JADE_MAS_AgentContext context
     */
    public PrisonersDilemma_CABSF_Helper(final JADE_MAS_AgentContext jade_MAS_AgentContext) {
        this.jade_MAS_AgentContext = jade_MAS_AgentContext;
        agentContext = jade_MAS_AgentContext;
        try {
            jade_MAS_AgentContext.initializeCabsfAgent("TEST");
        } catch (final JDOMException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
     * Populate Prison�r's Dilemma FrameworkMessage which wraps the XML message
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
            final Object simAgent, final int round, final DECISION otherPlayerLastDecision,
            final DECISION myDecision) {
        // TODO: Add support for multiple distributed systems
        // Get the Agent Mapping
        final SimulationDistributedSystemManager dsm = cabsfRepastS_AgentContext
                .getRepastS_SimulationRunContext().getSimulationDistributedSystemManager(
                        simAgent);
        final AgentMapping am = dsm.getAgentMappingForObject(simAgent);
        // TODO: Add validation here
        assert (am != null);

        // Construct FrameworkMessage to send to the distributed agent
        final FrameworkMessage msg = new FrameworkMessageImpl(
                SYSTEM_TYPE.SIMULATION_ENGINE, SYSTEM_TYPE.DISTRIBUTED_SYSTEM,
                agentContext.getBlankCachedMessageExchangeTemplate());
        assert (cabsfRepastS_AgentContext.getRepastS_SimulationRunContext()
                .getCachedDistributedAutonomousAgentTemplate() != null);

        // Get the distributed autonomous agent and set the ID
        final Element distributedAutonomousAgentElement = msg
                .getNextDistributedAutonomousAgent(msg.getDocument(),
                        agentContext.getCachedDistributedAutonomousAgentTemplate());
        msg.setDistributedAutonomousAgentID(distributedAutonomousAgentElement,
                am.getSoftwareAgentID());

        // Get the agent model actor and set the ID
        final Element agentModelActor = msg.getNextAgentModelActor(
                distributedAutonomousAgentElement,
                agentContext.getCachedAgentModelActorTemplate());
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
        cabsfRepastS_AgentContext.getRepastS_SimulationRunContext()
        .messageDistributedSystems(
                msg,
                cabsfRepastS_AgentContext.getRepastS_SimulationRunContext()
                .getSimulationRunContext());
    }

}
