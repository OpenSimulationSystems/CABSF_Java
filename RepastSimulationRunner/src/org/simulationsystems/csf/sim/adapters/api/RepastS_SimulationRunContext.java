package org.simulationsystems.csf.sim.adapters.api;

import org.simulationsystems.csf.common.internal.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.sim.api.SimulationRunContext;
import org.simulationsystems.csf.sim.api.SimulationRunGroup;
import org.simulationsystems.csf.sim.api.SimulationRunGroupContext;
import org.simulationsystems.csf.sim.api.configuration.SimulationRunGroupConfiguration;
import org.simulationsystems.csf.sim.api.distributedagents.SimulationDistributedAgentManager;

import repast.simphony.context.Context;

/*
 * Provides the context for the Common Simulation Framework.  This Simulation-Toolkit-specific context mirrors the generic SimulationRunContext provided by the Common Framework API.  It enables API users to get native Simulation-Toolkit objects instead of generic "Object"s.  This aids the API client at compile time.
 * The simulation context is created for the entire simulation run group, unlike in Repast where the simulation context exists per simulation run.
 * Adapter developers should first instantiate SimulationRunContext, before instantiating a Simulation-Toolkit-specific Context such as this class.
 */
public class RepastS_SimulationRunContext {
	private SimulationRunContext simulationRunContext;
	Context<Object> repastS_ContextForThisRun;

	public SimulationRunContext getSimulationRunContext() {
		return simulationRunContext;
	}

	/*
	 * Use the other constructor
	 */
	@SuppressWarnings("unused")
	private RepastS_SimulationRunContext() {

	}

	public RepastS_SimulationRunContext(SimulationRunContext simulationRunContext) {
		this.simulationRunContext = simulationRunContext;
	}

	public SimulationRunGroupConfiguration getSimulationRunGroupConfiguration() {
		return simulationRunContext.getSimulationRunGroupConfiguration();
	}

	public SimulationRunGroup getSimulationRunGroup() {
		return simulationRunContext.getSimulationRunGroup();
	}

	/*
	 * 
	 */
	/*
	 * LOW: Add the ability to support many simultaneous "Context"s
	 */
	public void setRepastContextForThisRun(Context<Object> repastS_ContextForThisRun) {
		this.repastS_ContextForThisRun = repastS_ContextForThisRun;

	}

	public Context<Object> getCurrentRepastContext() {
		return repastS_ContextForThisRun;
	}

	/*
	 * Returns the Simulation Distributed Agent Manager.
	 */
	// FIXME:
	public SimulationDistributedAgentManager getSimulationDistributedAgentManager() {
		SimulationDistributedAgentManager dam = simulationRunContext.getSimulationDistributedAgentManager();
		return dam;
	}
	

}
