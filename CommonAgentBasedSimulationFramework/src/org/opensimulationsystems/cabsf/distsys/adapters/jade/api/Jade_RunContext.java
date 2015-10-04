package org.opensimulationsystems.cabsf.distsys.adapters.jade.api;

import java.util.List;
import java.util.UUID;

import org.jdom2.Document;
import org.jdom2.Element;
import org.opensimulationsystems.cabsf.common.model.SYSTEM_TYPE;
import org.opensimulationsystems.cabsf.common.model.SimulationRunGroup;
import org.opensimulationsystems.cabsf.common.model.cabsfexceptions.CabsfInitializationRuntimeException;
import org.opensimulationsystems.cabsf.common.model.configuration.RunGroupConfiguration;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FRAMEWORK_COMMAND;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FrameworkMessage;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.nativeagents.NativeDistributedAutonomousAgent;
import org.opensimulationsystems.cabsf.distsys.core.api.DistSysRunContext;
import org.opensimulationsystems.cabsf.distsys.core.api.distributedautonomousagents.DistributedAgentModel;
import org.opensimulationsystems.cabsf.distsys.core.api.distributedautonomousagents.DistributedAgentsManager;
import org.opensimulationsystems.cabsf.distsys.core.api.distributedautonomousagents.DistributedAutonomousAgent;

/**
 * The simulation run context for the JADE MAS. Provides the mechanism for the
 * JADE MAS to communicate with with ABMS systems (like RepastS) for a
 * simulation run.
 *
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class Jade_RunContext {

    /** The DistSysRunContext context. */
    private DistSysRunContext distSysRunContext;

    /** The jade_ context for this run. */
    Object jade_ContextForThisRun;

    /** The dam. */
    private DistributedAgentsManager dam;

    /** The jade controller mock. */
    private JadeControllerInterface jadeControllerInterface;

    /**
     * Instantiates a new Jade_RunContext.
     */
    @SuppressWarnings("unused")
    private Jade_RunContext() {

    }

    /*
     * Uses the decorator pattern to set up a custom JADE MAS RunContext using
     * the general DistSystRunContext object.
     */
    /**
     * Instantiates a new Jade_RunContext.
     *
     * @param distSysRunContext
     *            the dist sys run context
     */
    public Jade_RunContext(final DistSysRunContext distSysRunContext) {
        this.distSysRunContext = distSysRunContext;

        // TODO: Make initialized based on configuration. For now, hard code one
        // distributed system.
    }

    /**
     * Close interface.
     *
     * @param distSysRunContext
     *            the dist sys run context
     */
    public void closeInterface(final DistSysRunContext distSysRunContext) {
        distSysRunContext.closeInterface();
    }

    /**
     * Gets the cached agent model actor template.
     *
     * @return the cached agent model actor template
     */
    public Element getCachedAgentModelActorTemplate() {
        return this.getDistSysRunContext().getDistSysRunGroupContext()
                .getCachedAgentModelActorTemplate();
    }

    /**
     * Gets the cached location template.
     *
     * @return the cached location template
     */
    public Element getCachedLocationTemplate() {
        return this.getDistSysRunContext().getDistSysRunGroupContext()
                .getCachedLocationTemplate();
    }

    /**
     * Gets the cached message exchange template.
     *
     * @return the cached message exchange template
     */
    public Document getCachedMessageExchangeTemplate() {
        return this.getDistSysRunContext().getDistSysRunGroupContext()
                .getBlankCachedMessageExchangeTemplate();
    }

    /**
     * Gets the current JADE_Context.
     *
     * @return the current JADE_Context
     */
    public Object getCurrentJADE_Context() {
        return jade_ContextForThisRun;
    }

    /**
     * Gets the dist sys run context.
     *
     * @return the dist sys run context
     */
    public DistSysRunContext getDistSysRunContext() {
        return distSysRunContext;
    }

    /**
     * Gets the simulation run group.
     *
     * @return the simulation run group
     */
    public SimulationRunGroup getSimulationRunGroup() {
        return distSysRunContext.getSimulationRunGroup();
    }

    /**
     * Gets the DistSysRunGroup configuration.
     *
     * @return the DistSysRunGroup configuration
     */
    public RunGroupConfiguration getSimulationRunGroupConfiguration() {
        return distSysRunContext.getDistributedSystemRunGroupConfiguration();
    }

    /**
     * Initialize agent mappings.
     */
    public void initializeAgentMappings() {
        // TODO Auto-generated method stub

    }

    /**
     * Listen for message from simulation engine.
     *
     * @return the framework message
     */
    public FrameworkMessage listenForMessageFromSimulationEngine() {
        return getDistSysRunContext().listenForMessageFromSimulationEngine();

    }

    // FIXME: Need this?
    /**
     * Request environment information.
     *
     * @return the framework message
     */
    public FrameworkMessage requestEnvironmentInformation() {
        getDistSysRunContext().requestEnvironmentInformation();
        return null;

    }

    /**
     * Message the simulation engine.
     *
     * @param frameworkMessage
     *            the framework message
     * @param distSysRunContext
     *            the dist sys run context
     */
    public void sendMessageToSimulationEngine(final FrameworkMessage frameworkMessage,
            final DistSysRunContext distSysRunContext) {
        distSysRunContext.messageSimulationEngine(frameworkMessage);
    }

    /**
     * Sets the DistributedAgentsManager
     *
     * @param dam
     *            the DistributedAgentsManager
     */
    public void setDam(final DistributedAgentsManager dam) {
        this.dam = dam;
    }

    /*
     *
     */
    /*
     * LOW: Add the ability to support many simultaneous "Context"s
     */
    /**
     * Sets the jade context for this run.
     *
     * @param jade_ContextForThisRun
     *            the new jade context for this run
     */
    public void setJadeContextForThisRun(final Object jade_ContextForThisRun) {
        this.jade_ContextForThisRun = jade_ContextForThisRun;

    }

    /**
     * Sets the jade controller.
     *
     * @param jadeControllerInterface
     *            the new jade controller
     */
    public void setJadeController(final JadeControllerInterface jadeControllerInterface) {
        this.jadeControllerInterface = jadeControllerInterface;

    }

    /**
     * Wait for and process simulation engine message after handshake, including
     * forwarding the appropriate message(s) to the distributed autonomous
     * agents.
     *
     * @return the STOP_SIMULATION command from the simulation system, or null
     *         if simulation is to still continue.
     */
    public FRAMEWORK_COMMAND waitForAndProcessSimulationEngineMessageAfterHandshake() {
        // Now listen for the messages from the simulation engine
        final FrameworkMessage fm = listenForMessageFromSimulationEngine();
        final List<Element> distributedAutonomousAgentElements = fm
                .getDistributedSoftwareAgentElements(fm.getDocument());

        // check whether to terminate the simulation
        // Listen for START_SIMULATION command from the simulation engine
        final FRAMEWORK_COMMAND fc = fm.getFrameworkToDistributedSystemCommand();
        // TODO: Better error handling. Send a message back to the simulation
        // engine that
        // this distributed system is terminating

        if (fc != null && fc.equals(FRAMEWORK_COMMAND.STOP_SIMULATION)) {
            return FRAMEWORK_COMMAND.STOP_SIMULATION;
        }

        // TODO: better validation
        // FIXME: Add
        // ***
        if (distributedAutonomousAgentElements.size() == 0) {
            throw new CabsfInitializationRuntimeException(
                    "No Software agents were found in the message exchange XML from the simulation engine"
                            + " This is usually caused by either the simulation agent not setting the message to be sent properly, or a leftover command"
                            + " from the simulation administrator left in the queue.  If it's the second problem, try flushing the Redis cache (if using Redis"
                            + " for the common CABSF interface.");
        }
        for (final Element distributedAutonomousAgentElement : distributedAutonomousAgentElements) {
            final String distributedAutonomousAgentID = fm
                    .getDistributedSoftwareAgentID(distributedAutonomousAgentElement);
            final DistributedAutonomousAgent distAutAgent = dam
                    .getDistributedAutonomousAgent(distributedAutonomousAgentID);
            // TODO: better validation
            assert (distAutAgent != null);
            final List<Element> agentModelElements = fm
                    .getAgentModels(distributedAutonomousAgentElement); // TODO:
            // better
            // validation
            assert (agentModelElements.size() != 0);

            System.out
            .println("[JADE Controller Agent] Received message from the simulation engine to start the simulation: "
                    + fm.transformToCommonMessagingXMLString(true));

            // Assertions: Agent Model.
            // We don't actually message the models from here, only the
            // distributed
            // autonomous agents
            for (final Element agentModelElement : agentModelElements) {
                final String agentModelIDfromMessage = fm
                        .getFirstAboutAgentModelID(agentModelElement);
                final DistributedAgentModel distAgentModel = distAutAgent
                        .getDistributedAgentModelIDStoAgentModels().get(
                                agentModelIDfromMessage);
                // TODO: add better validation
                assert (distAgentModel != null && agentModelIDfromMessage
                        .equals(distAgentModel.getDistributedAgentModelID()));

            }

            // FIXME: Need to keep this case on the JADE API side, everything
            // else in
            // the main DistaSys API
            // FIXME: Look into whether we need both
            // NativeDistributedAutonomousAgent and
            // DistributedAutonomousAgent
            final NativeDistributedAutonomousAgent nativeDistributedAutonomousAgent = (NativeDistributedAutonomousAgent) distAutAgent
                    .getNativeDistributedAutonomousAgent();
            assert (nativeDistributedAutonomousAgent != null);

            final FrameworkMessage fmToDistributedAutomousAgent = getDistSysRunContext()
                    .getDistSysRunGroupContext()
                    .convertDocumentToSendToDAAtoFrameworkMessage(
                            distributedAutonomousAgentElement,
                            distAutAgent.getDistributedAutonomousAgentID(),
                            SYSTEM_TYPE.SIMULATION_ENGINE, SYSTEM_TYPE.DISTRIBUTED_SYSTEM);

            assert (nativeDistributedAutonomousAgent.getDistributedAutonomousAgentID()
                    .equals(distributedAutonomousAgentID));
            final String messageID = UUID.randomUUID().toString();
            nativeDistributedAutonomousAgent.receiveMessage(fmToDistributedAutomousAgent,
                    messageID, null, jadeControllerInterface);

            // At this point the distributed agent has received the message from
            // the
            // controller, the distributed agent has notified the controller of
            // its
            // decision, the controller has sent the message over
            // the wire to the distributed autonomous agent, and control has
            // returned
            // here.
        }

        return null;
    }
}
