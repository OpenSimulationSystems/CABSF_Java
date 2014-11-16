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

import org.jdom2.JDOMException;
import org.simulationsystems.csf.common.csfmodel.CsfSimulationInitializationException;
import org.simulationsystems.csf.common.csfmodel.SimulationRunGroup;
import org.simulationsystems.csf.common.internal.messaging.MessagingUtilities;
import org.simulationsystems.csf.common.internal.systems.DistributedSystem;
import org.simulationsystems.csf.distsys.api.configuration.DistSysRunConfiguration;
import org.simulationsystems.csf.distsys.api.configuration.DistSysRunGroupConfiguration;
import org.simulationsystems.csf.distsys.api.distributedautonomousagents.DistributedAgentsManager;
import org.simulationsystems.csf.distsys.api.distributedautonomousagents.DistributedAutonomousAgent;
import org.simulationsystems.csf.distsys.api.simulationruntime.SimulationEngineManager;
import org.simulationsystems.csf.sim.api.SimulationRunContext;
import org.simulationsystems.csf.sim.api.configuration.SimulationRunConfiguration;
import org.simulationsystems.csf.sim.api.configuration.SimulationRunGroupConfiguration;
import org.simulationsystems.csf.sim.api.distributedsystems.SimulationDistributedSystemManager;

public class DistSysInitializationHelper {
	private DistributedSystemAPI distributedSystemAPI;

	/*
	 * Use the other Constructor instead
	 */
	@SuppressWarnings("unused")
	private DistSysInitializationHelper() {
	}

	public DistSysInitializationHelper(DistributedSystemAPI distributedSystemAPI) {
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
	 * protected DistSysRunContext initializeAPI( String frameworkConfigurationFileNameName, String
	 * fullyQualifiedClassNameForDistributedAgentManager)
	 */
	protected DistSysRunGroupContext initializeAPI(String frameworkConfigurationFileNameName)
			throws IOException {

		// Process the configuration properties (creating the not yet populated
		// DistSysRunContext simFrameworkContext = new
		// DistSysRunContext(fullyQualifiedClassNameForDistributedAgentManager);
		DistSysRunGroupContext distSysRunGroupContext = new DistSysRunGroupContext();
		DistSysRunGroupConfiguration config = processFrameworkConfigurationProperties(
				frameworkConfigurationFileNameName, distSysRunGroupContext);
		distSysRunGroupContext.setDistSysRunGroupConfiguration(config);

		// Cache the message exchange template from the file system
		try {
			distSysRunGroupContext.setCachedMessageExchangeTemplate(MessagingUtilities
					.createCachedMessageExchangeTemplate());
		} catch (JDOMException e) {
			throw new CsfSimulationInitializationException(
					"Error reading the message exchange template from the file system", e);
		}

		return distSysRunGroupContext;
	}

	protected DistSysRunContext initializeSimulationRun(Object simulationSideContext,
			DistSysRunGroupContext distSysRunGroupContext) {
		// TODO: Hook in the configuration to get the actual values
		DistSysRunContext distSysRunContext = new DistSysRunContext(
				distSysRunGroupContext.getSimulationRunGroup());
		distSysRunGroupContext.setDistSysRunContext(distSysRunContext);

		// Configuration
		// TODO: Actually setup run-level configuration?
		DistSysRunConfiguration distSysRunConfiguration = new DistSysRunConfiguration();
		distSysRunContext.setSimulationRunConfiguration(distSysRunConfiguration);

		// Set up the manager for the simulation runtime. Uses reflection to specify the correct
		// interface
		// LOW: Read optional simulation runtime id from the configuration
		SimulationEngineManager simRuntime = new SimulationEngineManager(distSysRunContext,
				distSysRunConfiguration.getCommonMessagingConcreteImplStr());
		distSysRunContext.setSimulationEngine(simRuntime);

		// Set up manager for the distributed autonomous agents (such as the JADE agents) on behalf
		// of the client of this API, such as the CSF JADE Controller Agent
		// TODO: Add this distributed system's id
		DistributedAgentsManager distributedAgentsManager = new DistributedAgentsManager(null,
				null, distSysRunContext.getDistSysRunConfiguration());
		distSysRunContext.setDistributedAgentsManager(distributedAgentsManager);

		return distSysRunContext;
	}

	/*
	 * Reads the Common Simulation Framework Configuration File. Creates the AgentMapping objects,
	 * with the actual mappings added in later.
	 */
	private DistSysRunGroupConfiguration processFrameworkConfigurationProperties(
			String frameworkConfigurationFileNameName, DistSysRunGroupContext distSysRunGroupContext)
			throws IOException {
		/*
		 * FileInputStream fstream = new FileInputStream("textfile.txt"); // Get the object of
		 * DataInputStream DataInputStream in = new DataInputStream(fstream); BufferedReader br =
		 * new BufferedReader(new InputStreamReader(in)); String strLine; // Read File Line By Line
		 * while ((strLine = br.readLine()) != null) { // Print the content on the console
		 * System.out.println(strLine); }
		 */

		DistSysRunGroupConfiguration config = new DistSysRunGroupConfiguration();

		// TODO: Retrieve the Simulation Run Group level configuration and use those values here
		// TODO: Comparison against actual simulation settings on the simulation side. Assume
		// simulation starts first and that all values match.
		SimulationRunGroup simulationRunGroup = new SimulationRunGroup("12345", "1.0", "1.0");
		simulationRunGroup.setSimulationFrameworkOptions("MonteCarlo_TestGroupA", null, null);
		distSysRunGroupContext.setDistributedSystemSimulationRunGroup(simulationRunGroup);

		return config;
	}

	/*
	 * This method is caused to assign an existing AgentMapping object (created at CSF
	 * initialization) to a simulation-side agent.
	 */
	public void assignNativeDistributedAutonomousAgent(Object nativeDistributedAutonomousAgent,
			DistSysRunContext distSysRunContext) {
		// TODO: Handle multiple distributed systems
		distSysRunContext.getDistributedAgentsManager().assignNativeDistributedAutonomousAgent(
				nativeDistributedAutonomousAgent);
	}

}
