package org.simulationsystems.csf.sim.engines.adapters.repastS.api;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunState;

public class RepastS_AgentContext {
	private RepastS_SimulationRunContext repastS_SimulationRunContext = null;

	public RepastS_SimulationRunContext getRepastS_SimulationRunContext() {
		return repastS_SimulationRunContext;
	}

	public RepastS_AgentContext() {

	}

	/*
	 * Called from the agent in Repast to get a hold of the Csf Context. Safe to call it
	 * multiple times.
	 */
	public RepastS_SimulationRunContext initializeCsfAgent() {
		// Get a hold of the REpast Simulation Run Context. The agent authors only use
		// this API class.
		if (repastS_SimulationRunContext == null) {
			@SuppressWarnings("unchecked")
			Context<Object> repastContext = RunState.getInstance().getMasterContext();
			Iterable<Class> simulationAgentsClasses = repastContext.getAgentTypes();
			// for (Class clazz : simulationAgentsClasses )
			Iterable<Object> csfRepastContextIterable = RunState.getInstance()
					.getMasterContext().getAgentLayer(RepastS_SimulationRunContext.class);
			RepastS_SimulationRunContext repastS_SimulationRunContext = (RepastS_SimulationRunContext) csfRepastContextIterable
					.iterator().next();

			this.repastS_SimulationRunContext = repastS_SimulationRunContext;
		}
		return repastS_SimulationRunContext;
	}
}
