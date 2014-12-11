package org.simulationsystems.csf.sim.engines.adapters.repastS.api;

import org.simulationsystems.csf.common.csfmodel.SIMULATION_TYPE;

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
	 * 
	 * Returns SIMULATION_TYPE to identify whether this is part of a CSF simulation or
	 * regular simulation. If part of the CSF simulation, it checks whether initialization
	 * has occured. if not, it initializes the simulation for this agent.
	 */
	public SIMULATION_TYPE initializeCsfAgent() {
		// Get a hold of the REpast Simulation Run Context. The agent authors only use
		// this API class.
		if (repastS_SimulationRunContext == null) {
			@SuppressWarnings("unchecked")
			Context<Object> repastContext = RunState.getInstance().getMasterContext();
			Iterable<Class> simulationAgentsClasses = repastContext.getAgentTypes();
			// for (Class clazz : simulationAgentsClasses )
			Iterable<Object> csfRepastContextIterable = RunState.getInstance()
					.getMasterContext().getAgentLayer(RepastS_SimulationRunContext.class);
			RepastS_SimulationRunContext repastS_SimulationRunContext = null;

			// Look to see if the Repast Simulation Context has been created
			// If not that means that this is not a CSF simulation run
			if (csfRepastContextIterable != null)
				repastS_SimulationRunContext = (RepastS_SimulationRunContext) csfRepastContextIterable
						.iterator().next();
			else
				return SIMULATION_TYPE.NON_CSF_SIMULATION;
			this.repastS_SimulationRunContext = repastS_SimulationRunContext;
		}
		return SIMULATION_TYPE.CSF_SIMULATION;
	}
}
