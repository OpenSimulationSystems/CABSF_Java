package org.simulationsystems.simulationframework.simulation.adapters.api;

import java.util.HashMap;

import org.simulationsystems.simulationframework.simulation.adapters.api.distributedagents.CommonFrameworkDistributedAgentManager;
import org.simulationsystems.simulationframework.simulation.adapters.api.distributedagents.CommonFrameworkDistributedAgentManager.CONFIGURATION_KEYS;

public class SimulationConfiguration {
	private SimulationRunGroup simulationRunGroup;
	
	public SimulationConfiguration(SimulationFrameworkContext simulationFrameworkContext) {

		//TODO: Read the actual values from the configuration file
		simulationRunGroup = new SimulationRunGroup("12345","1.0","1.0");
		simulationFrameworkContext.setSimulationRunGroup(simulationRunGroup);

		// Create AgentMapping objects based on the configured type and number
		// of agents.
		// These objects will be populated with actual mapped simulation-side
		// and
		// distributed-agent-side data.
		// Mocking data for now;
		CommonFrameworkDistributedAgentManager agentManager = simulationFrameworkContext
				.getCommonFrameworkDistributedAgentManager();
		agentManager.createAgentMapping("jzombies.Human", "jade.Agent");
		agentManager.createAgentMapping("jzombies.Human", "jade.Agent");
		agentManager.createAgentMapping("jzombies.Human", "jade.Agent");
		agentManager.createAgentMapping("jzombies.Human", "jade.Agent");
		agentManager.createAgentMapping("jzombies.Human", "jade.Agent");
	}

	/*
	 * Checks whether this agent belongs to a class that is expected to be
	 * distributed outside of the simulation runtime environment.
	 */
	public boolean isAgentClassDistributedType(Class agentClass) {
		// TODO: Tie this to the simulation configuration

		if (agentClass.toString() == "jzombies.Human")
			return true;
		else
			return false;

	}
}
