package org.simulationsystems.csf.sim.engines.adapters.repastS.api;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunState;

public class RepastS_AgentAdapterAPI {
	private static RepastS_AgentAdapterAPI instance = new RepastS_AgentAdapterAPI();

	/*
	 * Use getInstance() instead.
	 */
	private RepastS_AgentAdapterAPI() {
		super();
	}

	
	public static RepastS_AgentAdapterAPI getInstance() {
		return instance;
	}

	public RepastS_AgentContext getAgentContext() {
		return new RepastS_AgentContext();
		
	}

}
