package org.simulationsystems.simulationframework.simulation.adapters.api;

import java.util.HashMap;

import org.simulationsystems.simulationframework.simulation.adapters.api.distributedagents.CommonFrameworkDistributedAgentManager;

public class SimulationConfiguration {
	private SimulationAdapterAPI simulationAdapterAPI;

	HashMap<CONFIGURATION_KEYS, Object> simulationConfigurationProperties = new HashMap<CONFIGURATION_KEYS, Object>();

	public enum CONFIGURATION_KEYS {
		DISTRIBUTED_AGENTS

	}

	public SimulationConfiguration(SimulationAdapterAPI simulationAdapterAPI) {
		this.simulationAdapterAPI = simulationAdapterAPI;

		// <String fullyQualifiedSimulationAgentName, String
		// fullyQualifiedDistributedAgentName>
		HashMap<String, String> distributedAgents = new HashMap<String, String>();
		simulationConfigurationProperties.put(CONFIGURATION_KEYS.DISTRIBUTED_AGENTS,
				distributedAgents);

		// Create AgentMapping objects based on the configured type and number of agents.
		// These objects will be populated with actual mapped simulation-side and
		// distributed-agent-side data.
		// Mocking data for now;
		CommonFrameworkDistributedAgentManager agentManager = simulationAdapterAPI
				.getCommonFrameworkDistributedAgentManager();
		agentManager.createUninitializedAgentMapping("jzombies.Human", "jade.Agent");
		agentManager.createUninitializedAgentMapping("jzombies.Human", "jade.Agent");
		agentManager.createUninitializedAgentMapping("jzombies.Human", "jade.Agent");
		agentManager.createUninitializedAgentMapping("jzombies.Human", "jade.Agent");
		agentManager.createUninitializedAgentMapping("jzombies.Human", "jade.Agent");
	}
}
