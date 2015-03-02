package org.simulationsystems.csf.sim.adapters.simengines.repastS.api;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.jdom2.JDOMException;
import org.simulationsystems.csf.common.csfmodel.SIMULATION_TYPE;
import org.simulationsystems.csf.common.csfmodel.context.AgentContext;
import org.simulationsystems.csf.common.internal.messaging.MessagingUtilities;

/**
 * The Repast Simphony Agent Context.
 * 
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class RepastS_AgentContext extends AgentContext {

	/** The repast s_ simulation run context. */
	private RepastS_SimulationRunContext repastS_SimulationRunContext = null;

	/** The repast s_ simulation run group context. */
	private RepastS_SimulationRunGroupContext repastS_SimulationRunGroupContext;

	/** The bypass repast runtime for testing purposes. */
	private Boolean bypassRepastRuntimeForTestingPurposes = false;

	/**
	 * Instantiates a new repast s_ agent context.
	 */
	public RepastS_AgentContext() {

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
	 * Initializes the RepastS agent. Returns SIMULATION_TYPE to identify whether this is
	 * part of a CSF simulation or regular simulation. If it is part of the CSF
	 * simulation, it checks whether initialization has occurred. if not, it initializes
	 * the simulation for this agent.
	 * 
	 * @param simulationAgentsClasses
	 *            the simulation agents classes
	 * @param csfRepastContextIterable
	 *            the csf repast context iterable
	 * @return the simulation type
	 * @throws JDOMException
	 *             the JDOM exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public SIMULATION_TYPE initializeCsfAgent(
			final Iterable<Class> simulationAgentsClasses,
			final Iterable<Object> csfRepastContextIterable) throws JDOMException,
			IOException {
		// Get a hold of the REpast Simulation Run Context. The agent authors only use
		// this API class.
		if (!bypassRepastRuntimeForTestingPurposes
				&& repastS_SimulationRunContext == null) {
			@SuppressWarnings("unchecked")
			// Iterable<Class> simulationAgentsClasses =
			// RunState.getInstance().getMasterContext().getAgentTypes();
			// Iterable<Object> csfRepastContextIterable =
			// RunState.getInstance().getMasterContext().getAgentLayer(RepastS_SimulationRunContext.class);
			RepastS_SimulationRunContext repastS_SimulationRunContext = null;

			// Look to see if the Repast Simulation Context has been created
			// If not that means that this is not a CSF simulation run
			try {
				repastS_SimulationRunContext = (RepastS_SimulationRunContext) csfRepastContextIterable
						.iterator().next();
			} catch (final NoSuchElementException e) {
				return SIMULATION_TYPE.NON_CSF_SIMULATION;
			}
			this.repastS_SimulationRunContext = repastS_SimulationRunContext;
			this.repastS_SimulationRunGroupContext = repastS_SimulationRunContext
					.getRepastS_SimulationRunGroupContext();
		}

		setCachedMessageExchangeTemplateWithPlaceholders(MessagingUtilities
				.createCachedMessageExchangeTemplateWithPlaceholders());

		return SIMULATION_TYPE.CSF_SIMULATION;
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
