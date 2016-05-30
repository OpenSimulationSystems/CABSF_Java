package org.opensimulationsystems.cabsf.sim.core.api;

import java.io.IOException;

import org.jdom2.JDOMException;
import org.opensimulationsystems.cabsf.common.internal.messaging.MessagingUtilities;
import org.opensimulationsystems.cabsf.common.internal.systems.DistributedSystem;
import org.opensimulationsystems.cabsf.common.model.SimulationRunGroup;
import org.opensimulationsystems.cabsf.common.model.cabsfexceptions.CabsfInitializationRuntimeException;
import org.opensimulationsystems.cabsf.common.model.configuration.RunGroupConfiguration;
import org.opensimulationsystems.cabsf.sim.core.api.configuration.SimulationRunConfiguration;
import org.opensimulationsystems.cabsf.sim.core.api.distributedsystems.SimulationDistributedSystemManager;

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
	 * @param cabsfConfigurationFileName
	 *            the framework configuration file name name
	 * @return the simulation run group context
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	protected SimulationRunGroupContext initializeAPI(
			final String cabsfConfigurationFileName)

					throws IOException {

		// Process the configuration properties (creating the not yet populated
		// DistSysRunContext simFrameworkContext = new
		// DistSysRunContext(fullyQualifiedClassNameForDistributedAgentManager);
		final SimulationRunGroupContext simulationRunGroupContext = new SimulationRunGroupContext();
		final RunGroupConfiguration config = readCABSFConfigurationFile(
				cabsfConfigurationFileName, simulationRunGroupContext);
		simulationRunGroupContext.setRunGroupConfiguration(config);

		// Cache the message exchange template from the file system
		try {
			simulationRunGroupContext
			.setupCachedMessageExchangeTemplate(MessagingUtilities
					.createCachedMessageExchangeTemplate());
		} catch (final JDOMException e) {
			throw new CabsfInitializationRuntimeException(
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
	 * This method is caused to assign an existing AgentMapping object (created at CABSF
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
	 * Reads the Common Agent-Based Simulation Framework Configuration File. Creates the
	 * AgentMapping objects, with the actual mappings added in later.
	 */
	/**
	 * Process framework configuration properties.
	 *
	 * @param cabsfConfigurationFileName
	 *            the framework configuration file name name
	 * @param simulationRunGroupContext
	 *            the simulation run group context
	 * @return the simulation run group configuration
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private RunGroupConfiguration readCABSFConfigurationFile(
			final String cabsfConfigurationFileName,
			final SimulationRunGroupContext simulationRunGroupContext) throws IOException {

		final RunGroupConfiguration config = new RunGroupConfiguration(
				cabsfConfigurationFileName);
		simulationRunGroupContext.setRunGroupConfiguration(config);

		// TODO: Retrieve the Simulation Run Group level configuration and use those
		// values here:
		final SimulationRunGroup simulationRunGroup = new SimulationRunGroup("12345",
				"1.0", "1.0");
		// TODO: Organize these two methods better.
		simulationRunGroup.setSimulationFrameworkOptions("MonteCarlo_TestGroupA", null,
				null);
		simulationRunGroupContext.setSimulationRunGroup(simulationRunGroup);

		return config;
	}

}
