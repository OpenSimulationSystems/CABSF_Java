package org.simulationsystems.csf.distsys.api;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.simulationsystems.csf.common.csfmodel.SimulationRunGroup;
import org.simulationsystems.csf.common.internal.systems.DistributedSystem;
import org.simulationsystems.csf.distsys.api.configuration.DistributedSystemSimulationRunConfiguration;
import org.simulationsystems.csf.distsys.api.configuration.DistributedSystemSimulationRunGroupConfiguration;
import org.simulationsystems.csf.distsys.api.simulationruntime.SimulationEngineManager;
import org.simulationsystems.csf.sim.api.configuration.SimulationRunConfiguration;
import org.simulationsystems.csf.sim.api.configuration.SimulationRunGroupConfiguration;
import org.simulationsystems.csf.sim.api.distributedsystems.SimulationDistributedSystemManager;

public class DistributedSystemInitializationHelper {
	private DistributedSystemAPI distributedSystemAPI;

	/*
	 * Use the other Constructor instead
	 */
	@SuppressWarnings("unused")
	private DistributedSystemInitializationHelper() {
	}

	public DistributedSystemInitializationHelper(DistributedSystemAPI distributedSystemAPI) {
		this.distributedSystemAPI = distributedSystemAPI;
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
	 * protected DistributedSystemSimulationRunContext initializeAPI( String
	 * frameworkConfigurationFileNameName, String fullyQualifiedClassNameForDistributedAgentManager)
	 */
	protected DistributedSystemSimulationRunGroupContext initializeAPI(
			String frameworkConfigurationFileNameName)

	throws IOException {

		// Process the configuration properties (creating the not yet populated
		// DistributedSystemSimulationRunContext simFrameworkContext = new
		// DistributedSystemSimulationRunContext(fullyQualifiedClassNameForDistributedAgentManager);
		DistributedSystemSimulationRunGroupContext distributedSystemSimulationRunGroupContext = new DistributedSystemSimulationRunGroupContext();
		DistributedSystemSimulationRunGroupConfiguration config = processFrameworkConfigurationProperties(
				frameworkConfigurationFileNameName, distributedSystemSimulationRunGroupContext);
		distributedSystemSimulationRunGroupContext.setSimulationConfiguration(config);

		return distributedSystemSimulationRunGroupContext;
	}

	protected DistributedSystemSimulationRunContext initializeSimulationRun(
			Object simulationSideContext,
			DistributedSystemSimulationRunGroupContext distributedSystemSimulationRunGroupContext) {
		// TODO: Hook in the configuration to get the actual values
		DistributedSystemSimulationRunContext distributedSystemSimulationRunContext = new DistributedSystemSimulationRunContext(
				distributedSystemSimulationRunGroupContext.getSimulationRunGroup());
		distributedSystemSimulationRunGroupContext
				.setSimulationRunContext(distributedSystemSimulationRunContext);

		// Configuration
		DistributedSystemSimulationRunConfiguration distributedSystemSimulationRunConfiguration = new DistributedSystemSimulationRunConfiguration();
		distributedSystemSimulationRunContext
				.setSimulationRunConfiguration(distributedSystemSimulationRunConfiguration);

		// Distributed Agents
		// LOW: Fix later to handle multiple distributed systems
		// LOW: Read optional simulation runtime id from the configuration
		SimulationEngineManager simRuntime = new SimulationEngineManager(null);
		distributedSystemSimulationRunContext.setSimulationEngine(simRuntime);

		return distributedSystemSimulationRunContext;
	}

	/*
	 * Reads the Common Simulation Framework Configuration File. Creates the AgentMapping objects,
	 * with the actual mappings added in later.
	 */
	private DistributedSystemSimulationRunGroupConfiguration processFrameworkConfigurationProperties(
			String frameworkConfigurationFileNameName,
			DistributedSystemSimulationRunGroupContext distributedSystemSimulationRunGroupContext)
			throws IOException {
		/*
		 * FileInputStream fstream = new FileInputStream("textfile.txt"); // Get the object of
		 * DataInputStream DataInputStream in = new DataInputStream(fstream); BufferedReader br =
		 * new BufferedReader(new InputStreamReader(in)); String strLine; // Read File Line By Line
		 * while ((strLine = br.readLine()) != null) { // Print the content on the console
		 * System.out.println(strLine); }
		 */

		DistributedSystemSimulationRunGroupConfiguration config = new DistributedSystemSimulationRunGroupConfiguration();

		// TODO: Retrieve the Simulation Run Group level configuration and use those values here
		// TODO: Comparison against actual simulation settings on the simulation side. Assume
		// simulation starts first and that all values match.
		SimulationRunGroup simulationRunGroup = new SimulationRunGroup("12345", "1.0", "1.0");
		simulationRunGroup.setSimulationFrameworkOptions("MonteCarlo_TestGroupA", null, null);
		distributedSystemSimulationRunGroupContext.setSimulationRunGroup(simulationRunGroup);

		return config;
	}

}
