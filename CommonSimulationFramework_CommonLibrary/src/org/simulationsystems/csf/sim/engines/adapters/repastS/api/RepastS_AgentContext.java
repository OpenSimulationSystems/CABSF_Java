package org.simulationsystems.csf.sim.engines.adapters.repastS.api;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.jdom2.JDOMException;
import org.simulationsystems.csf.common.csfmodel.SIMULATION_TYPE;
import org.simulationsystems.csf.common.csfmodel.api.AgentContext;
import org.simulationsystems.csf.common.csfmodel.api.CsfRunGroupContext;
import org.simulationsystems.csf.common.internal.messaging.MessagingUtilities;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunState;

public class RepastS_AgentContext extends AgentContext {
	private RepastS_SimulationRunContext repastS_SimulationRunContext = null;
	private RepastS_SimulationRunGroupContext repastS_SimulationRunGroupContext;
	
	private Boolean bypassRepastRuntimeForTestingPurposes;
	
	public void setBypassRepastRuntimeForTestingPurposes(
			Boolean bypassRepastRuntimeForTestingPurposes) {
		this.bypassRepastRuntimeForTestingPurposes = bypassRepastRuntimeForTestingPurposes;
	}

	public RepastS_SimulationRunContext getRepastS_SimulationRunContext() {
		return repastS_SimulationRunContext;
	}

	public RepastS_AgentContext() {

	}

	/*
	 * Requires the Repast Runtime to be executing
	 *
	 * Called from the agent in Repast to get a hold of the Csf Context. Safe to call it
	 * multiple times.
	 * 
	 * Returns SIMULATION_TYPE to identify whether this is part of a CSF simulation or
	 * regular simulation. If part of the CSF simulation, it checks whether initialization
	 * has occured. if not, it initializes the simulation for this agent.
	 * 
	 */
	public SIMULATION_TYPE initializeCsfAgent() throws JDOMException, IOException {
		// Get a hold of the REpast Simulation Run Context. The agent authors only use
		// this API class.
		if (!bypassRepastRuntimeForTestingPurposes && repastS_SimulationRunContext == null) {
			@SuppressWarnings("unchecked")
			Context<Object> repastContext = RunState.getInstance().getMasterContext();
			Iterable<Class> simulationAgentsClasses = repastContext.getAgentTypes();
			// for (Class clazz : simulationAgentsClasses )
			Iterable<Object> csfRepastContextIterable = RunState.getInstance()
					.getMasterContext().getAgentLayer(RepastS_SimulationRunContext.class);
			RepastS_SimulationRunContext repastS_SimulationRunContext = null;

			// Look to see if the Repast Simulation Context has been created
			// If not that means that this is not a CSF simulation run
			try {
				repastS_SimulationRunContext = (RepastS_SimulationRunContext) csfRepastContextIterable
						.iterator().next();
			}
			catch (NoSuchElementException e ) {
				return SIMULATION_TYPE.NON_CSF_SIMULATION;
			}
			this.repastS_SimulationRunContext = repastS_SimulationRunContext;
			this.repastS_SimulationRunGroupContext = repastS_SimulationRunContext.getRepastS_SimulationRunGroupContext();
		}
		
		setCachedMessageExchangeTemplateWithPlaceholders(MessagingUtilities
				.createCachedMessageExchangeTemplateWithPlaceholders());
		
		return SIMULATION_TYPE.CSF_SIMULATION;
	}

	public void setRepastS_SimulationRunContext(
			RepastS_SimulationRunContext repastS_SimulationRunContext) {
		this.repastS_SimulationRunContext = repastS_SimulationRunContext;
	}

	public void setRepastS_SimulationRunGroupContext(
			RepastS_SimulationRunGroupContext repastS_SimulationRunGroupContext) {
		this.repastS_SimulationRunGroupContext = repastS_SimulationRunGroupContext;
	}

	public RepastS_SimulationRunGroupContext getRepastS_SimulationRunGroupContext() {
		return repastS_SimulationRunGroupContext;
	}
}
