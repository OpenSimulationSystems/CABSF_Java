package org.simulationsystems.csf.sim.core.api;

/*
 * import java.lang.reflect.Constructor; import java.lang.reflect.InvocationTargetException; import
 * java.lang.reflect.Method;
 */

import java.util.HashSet;
import java.util.Set;

import org.simulationsystems.csf.common.csfmodel.SimulationRunGroup;
import org.simulationsystems.csf.common.csfmodel.context.CsfRunContext;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.sim.core.api.configuration.SimulationRunConfiguration;
import org.simulationsystems.csf.sim.core.api.distributedsystems.SimulationDistributedSystemManager;

/**
 * Provides the context for the Common Simulation Framework.
 * 
 * Adapter developers should instantiate this class first before the
 * Simulation-Toolkit-specific Context object.
 * 
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class SimulationRunContext extends CsfRunContext {
	// protected SimulationRunGroupConfiguration simulationRunGroupConfiguration; //
	// Simulation
	// Run-group-wide
	/** The simulation distributed system managers. */
	private final Set<SimulationDistributedSystemManager> simulationDistributedSystemManagers = new HashSet<SimulationDistributedSystemManager>();

	/** The simulation run group. */
	private final SimulationRunGroup simulationRunGroup;

	/** The simulation tool context. */
	private Object simulationToolContext;

	/** The simulation run configuration. */
	private SimulationRunConfiguration simulationRunConfiguration;

	/** The simulation run group context. */
	private final SimulationRunGroupContext simulationRunGroupContext;

	/**
	 * Instantiates a new simulation run context.
	 * 
	 * @param simulationRunGroup
	 *            the simulation run group
	 * @param simulationRunGroupContext
	 *            the simulation run group context
	 */
	protected SimulationRunContext(final SimulationRunGroup simulationRunGroup,
			final SimulationRunGroupContext simulationRunGroupContext) {
		this.simulationRunGroup = simulationRunGroup;
		this.simulationRunGroupContext = simulationRunGroupContext;
	}

	/**
	 * Adds the simulation distributed system manager.
	 * 
	 * @param simulationDistributedSystemManager
	 *            the simulation distributed system manager
	 */
	public void addSimulationDistributedSystemManager(
			final SimulationDistributedSystemManager simulationDistributedSystemManager) {
		simulationDistributedSystemManagers.add(simulationDistributedSystemManager);

	}

	/**
	 * Cleanup.
	 */
	public void cleanup() {

	}

	/**
	 * Close interface.
	 */
	public void closeInterface() {
		getSimulationDistributedSystemManagers().iterator().next().closeInterface();
	}

	/**
	 * Gets the simulation distributed system managers.
	 * 
	 * @return the simulation distributed system managers
	 */
	public Set<SimulationDistributedSystemManager> getSimulationDistributedSystemManagers() {
		return simulationDistributedSystemManagers;
	}

	/**
	 * Gets the simulation run configuration.
	 * 
	 * @return the simulation run configuration
	 */
	public SimulationRunConfiguration getSimulationRunConfiguration() {
		return simulationRunConfiguration;
	}

	/**
	 * Gets the simulation run group.
	 * 
	 * @return the simulation run group
	 */
	public SimulationRunGroup getSimulationRunGroup() {
		return simulationRunGroup;
	}

	/**
	 * Gets the simulation run group context.
	 * 
	 * @return the simulation run group context
	 */
	public SimulationRunGroupContext getSimulationRunGroupContext() {
		return simulationRunGroupContext;
	}

	/**
	 * Convenience method to return the context of the Simulation Software. For example,
	 * for Repast Simphony, this method would return a repast.simphony.context.Context<T>
	 * 
	 * @return the simulation tool context
	 */
	public Object getSimulationToolContext() {
		return simulationToolContext;
	}

	/**
	 * After the Simulation and Common Framework are initialized, the Simulation Adaptor
	 * API (or child class) is initialized, and prior to executing a simulation run, this
	 * method must be called to configure the simulation-side of the AgentMappings for one
	 * type (class) of simulation agent. If multiple agent classes are distributed, this
	 * method must be called for each type. This is done prior to the distributed
	 * agent-side mappings.
	 * 
	 * Use this method to send a single Simulation Agent object.
	 * 
	 * @param simulationAgent
	 *            the simulation agent
	 */
	public void mapSimulationSideAgent(final Object simulationAgent) {
		mapSimulationSideAgent(simulationAgent);
	}

	/*
	 * @see mapSimulationSideAgent
	 */
	/**
	 * Map simulation side agents.
	 * 
	 * @param agentsOfOneType
	 *            the agents of one type
	 */
	public void mapSimulationSideAgents(final Iterable<Object> agentsOfOneType) {
		for (final Object simulationAgent : agentsOfOneType) {
			mapSimulationSideAgent(simulationAgent);
		}
	}

	/**
	 * Message distributed systems.
	 * 
	 * @param frameworkMessage
	 *            the framework message
	 */
	public void messageDistributedSystems(final FrameworkMessage frameworkMessage) {
		// LOW: Fix for handling multiple distributed systems, Loop through all systems
		final SimulationDistributedSystemManager mgr = getSimulationDistributedSystemManagers()
				.iterator().next();

		mgr.messageDistributedAgents(frameworkMessage, this);

	}

	/**
	 * Read framework message from distributed system.
	 * 
	 * @return the framework message
	 */
	public FrameworkMessage readFrameworkMessageFromDistributedSystem() {
		// LOW: Fix for handling multiple distributed systems, Loop through all systems
		final SimulationDistributedSystemManager mgr = getSimulationDistributedSystemManagers()
				.iterator().next();

		return mgr.readFrameworkMessageFromDistributedSystem();

	}

	/**
	 * Read framework message from simulation administrator.
	 * 
	 * @return the framework message
	 */
	public FrameworkMessage readFrameworkMessageFromSimulationAdministrator() {
		// LOW: Fix for handling multiple distributed systems, Loop through all systems
		final SimulationDistributedSystemManager mgr = getSimulationDistributedSystemManagers()
				.iterator().next();

		return mgr.readFrameworkMessageFromSimulationAdministrator();

	}

	/**
	 * Sets the simulation run configuration.
	 * 
	 * @param simulationRunConfiguration
	 *            the new simulation run configuration
	 */
	protected void setSimulationRunConfiguration(
			final SimulationRunConfiguration simulationRunConfiguration) {
		this.simulationRunConfiguration = simulationRunConfiguration;
	}

	/**
	 * Sets the simulation tool context.
	 * 
	 * @param simulationToolContext
	 *            the new simulation tool context
	 */
	protected void setSimulationToolContext(final Object simulationToolContext) {
		this.simulationToolContext = simulationToolContext;
	}

}
