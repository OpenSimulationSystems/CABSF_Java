package org.opensimulationsystems.cabsf.sim.adapters.simengines.repastS.api;

import org.jdom2.Document;
import org.opensimulationsystems.cabsf.sim.core.api.SimulationRunGroupContext;

/**
 * The simulation run group context for Repast Simphony
 * 
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class RepastS_SimulationRunGroupContext {

	/** The simulation run group context. */
	private final SimulationRunGroupContext simulationRunGroupContext;

	/**
	 * Instantiates a new repast s_ simulation run group context.
	 * 
	 * @param simulationRunGroupContext
	 *            the simulation run group context
	 */
	public RepastS_SimulationRunGroupContext(
			final SimulationRunGroupContext simulationRunGroupContext) {
		this.simulationRunGroupContext = simulationRunGroupContext;
	}

	// TODO: This can probably be removed now that we've added the abstract AgentContext
	/**
	 * Gets the cached message exchange template.
	 * 
	 * @return the cached message exchange template
	 */
	public Document getCachedMessageExchangeTemplate() {
		return this.simulationRunGroupContext.getBlankCachedMessageExchangeTemplate();
	}

	/**
	 * Gets the simulation run group context.
	 * 
	 * @return the simulation run group context
	 */
	public SimulationRunGroupContext getSimulationRunGroupContext() {
		return simulationRunGroupContext;
	}

}
