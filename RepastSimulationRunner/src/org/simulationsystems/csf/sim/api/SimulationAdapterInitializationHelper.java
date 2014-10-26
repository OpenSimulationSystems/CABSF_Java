package org.simulationsystems.csf.sim.api;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.simulationsystems.csf.sim.api.configuration.SimulationRunConfiguration;
import org.simulationsystems.csf.sim.api.configuration.SimulationRunGroupConfiguration;
import org.simulationsystems.csf.sim.api.distributedagents.SimulationDistributedAgentManager;

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
	/*
	 * protected SimulationRunContext initializeAPI( String frameworkConfigurationFileNameName,
	 * String fullyQualifiedClassNameForDistributedAgentManager)
	 */
	protected SimulationRunGroupContext initializeAPI(String frameworkConfigurationFileNameName)

	throws IOException {

		// Process the configuration properties (creating the not yet populated
		// SimulationRunContext simFrameworkContext = new
		// SimulationRunContext(fullyQualifiedClassNameForDistributedAgentManager);
		SimulationRunGroupContext simulationRunGroupContext = new SimulationRunGroupContext();
		SimulationRunGroupConfiguration config = processFrameworkConfigurationProperties(
				frameworkConfigurationFileNameName, simulationRunGroupContext);
		simulationRunGroupContext.setSimulationConfiguration(config);

		return simulationRunGroupContext;
	}

	protected SimulationRunContext initializeSimulationRun(Object simulationSideContext,
			SimulationRunGroupContext simulationRunGroupContext) {
		// TODO: Hook in the configuration to get the actual values
		SimulationRunContext simulationRunContext = new SimulationRunContext(
				simulationRunGroupContext.getSimulationRunGroup());
		simulationRunGroupContext.setSimulationRunGroupContext(simulationRunContext);

		//Configuration
		SimulationRunConfiguration simulationRunConfiguration = new SimulationRunConfiguration();
		simulationRunContext.setSimulationRunConfiguration(simulationRunConfiguration);
		
		//Distributed Agents
		SimulationDistributedAgentManager simulationDistributedAgentManager = new SimulationDistributedAgentManager(
				simulationRunContext, simulationRunConfiguration.getSimulationDistributedAgentMessagingManagerStr());
		simulationRunContext
				.setSimulationDistributedAgentManager(simulationDistributedAgentManager);
		
		//
		
		return simulationRunContext;
	}

	/*
	 * Reads the Common Simulation Framework Configuration File. Creates the AgentMapping objects,
	 * with the actual mappings added in later.
	 */
	private SimulationRunGroupConfiguration processFrameworkConfigurationProperties(
			String frameworkConfigurationFileNameName,
			SimulationRunGroupContext simulationRunGroupContext) throws IOException {
		/*
		 * FileInputStream fstream = new FileInputStream("textfile.txt"); // Get the object of
		 * DataInputStream DataInputStream in = new DataInputStream(fstream); BufferedReader br =
		 * new BufferedReader(new InputStreamReader(in)); String strLine; // Read File Line By Line
		 * while ((strLine = br.readLine()) != null) { // Print the content on the console
		 * System.out.println(strLine); }
		 */
		SimulationRunGroupConfiguration config = new SimulationRunGroupConfiguration(
				simulationRunGroupContext);

		// TODO: Retrieve the Simulation Run Group level configuration and use those values here:
		SimulationRunGroup simulationRunGroup = new SimulationRunGroup("12345", "1.0", "1.0");
		simulationRunGroup.setSimulationFrameworkOptions("MonteCarlo_TestGroupA", null, null);
		simulationRunGroupContext.setSimulationRunGroup(simulationRunGroup);

		return config;
	}

	/*
	 * This method is caused to assign an existing AgentMapping object to a simulation-side agent.
	 */
	public void mapSimulationSideAgent(Object simulationAgent,
			SimulationRunContext simulationRunContext) {
		simulationRunContext.getSimulationDistributedAgentManager()
				.addSimulationAgentToAgentMapping(simulationAgent);

	}
}
