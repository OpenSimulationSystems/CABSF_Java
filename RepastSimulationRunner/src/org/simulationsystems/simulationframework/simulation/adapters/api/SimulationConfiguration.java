package org.simulationsystems.simulationframework.simulation.adapters.api;

import java.util.HashMap;

import org.simulationsystems.simulationframework.simulation.adapters.api.distributedagents.CommonFrameworkDistributedAgentManager;
import org.simulationsystems.simulationframework.simulation.adapters.api.distributedagents.CommonFrameworkDistributedAgentManager.CONFIGURATION_KEYS;

public class SimulationConfiguration {
	private SimulationAdapterAPI simulationAdapterAPI;

	HashMap<CONFIGURATION_KEYS, Object> simulationConfigurationProperties = new HashMap<CONFIGURATION_KEYS, Object>();

	public SimulationConfiguration(SimulationAdapterAPI simulationAdapterAPI) {
		this.simulationAdapterAPI = simulationAdapterAPI;
		this.simulationAdapterAPI.setSimulationConfiguration(this);

		// <String fullyQualifiedSimulationAgentName, String
		// fullyQualifiedDistributedAgentName>
		HashMap<String, String> distributedAgents = new HashMap<String, String>();
		simulationConfigurationProperties.put(
				CONFIGURATION_KEYS.DISTRIBUTED_AGENTS, distributedAgents);

		// Create AgentMapping objects based on the configured type and number
		// of agents.
		// These objects will be populated with actual mapped simulation-side
		// and
		// distributed-agent-side data.
		// Mocking data for now;
		CommonFrameworkDistributedAgentManager agentManager = simulationAdapterAPI
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
