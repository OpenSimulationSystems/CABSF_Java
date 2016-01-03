package org.opensimulationsystems.cabsf.distsys.core.api;

import java.io.IOException;

import org.jdom2.JDOMException;
import org.opensimulationsystems.cabsf.common.internal.messaging.MessagingUtilities;
import org.opensimulationsystems.cabsf.common.model.SimulationRunGroup;
import org.opensimulationsystems.cabsf.common.model.cabsfexceptions.CabsfInitializationRuntimeException;
import org.opensimulationsystems.cabsf.common.model.configuration.RunGroupConfiguration;
import org.opensimulationsystems.cabsf.distsys.core.api.configuration.DistSysRunConfiguration;
import org.opensimulationsystems.cabsf.distsys.core.api.simulationruntime.SimulationEngineManager;
import org.opensimulationsystems.cabsf.distsys.core.api.softwareagents.DistributedAgentsManager;

public class DistSysInitializationHelper {
	private DistributedSystemAPI distributedSystemAPI;

	/*
	 * Use the other Constructor instead
	 */
	@SuppressWarnings("unused")
	private DistSysInitializationHelper() {
	}

	public DistSysInitializationHelper(final DistributedSystemAPI distributedSystemAPI) {
		this.distributedSystemAPI = distributedSystemAPI;
	}

	/*
	 * This method is caused to assign an existing AgentMapping object (created at CABSF
	 * initialization) to a simulation-side agent.
	 */
	public void assignNativeSoftwareAgent(
			final Object nativeDistributedAutonomousAgent,
			final DistSysRunContext distSysRunContext) {
		// TODO: Handle multiple distributed systems
		distSysRunContext.getDistributedAgentsManager()
				.assignNativeDistributedAutonomousAgent(nativeDistributedAutonomousAgent);
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
	 * Throws exception for error reading the Common Agent-Based Simulation Framework configuration
	 * file.
	 */
	/*
	 * protected DistSysRunContext initializeAPI( String
	 * frameworkConfigurationFileNameName, String
	 * fullyQualifiedClassNameForDistributedAgentManager)
	 */
	protected DistSysRunGroupContext initializeAPI(final String cabsfConfigurationFileName)
			throws IOException {

		// Process the configuration properties (creating the not yet populated
		// DistSysRunContext simFrameworkContext = new
		// DistSysRunContext(fullyQualifiedClassNameForDistributedAgentManager);
		final DistSysRunGroupContext simulationRunGroupConfiguration = new DistSysRunGroupContext();
		final RunGroupConfiguration config = readCABSFConfigurationFile(
				cabsfConfigurationFileName, simulationRunGroupConfiguration);
		simulationRunGroupConfiguration.setRunGroupConfiguration(config);

		// Cache the message exchange template from the file system
		try {
			simulationRunGroupConfiguration
					.setupCachedMessageExchangeTemplateWithPlaceholders(MessagingUtilities
							.createCachedMessageExchangeTemplateWithPlaceholders());
		} catch (final JDOMException e) {
			throw new CabsfInitializationRuntimeException(
					"Error reading the message exchange template from the file system", e);
		}

		return simulationRunGroupConfiguration;
	}

	protected DistSysRunContext initializeSimulationRun(
			final Object simulationSideContext,
			final DistSysRunGroupContext distSysRunGroupContext) {
		// TODO: Hook in the configuration to get the actual values
		final DistSysRunContext distSysRunContext = new DistSysRunContext(
				distSysRunGroupContext.getRunGroup(), distSysRunGroupContext);
		distSysRunGroupContext.setDistSysRunContext(distSysRunContext);

		// Configuration
		// TODO: Actually setup run-level configuration?
		final DistSysRunConfiguration distSysRunConfiguration = new DistSysRunConfiguration();
		distSysRunContext.setSimulationRunConfiguration(distSysRunConfiguration);
		distSysRunContext.setDistSysRunGroupContext(distSysRunGroupContext);

		// Set up the manager for the simulation runtime. Uses reflection to specify the
		// correct
		// interface
		// LOW: Read optional simulation runtime id from the configuration
		final SimulationEngineManager simRuntime = new SimulationEngineManager(
				distSysRunContext,
				distSysRunConfiguration.getCommonMessagingConcreteImplStr(),
				distSysRunContext.getDistSysRunConfiguration().getDistributedSystemID());
		distSysRunContext.setSimulationEngine(simRuntime);

		// Set up manager for the distributed autonomous agents (such as the JADE agents)
		// on behalf
		// of the client of this API, such as the CABSF JADE Controller Agent
		// TODO: Add this distributed system's id
		final DistributedAgentsManager distributedAgentsManager = new DistributedAgentsManager(
				distSysRunContext.getDistSysRunConfiguration().getDistributedSystemID(),
				distSysRunContext, distSysRunContext.getDistSysRunConfiguration());
		distSysRunContext.setDistributedAgentsManager(distributedAgentsManager);

		return distSysRunContext;
	}

	/*
	 * Reads the Common Agent-Based Simulation Framework Configuration File. Creates the AgentMapping
	 * objects, with the actual mappings added in later.
	 */
	private RunGroupConfiguration readCABSFConfigurationFile(
			final String cabsfConfigurationFileName,
			final DistSysRunGroupContext distSysRunGroupContext) throws IOException {

		// final DistSysRunGroupConfiguration config = new DistSysRunGroupConfiguration();
		final RunGroupConfiguration config = new RunGroupConfiguration(
				cabsfConfigurationFileName);

		// TODO: Retrieve the Simulation Run Group level configuration and use those
		// values here
		// TODO: Comparison against actual simulation settings on the simulation side.
		// Assume
		// simulation starts first and that all values match.
		final SimulationRunGroup simulationRunGroup = new SimulationRunGroup("12345",
				"1.0", "1.0");
		simulationRunGroup.setSimulationFrameworkOptions("MonteCarlo_TestGroupA", null,
				null);
		distSysRunGroupContext.setDistributedSystemSimulationRunGroup(simulationRunGroup);

		return config;
	}

}
