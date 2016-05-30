package org.opensimulationsystems.cabsf.sim.adapters.simengines.repastS.api;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.jdom2.JDOMException;
import org.opensimulationsystems.cabsf.common.internal.messaging.MessagingUtilities;
import org.opensimulationsystems.cabsf.common.model.CABSF_SIMULATION_DISTRIBUATION_TYPE;
import org.opensimulationsystems.cabsf.common.model.cabsfexceptions.CabsfInitializationRuntimeException;
import org.opensimulationsystems.cabsf.common.model.context.AgentContext_Cabsf;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FrameworkMessage;
import org.opensimulationsystems.cabsf.sim.core.api.distributedsystems.SimulationDistributedSystemManager;

import repast.simphony.context.Context;

/**
 * The CABSF Repast Simphony Agent Context. This is the main CABSF-specific
 * context object for the RepastS agent. It augments the native RepastS
 * repast.simphony.context.Context. Accessible from this CABSF
 * RepastS_AgentContext_Cabsf there is a separate CABSF
 * RepastS_SimulationRunContext_CABSF and CABSF
 * RepastS_SimulationRunGroupContext_CABSF, which hold more detailed context
 * information for CABSF simulations.
 * <p/>
 * In general, the individual RepastS agents only need to use the
 * RepastS_AgentContext_Cabsf and simulation-specific helper class. The helper
 * class does utilize RepastS_SimulationRunContext_CABSF and
 * RepastS_SimulationRunGroupContext_CABSF.
 *
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class RepastS_AgentContext_Cabsf extends AgentContext_Cabsf {

    /** The repast s_ simulation run context. */
    private RepastS_SimulationRunContext_CABSF repastS_SimulationRunContext_CABSF = null;

    /** The repast s_ simulation run group context. */
    private RepastS_SimulationRunGroupContext_CABSF repastS_SimulationRunGroupContext_CABSF;

    /** The bypass repast runtime for testing purposes. */
    private Boolean setBypassRepastSruntimeForTesting = false;

    /**
     * Instantiates a new repast s_ agent context.
     */
    public RepastS_AgentContext_Cabsf() {

    }

    /**
     * Gets the CABSF RepastS_SimulationRunContext_CABSF.
     * RepastS_SimulationRunContext_CABSF maintains the state of this specific
     * simulation run.
     *
     * @see getRepastS_SimulationRunGroupContext()
     * @return the repast s_ simulation run context
     */
    public RepastS_SimulationRunContext_CABSF getRepastS_SimulationRunContext() {
        return repastS_SimulationRunContext_CABSF;
    }

    /**
     * Gets the CABSF RepastS_SimulationRunGroupContext_CABSF.
     * RepastS_SimulationRunGroupContext_CABSF maintains overall state of all of
     * the simulation runs for this group of simulation runs being executed.
     * Specific simulation run states is stored in
     * RepastS_SimulationRunContext_CABSF instead.
     *
     * @see getRepastS_SimulationRunContext()
     * @return the repast s_ simulation run group context
     */
    public RepastS_SimulationRunGroupContext_CABSF getRepastS_SimulationRunGroupContext() {
        return repastS_SimulationRunGroupContext_CABSF;
    }

    /**
     * Gets the simulation distributed system manager for this agent.
     *
     * @param agent
     *            the agent
     * @return the simulation distributed system manager
     */
    public SimulationDistributedSystemManager getSimulationDistributedSystemManager(
            final Object agent) {
        return this.repastS_SimulationRunContext_CABSF
                .getSimulationDistributedSystemManager(agent);
    }

    /**
     * Initializes the RepastS agent in the CABSF. Returns
     * CABSF_SIMULATION_DISTRIBUATION_TYPE to identify whether whether the
     * distributed CABSF functionality is being utilized, or all of the logic is
     * here in the simulation runtime, depending on whether the simulation-wide
     * CABSF initializations were previously performed.
     *
     * @param nativeRepastScontext
     *            the native repast scontext
     * @return the simulation type
     */
    public CABSF_SIMULATION_DISTRIBUATION_TYPE initializeRepastSagentForCabsf(
            final Context nativeRepastScontext) {

        /*
         * The CABSF simulation run context should have been added by the time
         * the agent tries to initialize.
         */
        final Iterable<Object> cabsfRepastContextIterable = nativeRepastScontext
                .getAgentLayer(RepastS_SimulationRunContext_CABSF.class);

        if (!setBypassRepastSruntimeForTesting
                && repastS_SimulationRunContext_CABSF == null) {

            // Look to see if the Repast Simulation Context has been created
            // If not that means that this is not a CABSF simulation run
            try {
                repastS_SimulationRunContext_CABSF = (RepastS_SimulationRunContext_CABSF) cabsfRepastContextIterable
                        .iterator().next();
            } catch (final NoSuchElementException e) {
                // RunEnvironment.getInstance().isBatch()) {
                return CABSF_SIMULATION_DISTRIBUATION_TYPE.NON_DISTRIBUTED;

            }

            this.repastS_SimulationRunGroupContext_CABSF = repastS_SimulationRunContext_CABSF
                    .getRepastS_SimulationRunGroupContext();
        }

        try {
            setupCachedMessageExchangeTemplate(MessagingUtilities
                    .createCachedMessageExchangeTemplate());
        } catch (final JDOMException e) {
            throw new CabsfInitializationRuntimeException(
                    "Failed to initialize the Common Agent-Based Simulation Framework in the Repast simulation agent",
                    e);
        } catch (final IOException e) {
            throw new CabsfInitializationRuntimeException(
                    "Failed to initialize the Common Agent-Based Simulation Framework in the Repast simulation agent",
                    e);
        }

        return CABSF_SIMULATION_DISTRIBUATION_TYPE.DISTRIBUTED;
    }

    /**
     * Reads the framework message from distributed system. Call this method
     * after sending the message to the corresponding distributed agent model.
     * <p/>
     * See the custom send method in the helper class of one of the reference
     * simulations to see how to build the same message.
     *
     * @return the framework message from the distributed system.
     */
    public FrameworkMessage readFrameworkMessageFromDistributedSystem() {
        return getRepastS_SimulationRunContext()
                .readFrameworkMessageFromDistributedSystem();
    }

    // TODO: Remove and replace with reflection in the unit tests.
    /**
     * Only used for unit testing, for setting a private variable. Sets a flag
     * to bypass the RepastS runtime.
     *
     * @param setBypassRepastSruntimeForTesting
     *            the new bypass repast runtime for testing purposes
     * @deprecated
     */
    @Deprecated
    public void testOnly_setBypassRepastSruntime(
            final Boolean setBypassRepastSruntimeForTesting) {
        this.setBypassRepastSruntimeForTesting = setBypassRepastSruntimeForTesting;
    }

    // TODO: Remove and replace with reflection in the unit tests.
    /**
     * Only used for unit testing, for setting a private variable.. Sets the
     * repast s_ simulation run group context.
     *
     * @param repastS_SimulationRunGroupContext_CABSF
     *            the new repast s_ simulation run group context
     * @deprecated
     */
    @Deprecated
    public void testOnly_setRepastS_SimulationRunGroupContext(
            final RepastS_SimulationRunGroupContext_CABSF repastS_SimulationRunGroupContext_CABSF) {
        this.repastS_SimulationRunGroupContext_CABSF = repastS_SimulationRunGroupContext_CABSF;
    }
}
