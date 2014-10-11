package org.simulationsystems.simulationframework.simulation.adapters.api;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.simulationsystems.simulationframework.simulation.adapters.api.distributedagents.AgentMapping;
import org.simulationsystems.simulationframework.simulation.adapters.api.distributedagents.CommonFrameworkDistributedAgentManager;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunState;

public class SimulationAdapterInitializationHelper {
	private SimulationAdapterAPI simulationAdapterAPI;

	// Disabled
	private SimulationAdapterInitializationHelper() {
	}

	public SimulationAdapterInitializationHelper(SimulationAdapterAPI simulationAdapterAPI) {
		this.simulationAdapterAPI = simulationAdapterAPI;
	}

	/*
	 * Initializes the API objects for the simulation adaptor client(s). Reads the
	 * properties from the configuration file, and prepares AgentMapping objects, which
	 * will be populated later once the distributed agents join the simulation.
	 * 
	 * It is expected that this API is only called once before any simulation runs in a
	 * simulation group are executed. All simulation runs within a simulation run group
	 * contain the same agents, both on the simulation-side and distributed-agent-side. If
	 * a different number or type of agents are needed, this should be set up as a
	 * separate simulation run group.
	 * 
	 * Throws exception for error reading the Common Simulation Framework configuration
	 * file.
	 */
	protected void initializeAPI(String frameworkConfigurationFileNameName)
			throws IOException {

		// Process the configuration properties (creating the not yet populated
		// AgentMapping objects), and store the configuration in the SimulationAdapterAPI.
		simulationAdapterAPI
				.setSimulationConfiguration(processFrameworkConfigurationProperties(frameworkConfigurationFileNameName));
	}

	/*
	 * creates the mappings between the Simulation-side agents and distributed agents.
	 */
	protected void initializeSimulationRun() {
	}

	/*
	 * Reads the Common Simulation Framework Configuration File. Creates the AgentMapping
	 * objects, with the actual mappings added in later.
	 */
	private SimulationConfiguration processFrameworkConfigurationProperties(
			String frameworkConfigurationFileNameName) throws IOException {
		/*
		 * FileInputStream fstream = new FileInputStream("textfile.txt"); // Get the
		 * object of DataInputStream DataInputStream in = new DataInputStream(fstream);
		 * BufferedReader br = new BufferedReader(new InputStreamReader(in)); String
		 * strLine; // Read File Line By Line while ((strLine = br.readLine()) != null) {
		 * // Print the content on the console System.out.println(strLine); }
		 */

		return new SimulationConfiguration(simulationAdapterAPI);
	}

	private void mapSimulationAgentsToDistributedAgents() {
		CommonFrameworkDistributedAgentManager mgr = simulationAdapterAPI
				.getCommonFrameworkDistributedAgentManager();

		// Loop through the Agent mappings from the configuration file
		// TODO Auto-generated constructor stub
		// List<Object> simulationObjects = getSimulationAgents();

		//AgentMapping am = mgr.createAgentMapping(fullyQualifiedSimulationAgent);
	}

	private List<Object> getSimulationAgents() {
		return new ArrayList();
	}

	public void configureAgentMapping(Class item, Object simulationAgent) {
		// TODO Auto-generated method stub
		
	}
}
