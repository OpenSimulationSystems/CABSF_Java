package org.simulationsystems.simulationframework.simulation.adapters.api;

import org.simulationsystems.simulationframework.simulation.adapters.api.distributedagents.CommonFrameworkDistributedAgentManager;

/*
 * This context contains an instances
 * of a Simulation Run Group, which may contain 1 to many simulation runs.
 */
public class CommonSimulationFrameworkContext {
	protected void setSimulationConfiguration(SimulationConfiguration simulationConfiguration) {
		this.simulationConfiguration = simulationConfiguration;
	}

	protected SimulationConfiguration simulationConfiguration;
	private CommonFrameworkDistributedAgentManager commonFrameworkDistributedAgentManager = new CommonFrameworkDistributedAgentManager(
			this);
	private SimulationRunGroup simulationRunGroup;

	/*
	 * Creates the context for the Common Simulation Framework.
	 */
	protected CommonSimulationFrameworkContext() {
	}

	public CommonFrameworkDistributedAgentManager getCommonFrameworkDistributedAgentManager() {
		return commonFrameworkDistributedAgentManager;
	}

	public SimulationConfiguration getSimulationConfiguration() {
		return simulationConfiguration;
	}

	public SimulationRunGroup getSimulationRunGroup() {
		return simulationRunGroup;
	}

	protected void setSimulationRunGroup(SimulationRunGroup simulationRunGroup) {
		this.simulationRunGroup = simulationRunGroup;
	}

	/*
	 * @see mapSimulationSideAgent
	 */
	public void mapSimulationSideAgents(Class item, Iterable<Object> agentsOfOneType) {
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
}
