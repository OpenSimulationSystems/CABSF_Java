package org.simulationsystems.csf.sim.api;

/*
 * import java.lang.reflect.Constructor; import java.lang.reflect.InvocationTargetException; import
 * java.lang.reflect.Method;
 */

import java.util.HashSet;
import java.util.Set;

import org.simulationsystems.csf.common.internal.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.sim.api.configuration.SimulationRunConfiguration;
import org.simulationsystems.csf.sim.api.configuration.SimulationRunGroupConfiguration;
import org.simulationsystems.csf.sim.api.distributedsystems.SimulationDistributedSystemManager;

/*
 * Provides the context for the Common Simulation Framework. Adapter developers may use this context
 * directly, but are encouraged to create separate Simulation-Toolkit-specific context (e.g.,
 * org.simulationsystems.csf.sim.adapters.api.RepastSimphonySimulationFrameworkContext). The benefit
 * is that the API client would be able to utilize native Simulation-Toolkit-specific objects
 * instead of the generic "Object" that is used by this generic Simulation Framework API.
 * 
 * Adapter developers should instantiate this class first before the Simulation-Toolkit-specific
 * Context object.
 */
public class SimulationRunContext {
	protected SimulationRunGroupConfiguration simulationRunGroupConfiguration; // Simulation
																				// Run-group-wide
	private Set<SimulationDistributedSystemManager> simulationDistributedSystemManagers = new HashSet<SimulationDistributedSystemManager>();
	private SimulationRunGroup simulationRunGroup;
	private Object simulationToolContext;
	private SimulationRunConfiguration simulationRunConfiguration;

	/*
	 * Convenience method to return the context of the Simulation Software. For example, for Repast
	 * Simphony, this method would return a repast.simphony.context.Context<T>
	 */
	public Object getSimulationToolContext() {
		return simulationToolContext;
	}

	public SimulationRunConfiguration getSimulationRunConfiguration() {
		return simulationRunConfiguration;
	}

	public void setSimulationRunConfiguration(SimulationRunConfiguration simulationRunConfiguration) {
		this.simulationRunConfiguration = simulationRunConfiguration;
	}

	protected void setSimulationToolContext(Object simulationToolContext) {
		this.simulationToolContext = simulationToolContext;
	}

	/*
	 * Creates the context for the Common Simulation Framework.
	 */
	// protected SimulationRunContext(String fullyQualifiedClassNameForDistributedAgentManager) {
	protected SimulationRunContext(SimulationRunGroup simulationRunGroup) {
		this.simulationRunGroup = simulationRunGroup;
	}

	public Set<SimulationDistributedSystemManager> getSimulationDistributedSystemManagers() {
		return simulationDistributedSystemManagers;
	}

	public SimulationRunGroupConfiguration getSimulationRunGroupConfiguration() {
		return simulationRunGroupConfiguration;
	}

	public SimulationRunGroup getSimulationRunGroup() {
		return simulationRunGroup;
	}

	protected void setSimulationConfiguration(
			SimulationRunGroupConfiguration simulationRunGroupConfiguration) {
		this.simulationRunGroupConfiguration = simulationRunGroupConfiguration;
	}

	/*
	 * @see mapSimulationSideAgent
	 */
	public void mapSimulationSideAgents(Iterable<Object> agentsOfOneType) {
		for (Object simulationAgent : agentsOfOneType) {
			mapSimulationSideAgent(simulationAgent);
		}
	}

	/*
	 * After the Simulation and Common Framework are initialized, the Simulation Adaptor API (or
	 * child class) is initialized, and prior to executing a simulation run, this method must be
	 * called to configure the simulation-side of the AgentMappings for one type (class) of
	 * simulation agent. If multiple agent classes are distributed, this method must be called for
	 * each type. This is done prior to the distributed agent-side mappings.
	 * 
	 * Use this method to send a single Simulation Agent object.
	 * 
	 * @see mapSimulationSideAgents
	 */
	public void mapSimulationSideAgent(Object simulationAgent) {
		mapSimulationSideAgent(simulationAgent);
	}

	public void cleanup() {

	}

	public void addSimulationDistributedSystemManager(
			SimulationDistributedSystemManager simulationDistributedSystemManager) {
		simulationDistributedSystemManagers.add(simulationDistributedSystemManager);

	}

	public void messageDistributedSystems(FrameworkMessage frameworkMessage,
			SimulationRunContext simulationRunContext) {
		// TODO: Fix for handling multiple distributed systems, Loop through all systems
		SimulationDistributedSystemManager mgr = getSimulationDistributedSystemManagers()
				.iterator().next();

		mgr.messageDistributedAgents(frameworkMessage, simulationRunContext);

	}
}
