package org.simulationsystems.csf.sim.core.api;

import java.io.IOException;

import org.jdom2.JDOMException;
import org.simulationsystems.csf.common.csfmodel.SimulationRunGroup;
import org.simulationsystems.csf.common.csfmodel.csfexceptions.CsfInitializationRuntimeException;
import org.simulationsystems.csf.common.internal.messaging.MessagingUtilities;
import org.simulationsystems.csf.common.internal.systems.DistributedSystem;
import org.simulationsystems.csf.sim.core.api.configuration.SimulationRunConfiguration;
import org.simulationsystems.csf.sim.core.api.configuration.SimulationRunGroupConfiguration;
import org.simulationsystems.csf.sim.core.api.distributedsystems.SimulationDistributedSystemManager;

// TODO: Auto-generated Javadoc
/**
 * The simulation initialization helper class
 * 
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class SimulationInitializationHelper {

	/** The simulation api. */
	private SimulationAPI simulationAPI;

	/**
	 * Disabled constructor
	 */
	@SuppressWarnings("unused")
	private SimulationInitializationHelper() {
	}

	/**
	 * Instantiates a new simulation initialization helper.
	 * 
	 * @param simulationAPI
	 *            the simulation api
	 */
	public SimulationInitializationHelper(final SimulationAPI simulationAPI) {
		this.simulationAPI = simulationAPI;
	}

	/**
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
	 * @param frameworkConfigurationFileNameName
	 *            the framework configuration file name name
	 * @return the simulation run group context
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	protected SimulationRunGroupContext initializeAPI(
			final String frameworkConfigurationFileNameName)

	throws IOException {

		// Process the configuration properties (creating the not yet populated
		// DistSysRunContext simFrameworkContext = new
		// DistSysRunContext(fullyQualifiedClassNameForDistributedAgentManager);
		final SimulationRunGroupContext simulationRunGroupContext = new SimulationRunGroupContext();
		final SimulationRunGroupConfiguration config = processFrameworkConfigurationProperties(
				frameworkConfigurationFileNameName, simulationRunGroupContext);
		simulationRunGroupContext.setSimulationConfiguration(config);

		// Cache the message exchange template from the file system
		try {
			simulationRunGroupContext
					.setCachedMessageExchangeTemplateWithPlaceholders(MessagingUtilities
							.createCachedMessageExchangeTemplateWithPlaceholders());
		} catch (final JDOMException e) {
			throw new CsfInitializationRuntimeException(
					"Error reading the message exchange template from the file system", e);
		}

		return simulationRunGroupContext;
	}

	/**
	 * Initializes a simulation run.
	 * 
	 * @param simulationSideContext
	 *            the simulation side context
	 * @param simulationRunGroupContext
	 *            the simulation run group context
	 * @return the simulation run context
	 */
	protected SimulationRunContext initializeSimulationRun(
			final Object simulationSideContext,
			final SimulationRunGroupContext simulationRunGroupContext) {
		// TODO: Hook in the configuration to get the actual values
		final SimulationRunContext simulationRunContext = new SimulationRunContext(
				simulationRunGroupContext.getSimulationRunGroup(),
				simulationRunGroupContext);
		simulationRunGroupContext.setSimulationRunContext(simulationRunContext);

		// Configuration
		final SimulationRunConfiguration simulationRunConfiguration = new SimulationRunConfiguration();
		simulationRunContext.setSimulationRunConfiguration(simulationRunConfiguration);

		// Distributed Agents
		// LOW: Fix later to handle multiple distributed systems
		// LOW: Read optional distributed system id from the configuration
		// TODO: Read the actual distributed system id
		final DistributedSystem sys = new DistributedSystem(simulationRunContext
				.getSimulationRunConfiguration().getDistributedSystemID());
		final SimulationDistributedSystemManager simulationDistributedSystemManager = new SimulationDistributedSystemManager(
				simulationRunContext,
				simulationRunConfiguration.getCommonMessagingConcreteImplStr(), sys);
		simulationRunContext
				.addSimulationDistributedSystemManager(simulationDistributedSystemManager);

		return simulationRunContext;
	}

	/*
	 * This method is caused to assign an existing AgentMapping object (created at CSF
	 * initialization) to a simulation-side agent.
	 */
	/**
	 * Map simulation side agent.
	 * 
	 * @param simulationAgent
	 *            the simulation agent
	 * @param simulationRunContext
	 *            the simulation run context
	 */
	public void mapSimulationSideAgent(final Object simulationAgent,
			final SimulationRunContext simulationRunContext) {
		// TODO: Handle multiple distributed systems
		simulationRunContext.getSimulationDistributedSystemManagers().iterator().next()
				.addSimulationAgentToAgentMapping(simulationAgent);

	}

	/*
	 * Reads the Common Simulation Framework Configuration File. Creates the AgentMapping
	 * objects, with the actual mappings added in later.
	 */
	/**
	 * Process framework configuration properties.
	 * 
	 * @param frameworkConfigurationFileNameName
	 *            the framework configuration file name name
	 * @param simulationRunGroupContext
	 *            the simulation run group context
	 * @return the simulation run group configuration
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private SimulationRunGroupConfiguration processFrameworkConfigurationProperties(
			final String frameworkConfigurationFileNameName,
			final SimulationRunGroupContext simulationRunGroupContext) throws IOException {
		/*
		 * FileInputStream fstream = new FileInputStream("textfile.txt"); // Get the
		 * object of DataInputStream DataInputStream in = new DataInputStream(fstream);
		 * BufferedReader br = new BufferedReader(new InputStreamReader(in)); String
		 * strLine; // Read File Line By Line while ((strLine = br.readLine()) != null) {
		 * // Print the content on the console System.out.println(strLine); }
		 */
		final SimulationRunGroupConfiguration config = new SimulationRunGroupConfiguration(
				frameworkConfigurationFileNameName);
		simulationRunGroupContext.setSimulationRunGroupConfiguration(config);

		// TODO: Retrieve the Simulation Run Group level configuration and use those
		// values here:
		final SimulationRunGroup simulationRunGroup = new SimulationRunGroup("12345",
				"1.0", "1.0");
		// TODO: Organizae these two methods better.
		simulationRunGroup.setSimulationFrameworkOptions("MonteCarlo_TestGroupA", null,
				null);
		simulationRunGroupContext.setSimulationRunGroup(simulationRunGroup);

		return config;
	}

}
