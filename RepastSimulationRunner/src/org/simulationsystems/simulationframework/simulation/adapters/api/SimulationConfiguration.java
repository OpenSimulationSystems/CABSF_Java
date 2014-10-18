package org.simulationsystems.simulationframework.simulation.adapters.api;

import java.util.HashMap;

import org.simulationsystems.simulationframework.simulation.adapters.api.distributedagents.SimulationDistributedAgentManager;
import org.simulationsystems.simulationframework.simulation.adapters.api.distributedagents.SimulationDistributedAgentManager.CONFIGURATION_KEYS;

public class SimulationConfiguration {
	private SimulationRunGroup simulationRunGroup;
	private SimulationFrameworkContext simulationFrameworkContext;

	public SimulationConfiguration(SimulationFrameworkContext simulationFrameworkContext) {
		// TODO: Read the actual values from the configuration file
		this.simulationFrameworkContext = simulationFrameworkContext;
		simulationRunGroup = new SimulationRunGroup("12345", "1.0", "1.0");
		simulationFrameworkContext.setCurrentSimulationRunGroup(simulationRunGroup);
	}

	public void initializeAgentMappings() {
		// Create AgentMapping objects based on the configured type and number
		// of agents.
		// These objects will be populated with actual mapped simulation-side
		// and
		// distributed-agent-side data.
		// Mocking data for now;

		SimulationDistributedAgentManager agentManager = new SimulationDistributedAgentManager(
				simulationFrameworkContext);
		simulationFrameworkContext.setCurrentSimulationDistributedAgentManager(agentManager);
		agentManager.createAgentMapping("jzombies.Human", "jade.Agent");
		agentManager.createAgentMapping("jzombies.Human", "jade.Agent");
		agentManager.createAgentMapping("jzombies.Human", "jade.Agent");
		agentManager.createAgentMapping("jzombies.Human", "jade.Agent");
		agentManager.createAgentMapping("jzombies.Human", "jade.Agent");
	}

	/*
	 * Checks whether this agent belongs to a class that is expected to be distributed outside of
	 * the simulation runtime environment.
	 */
	public boolean isAgentClassDistributedType(Class agentClass) {
		// TODO: Tie this to the simulation configuration

		if (agentClass.getCanonicalName().equals("jzombies.Human"))
			return true;
		else
			return false;

	}
}
