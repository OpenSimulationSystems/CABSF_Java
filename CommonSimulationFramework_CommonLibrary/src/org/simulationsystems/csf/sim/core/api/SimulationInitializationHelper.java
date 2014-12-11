package org.simulationsystems.csf.sim.core.api;

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
import org.simulationsystems.csf.common.csfmodel.SimulationRunGroup;
import org.simulationsystems.csf.common.csfmodel.csfexceptions.CsfInitializationRuntimeException;
import org.simulationsystems.csf.common.internal.messaging.MessagingUtilities;
import org.simulationsystems.csf.common.internal.systems.DistributedSystem;
import org.simulationsystems.csf.sim.core.api.configuration.SimulationRunConfiguration;
import org.simulationsystems.csf.sim.core.api.configuration.SimulationRunGroupConfiguration;
import org.simulationsystems.csf.sim.core.api.distributedsystems.SimulationDistributedSystemManager;

public class SimulationInitializationHelper {
	private SimulationAPI simulationAPI;

	/*
	 * Use the other Constructor instead
	 */
	@SuppressWarnings("unused")
	private SimulationInitializationHelper() {
	}

	public SimulationInitializationHelper(SimulationAPI simulationAPI) {
		this.simulationAPI = simulationAPI;
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
	protected SimulationRunGroupContext initializeAPI(String frameworkConfigurationFileNameName)

	throws IOException {

		// Process the configuration properties (creating the not yet populated
		// DistSysRunContext simFrameworkContext = new
		// DistSysRunContext(fullyQualifiedClassNameForDistributedAgentManager);
		SimulationRunGroupContext simulationRunGroupContext = new SimulationRunGroupContext();
		SimulationRunGroupConfiguration config = processFrameworkConfigurationProperties(
				frameworkConfigurationFileNameName, simulationRunGroupContext);
		simulationRunGroupContext.setSimulationConfiguration(config);
		
		//Cache the message exchange template from the file system
		try {
			simulationRunGroupContext.setCachedMessageExchangeTemplate(MessagingUtilities.createCachedMessageExchangeTemplate());
		} catch (JDOMException e) {
			throw new CsfInitializationRuntimeException(
					"Error reading the message exchange template from the file system", e);
		}
		
		return simulationRunGroupContext;
	}

	protected SimulationRunContext initializeSimulationRun(Object simulationSideContext,
			SimulationRunGroupContext simulationRunGroupContext) {
		// TODO: Hook in the configuration to get the actual values
		SimulationRunContext simulationRunContext = new SimulationRunContext(
				simulationRunGroupContext.getSimulationRunGroup(), simulationRunGroupContext);
		simulationRunGroupContext.setSimulationRunContext(simulationRunContext);

		// Configuration
		SimulationRunConfiguration simulationRunConfiguration = new SimulationRunConfiguration();
		simulationRunContext.setSimulationRunConfiguration(simulationRunConfiguration);

		// Distributed Agents
		// LOW: Fix later to handle multiple distributed systems
		// LOW: Read optional distributed system id from the configuration
		// TODO: Read the actual distributed system id
		DistributedSystem sys = new DistributedSystem(simulationRunContext.getSimulationRunConfiguration().getDistributedSystemID());
		SimulationDistributedSystemManager simulationDistributedSystemManager = new SimulationDistributedSystemManager(
				simulationRunContext,
				simulationRunConfiguration.getCommonMessagingConcreteImplStr(), sys);
		simulationRunContext
				.addSimulationDistributedSystemManager(simulationDistributedSystemManager);

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
		SimulationRunGroupConfiguration config = new SimulationRunGroupConfiguration();

		// TODO: Retrieve the Simulation Run Group level configuration and use those values here:
		SimulationRunGroup simulationRunGroup = new SimulationRunGroup("12345", "1.0", "1.0");
		// TODO: Organizae these two methods better.
		simulationRunGroup.setSimulationFrameworkOptions("MonteCarlo_TestGroupA", null, null);
		simulationRunGroupContext.setSimulationRunGroup(simulationRunGroup);

		return config;
	}

	/*
	 * This method is caused to assign an existing AgentMapping object (created at CSF
	 * initialization) to a simulation-side agent.
	 */
	public void mapSimulationSideAgent(Object simulationAgent,
			SimulationRunContext simulationRunContext) {
		// TODO: Handle multiple distributed systems
		simulationRunContext.getSimulationDistributedSystemManagers().iterator().next()
				.addSimulationAgentToAgentMapping(simulationAgent);

	}

}
