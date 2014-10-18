package org.simulationsystems.simulationframework.simulation.adapters.simulationapps.api;

import org.simulationsystems.simulationframework.simulation.adapters.api.SimulationConfiguration;
import org.simulationsystems.simulationframework.simulation.adapters.api.SimulationFrameworkContext;
import org.simulationsystems.simulationframework.simulation.adapters.api.SimulationRunGroup;
import org.simulationsystems.simulationframework.simulation.adapters.api.distributedagents.SimulationDistributedAgentManager;

import repast.simphony.context.Context;

/*
 * Provides the context for the Common Simulation Framework.  This Simulation-Toolkit-specific context mirrors the generic SimulationFrameworkContext provided by the Common Framework API.  It enables API users to get native Simulation-Toolkit objects instead of generic "Object"s.  This aids the API client at compile time.
 * The simulation context is created for the entire simulation run group, unlike in Repast where the simulation context exists per simulation run.
 * Adapter developers should first instantiate SimulationFrameworkContext, before instantiating a Simulation-Toolkit-specific Context such as this class.
 */
public class RepastSimphonySimulationFrameworkContext {
	private SimulationFrameworkContext simulationFrameworkContext;
	Context<Object> currentRepastContext;

	/*
	 * Use the other constructor
	 */
	@SuppressWarnings("unused")
	private RepastSimphonySimulationFrameworkContext() {

	}

	public RepastSimphonySimulationFrameworkContext(
			SimulationFrameworkContext simulationFrameworkContext) {
		this.simulationFrameworkContext = simulationFrameworkContext;
	}

	protected SimulationFrameworkContext getCurrentSimulationFrameworkContext() {
		return simulationFrameworkContext;
	}

	/*
	 * Returns the Simulation Distributed Agent Manager.
	 */
	//FIXME: 
	public SimulationDistributedAgentManager getSimulationDistributedAgentManager() {
		SimulationDistributedAgentManager dam = simulationFrameworkContext.getCurrentSimulationDistributedAgentManager();
		return dam;
	}

	public SimulationConfiguration getSimulationConfiguration() {
		return simulationFrameworkContext.getSimulationConfiguration();
	}

	public SimulationRunGroup getSimulationRunGroup() {
		return simulationFrameworkContext.getSimulationRunGroup();
	}
	
	/*
	 * 
	 */
	/*
	 * LOW:  Add the ability to support many simultaneous "Context"s
	 */
	public void setCurrentRepastContextForThisRun(Context<Object> repastContextForThisRun) {
		this.currentRepastContext = repastContextForThisRun;
		
	}

	public Context<Object> getCurrentRepastContext() {
		return currentRepastContext;
	}

}
