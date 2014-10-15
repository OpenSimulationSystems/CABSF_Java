package org.simulationsystems.simulationframework.simulation.adapters.simulationapps.api;

import org.simulationsystems.simulationframework.simulation.adapters.api.SimulationConfiguration;
import org.simulationsystems.simulationframework.simulation.adapters.api.SimulationFrameworkContext;
import org.simulationsystems.simulationframework.simulation.adapters.api.SimulationRunGroup;
import org.simulationsystems.simulationframework.simulation.adapters.api.distributedagents.SimulationDistributedAgentManager;

/*
 * Provides the context for the Common Simulation Framework.  This Simulation-Toolkit-specific context mirrors the generic SimulationFrameworkContext provided by the Common Framework API.  It enables API users to get native Simulation-Toolkit objects instead of generic "Object"s.  This aids the API client at compile time.
 * 
 * Adapter developers should first instantiate SimulationFrameworkContext, before instantiating a Simulation-Toolkit-specific Context such as this class.
 */
public class SimulationFrameworkContextForRepastSimphony {
	private SimulationFrameworkContext simulationFrameworkContext;

	/*
	 * Use the other constructor
	 */
	@SuppressWarnings("unused")
	private SimulationFrameworkContextForRepastSimphony() {

	}

	public SimulationFrameworkContextForRepastSimphony(
			SimulationFrameworkContext simulationFrameworkContext) {
		this.simulationFrameworkContext = simulationFrameworkContext;
	}
	
	protected SimulationFrameworkContext getSimulationFrameworkContext() {
		return simulationFrameworkContext;
	}

	/*
	 * Returns the Simulation Distributed Agent Manager.  
	 */
	public SimulationDistributedAgentManager getSimulationDistributedAgentManager() {
		return simulationFrameworkContext.getSimulationDistributedAgentManager();
	}

	public SimulationConfiguration getSimulationConfiguration() {
		return simulationFrameworkContext.getSimulationConfiguration();
	}

	public SimulationRunGroup getSimulationRunGroup() {
		return simulationFrameworkContext.getSimulationRunGroup();
	}

	/*
	 * @see mapSimulationSideAgent
	 */
	public void mapSimulationSideAgents(Iterable<Object> agentsOfOneType) {
		for (Object simulationAgent : agentsOfOneType) {
			simulationFrameworkContext.mapSimulationSideAgent(simulationAgent);
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
		simulationFrameworkContext.mapSimulationSideAgent(simulationAgent);
	}

}
