package org.opensimulationsystems.cabsf.distsys.adapters.jade.api;

import jade.core.Agent;

import java.io.IOException;
import java.util.Set;

import org.opensimulationsystems.cabsf.common.internal.messaging.xml.XMLUtilities;
import org.opensimulationsystems.cabsf.common.model.SYSTEM_TYPE;
import org.opensimulationsystems.cabsf.common.model.cabsfexceptions.CabsfInitializationRuntimeException;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FRAMEWORK_COMMAND;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FrameworkMessage;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FrameworkMessageImpl;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.STATUS;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.nativeagents.NativeDistributedAutonomousAgent;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.nativeagents.NativeJADEMockContext;
import org.opensimulationsystems.cabsf.distsys.core.api.DistSysRunContext;
import org.opensimulationsystems.cabsf.distsys.core.api.DistSysRunGroupContext;
import org.opensimulationsystems.cabsf.distsys.core.api.DistributedSystemAPI;

/**
 * The JADE MAS Adapter context factory. Part of the JADE MAS Adapter API
 *
 * @author Jorge Calderon
 */
public class Jade_AdapterAPI {

    /** The instance. */
    private static Jade_AdapterAPI instance = new Jade_AdapterAPI();

    // private DistSysRunContext
    /**
     * The singleton
     *
     * @return single instance of Jade_AdapterAPI
     */
    public static Jade_AdapterAPI getInstance() {
        return instance;
    }

    /** The Distributed System Adapter context factory. */
    private final DistributedSystemAPI distributedSystemAPI = DistributedSystemAPI
            .getInstance();

    /** The distributed system name to set in distributed system api. */
    private final String distributedSystemNameToSetInDistributedSystemAPI = "JADE";

    /** The jade_ ma s_ run context. */
    private Jade_RunContext jade_MAS_RunContext;

    /** The jade controller mock. */
    private JadeControllerInterface jadeControllerInterface;

    /** The jade controller agent. */
    private Agent jadeControllerAgent;

    /**
     * Instantiates a new Jade_AdapterAPI.
     */
    private Jade_AdapterAPI() {
        super();
    }

    /**
     * Assign jade agents to distributed autonomous agents.
     *
     * @param jadeAgents
     *            the jade agents
     * @param jade_MAS_RunContext
     *            the jade_ ma s_ run context
     * @see assignJadeAgentToDistributedAutonomousAgent
     */
    @SuppressWarnings("unused")
    private void assignJadeAgentsToDistributedAutonomousAgents(
            final Set<NativeDistributedAutonomousAgent> jadeAgents,
            final Jade_RunContext jade_MAS_RunContext) {
        for (final NativeDistributedAutonomousAgent jadeAgent : jadeAgents) {
            assignJadeAgentToDistributedAutonomousAgent(jadeAgent, jade_MAS_RunContext);
        }
    }

    /**
     * Assign jade agent to distributed autonomous agent.
     *
     * @param jadeAgent
     *            the jade agent
     * @param jade_MAS_RunContext
     *            the jade_ ma s_ run context
     * @see assignJadeAgentsToDistributedAutonomousAgents
     */
    private void assignJadeAgentToDistributedAutonomousAgent(
            final NativeDistributedAutonomousAgent jadeAgent,
            final Jade_RunContext jade_MAS_RunContext) {
        distributedSystemAPI.assignNativeDistributedAutonomousAgent(jadeAgent,
                jade_MAS_RunContext.getDistSysRunContext());
    }

    /**
     * Initialize the API and create the JADE MAS Run Group Context.
     *
     * @param frameworkConfigurationFileName
     *            the framework configuration file name
     * @return the JAD e_ ma s_ run group context
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public Jade_RunGroupContext initializeAPI(
            final String frameworkConfigurationFileName) throws IOException {

        final DistSysRunGroupContext distSysRunGroupContext = distributedSystemAPI
                .initializeAPI(frameworkConfigurationFileName,
                        distributedSystemNameToSetInDistributedSystemAPI);

        // Set the JADE-specific objects, using the Decorator Pattern
        final Jade_RunGroupContext jade_MAS_RunGroupContext = new Jade_RunGroupContext(
                distSysRunGroupContext);

        return jade_MAS_RunGroupContext;
    }

    /**
     * Initialize simulation run and creates the JADE MAS Run Context. This
     * method must be called after the Jade_RunGroupContext has been
     * created.
     *
     * @param nativeJadeContextForThisRun
     *            the native jade context for this run
     * @param jade_MAS_RunGroupContext
     *            the jade_ ma s_ run group context
     * @param jadeControllerInterface
     *            the jade controller mock
     * @param nativeAgentsSet
     *            the native agents set
     * @return the JAD e_ ma s_ run context
     */
    public Jade_RunContext initializeSimulationRun(
            final NativeJADEMockContext nativeJadeContextForThisRun,
            final Jade_RunGroupContext jade_MAS_RunGroupContext,
            final JadeControllerInterface jadeControllerInterface,
            final Set<NativeDistributedAutonomousAgent> nativeAgentsSet,
            final boolean executeHandshake) {
        this.jadeControllerInterface = jadeControllerInterface;

        final DistSysRunContext distSysRunContext = distributedSystemAPI
                .initializeSimulationRun(nativeJadeContextForThisRun,
                        jade_MAS_RunGroupContext.getDistSysRunGroupContext());

        // User Decorator Pattern for JADE_DistSysRunContext
        jade_MAS_RunContext = new Jade_RunContext(distSysRunContext);
        jade_MAS_RunContext.setJadeContextForThisRun(nativeJadeContextForThisRun);

        // jade_MAS_RunContext.getDistSysRunContext().getSimulationEngineManager().initializeAgentMappings();

        // LOW: Support multiple Simulation Run Groups. For now just assume that
        // there's
        // one.
        jade_MAS_RunContext
        .getDistSysRunContext()
        .getDistributedAgentsManager()
        .initializeDistributedAutonomousAgents(nativeJadeContextForThisRun,
                nativeAgentsSet);

        if (executeHandshake) {
            // Listen for START_SIMULATION command from the simulation engine
            final FrameworkMessage msg = jade_MAS_RunContext
                    .listenForMessageFromSimulationEngine();
            System.out
            .println("[JADE Controller Agent] Received framework message from the simulation engine: "
                    + XMLUtilities.convertDocumentToXMLString(msg.getDocument(),
                            true));
            final FRAMEWORK_COMMAND fc = msg.getFrameworkToDistributedSystemCommand();
            // TODO: Better error handling. Send a message back to the
            // simulation engine that
            // this distributed system is terminating

            if (fc == null || !fc.equals(FRAMEWORK_COMMAND.START_SIMULATION)) {
                String fcStr = "null";
                if (fc != null) {
                    fcStr = fc.toString();
                }
                throw new CabsfInitializationRuntimeException(
                        "The JADE Controller Agent tried to read message from the simulation engine, but did not understand the command: "
                                + fcStr
                                + " The most common cause is that a previous simulation run didn't complete and a message was left"
                                + " in the Redis cache (if using Redis as the common interface), which has been now picked up in this"
                                + " new simulation run. Try killing all CABSF processes, flushing the Redis cache by going to the scripts"
                                + " directory and re-running the simulation.");
            }

            // Push Status of Ready back to the simulation engine
            final FrameworkMessage fm = new FrameworkMessageImpl(
                    SYSTEM_TYPE.DISTRIBUTED_SYSTEM, SYSTEM_TYPE.SIMULATION_ENGINE,
                    jade_MAS_RunContext.getCachedMessageExchangeTemplate());
            fm.setStatus(STATUS.READY_TO_START_SIMULATION);
            jade_MAS_RunContext.sendMessageToSimulationEngine(fm,
                    jade_MAS_RunContext.getDistSysRunContext());
        }

        // TODO:Move all of this up one API level
        // Get the tick information from the simulation engine
        // Distribute message to the JADE Agent
        jade_MAS_RunContext.setDam(jade_MAS_RunContext.getDistSysRunContext()
                .getDistributedAgentsManager());

        jade_MAS_RunContext.setJadeController(jadeControllerInterface);

        return jade_MAS_RunContext;
    }

}