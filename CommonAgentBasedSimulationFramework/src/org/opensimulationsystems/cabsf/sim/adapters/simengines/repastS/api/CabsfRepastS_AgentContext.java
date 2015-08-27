package org.opensimulationsystems.cabsf.sim.adapters.simengines.repastS.api;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.jdom2.JDOMException;
import org.opensimulationsystems.cabsf.common.internal.messaging.MessagingUtilities;
import org.opensimulationsystems.cabsf.common.model.CABSF_SIMULATION_DISTRIBUATION_TYPE;
import org.opensimulationsystems.cabsf.common.model.cabsfexceptions.CabsfInitializationRuntimeException;
import org.opensimulationsystems.cabsf.common.model.context.AgentContext;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FrameworkMessage;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.environment.RunState;

// TODO: Auto-generated Javadoc
/**
 * The Repast Simphony Agent Context.
 *
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class CabsfRepastS_AgentContext extends AgentContext {

    /** The repast s_ simulation run context. */
    private RepastS_SimulationRunContext repastS_SimulationRunContext = null;

    /** The repast s_ simulation run group context. */
    private RepastS_SimulationRunGroupContext repastS_SimulationRunGroupContext;

    /** The bypass repast runtime for testing purposes. */
    private Boolean bypassRepastRuntimeForTestingPurposes = false;

    /**
     * Instantiates a new repast s_ agent context.
     */
    public CabsfRepastS_AgentContext() {

    }

    /**
     * Gets the repast s_ simulation run context.
     *
     * @return the repast s_ simulation run context
     */
    public RepastS_SimulationRunContext getRepastS_SimulationRunContext() {
        return repastS_SimulationRunContext;
    }

    /**
     * Gets the repast s_ simulation run group context.
     *
     * @return the repast s_ simulation run group context
     */
    public RepastS_SimulationRunGroupContext getRepastS_SimulationRunGroupContext() {
        return repastS_SimulationRunGroupContext;
    }

    /**
     * Initializes the RepastS agent. Returns
     * CABSF_SIMULATION_DISTRIBUATION_TYPE to identify whether this is part of a
     * CABSF simulation or regular simulation. If it is part of the CABSF
     * simulation, it checks whether initialization has occurred. if not, it
     * initializes the simulation for this agent.
     *
     * @param nativeRepastScontext
     *            the native repast scontext
     * @return the simulation type
     *
     * @throws CabsfInitializationRuntimeException
     *             Signals that an initialization error occurred.
     */
    public CABSF_SIMULATION_DISTRIBUATION_TYPE initializeCabsfAgent(
            final Context nativeRepastScontext,
            final CabsfRepastS_AgentContext cabsfRepastS_AgentContext) {
        final Iterable<Class> simulationAgentsClasses2 = RunState.getInstance()
                .getMasterContext().getAgentTypes();

        final Iterable<Class> simulationAgentsClasses = (nativeRepastScontext
                .getAgentTypes());
        final Iterable<Object> cabsfRepastContextIterable = nativeRepastScontext
                .getAgentLayer(RepastS_SimulationRunContext.class);

        // Get a hold of the REpast Simulation Run Context. The agent authors
        // only use
        // this API class.
        if (!bypassRepastRuntimeForTestingPurposes
                && repastS_SimulationRunContext == null) {
            @SuppressWarnings("unchecked")
            RepastS_SimulationRunContext repastS_SimulationRunContext = null;

            // Look to see if the Repast Simulation Context has been created
            // If not that means that this is not a CABSF simulation run
            try {
                repastS_SimulationRunContext = (RepastS_SimulationRunContext) cabsfRepastContextIterable
                        .iterator().next();
            } catch (final NoSuchElementException e) {
                if (RunEnvironment.getInstance().isBatch()) {
                    return CABSF_SIMULATION_DISTRIBUATION_TYPE.NON_DISTRIBUTED;
                }
            }
            this.repastS_SimulationRunContext = repastS_SimulationRunContext;
            this.repastS_SimulationRunGroupContext = repastS_SimulationRunContext
                    .getRepastS_SimulationRunGroupContext();
        }

        try {
            setCachedMessageExchangeTemplateWithPlaceholders(MessagingUtilities
                    .createCachedMessageExchangeTemplateWithPlaceholders());
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

    public FrameworkMessage readFrameworkMessageFromDistributedSystem() {
        return getRepastS_SimulationRunContext()
                .readFrameworkMessageFromDistributedSystem();
    }

    /**
     * Sets the bypass repast runtime for testing purposes.
     *
     * @param bypassRepastRuntimeForTestingPurposes
     *            the new bypass repast runtime for testing purposes
     */
    public void setBypassRepastRuntimeForTestingPurposes(
            final Boolean bypassRepastRuntimeForTestingPurposes) {
        this.bypassRepastRuntimeForTestingPurposes = bypassRepastRuntimeForTestingPurposes;
    }

    /**
     * Sets the repast s_ simulation run context.
     *
     * @param repastS_SimulationRunContext
     *            the new repast s_ simulation run context
     */
    public void setRepastS_SimulationRunContext(
            final RepastS_SimulationRunContext repastS_SimulationRunContext) {
        this.repastS_SimulationRunContext = repastS_SimulationRunContext;
    }

    /**
     * Sets the repast s_ simulation run group context.
     *
     * @param repastS_SimulationRunGroupContext
     *            the new repast s_ simulation run group context
     */
    public void setRepastS_SimulationRunGroupContext(
            final RepastS_SimulationRunGroupContext repastS_SimulationRunGroupContext) {
        this.repastS_SimulationRunGroupContext = repastS_SimulationRunGroupContext;
    }
}
