package org.simulationsystems.simulationframework.simulation.adapters.api;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.simulationsystems.simulationframework.simulation.adapters.api.distributedagents.SimulationDistributedAgentManager;

public class SimulationAdapterInitializationHelper {
	private SimulationAdapterAPI simulationAdapterAPI;

	/*
	 * Use the other Constructor instead
	 */
	@SuppressWarnings("unused")
	private SimulationAdapterInitializationHelper() {
	}

	public SimulationAdapterInitializationHelper(SimulationAdapterAPI simulationAdapterAPI) {
		this.simulationAdapterAPI = simulationAdapterAPI;
	}

	/*
	 * Initializes the API objects for the simulation adaptor client(s). Reads the properties from
	 * the configuration file, and prepares AgentMapping objects, which will be populated later once
	 * the distributed agents join the simulation.
	 * 
	 * It is expected that this API is only called once before any simulation runs in a simulation
	 * group are executed. All simulation runs within a simulation run group contain the same
	 * agents, both on the simulation-side and distributed-agent-side. If a different number or type
	 * of agents are needed, this should be set up as a separate simulation run group.
	 * 
	 * Throws exception for error reading the Common Simulation Framework configuration file.
	 */
	protected SimulationFrameworkContext initializeAPI(
			String frameworkConfigurationFileNameName, String fullyQualifiedClassNameForDistributedAgentManager)
			throws IOException {

		// Process the configuration properties (creating the not yet populated
		// AgentMapping objects), and store the configuration in the SimulationAdapterAPI.
		SimulationFrameworkContext csfContext = new SimulationFrameworkContext(fullyQualifiedClassNameForDistributedAgentManager);
		SimulationConfiguration config = processFrameworkConfigurationProperties(
				frameworkConfigurationFileNameName, csfContext);
		csfContext.setSimulationConfiguration(config);
		return csfContext;
	}

	/*
	 * Placeholder for future functionality.  
	 */
	protected void initializeSimulationRun(Object simulationSideContext, SimulationFrameworkContext simulationFrameworkContext) {
	}

	/*
	 * Reads the Common Simulation Framework Configuration File. Creates the AgentMapping objects,
	 * with the actual mappings added in later.
	 */
	private SimulationConfiguration processFrameworkConfigurationProperties(
			String frameworkConfigurationFileNameName, SimulationFrameworkContext simulationFrameworkContext) throws IOException {
		/*
		 * FileInputStream fstream = new FileInputStream("textfile.txt"); // Get the object of
		 * DataInputStream DataInputStream in = new DataInputStream(fstream); BufferedReader br =
		 * new BufferedReader(new InputStreamReader(in)); String strLine; // Read File Line By Line
		 * while ((strLine = br.readLine()) != null) { // Print the content on the console
		 * System.out.println(strLine); }
		 */
		SimulationConfiguration config = new SimulationConfiguration(simulationFrameworkContext);
		return config;
	}

	/*
	 * This method is caused to assign an existing AgentMapping object to a simulation-side agent.
	 */
	public void mapSimulationSideAgent(Object simulationAgent,
			SimulationFrameworkContext simulationFrameworkContext) {
		simulationFrameworkContext.getSimulationDistributedAgentManager()
				.addSimulationAgentToAgentMapping(simulationAgent);

	}
}
