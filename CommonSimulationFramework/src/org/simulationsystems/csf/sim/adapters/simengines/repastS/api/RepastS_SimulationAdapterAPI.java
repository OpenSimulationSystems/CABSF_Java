package org.simulationsystems.csf.sim.adapters.simengines.repastS.api;

import java.io.IOException;

import org.simulationsystems.csf.common.csfmodel.SYSTEM_TYPE;
import org.simulationsystems.csf.common.csfmodel.csfexceptions.CsfMessagingRuntimeException;
import org.simulationsystems.csf.common.csfmodel.csfexceptions.CsfRuntimeException;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FRAMEWORK_COMMAND;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessageImpl;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.STATUS;
import org.simulationsystems.csf.sim.core.api.SimulationAPI;
import org.simulationsystems.csf.sim.core.api.SimulationRunContext;
import org.simulationsystems.csf.sim.core.api.SimulationRunGroupContext;

import repast.simphony.context.Context;

/**
 * The Repast Simphony Adapter API context factory. This class is the entry point for
 * RepastS simulations to use the Common Simulation Framework.
 * 
 * Note: This API was originally intended to only be used at the RepastS simulation-level,
 * not at the individual RepastS agent-level. However, in the current version, the
 * individual agents do use this API directly in addition to the agent-level API (RepastS
 * Agent API). This may change in newer versions so that agents only use instantiate the
 * agent-level API.
 * 
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class RepastS_SimulationAdapterAPI {

	/**
	 * The API singleton for adaptor.
	 * 
	 * @return single instance of RepastS_SimulationAdapterAPI
	 */
	public static RepastS_SimulationAdapterAPI getInstance() {
		return instance;
	}

	/** The CSF-wide Simulation API. Intended to be used across ABMS systems. */
	private final SimulationAPI simulationAPI = SimulationAPI.getInstance();

	/** The simulation tool name to set in simulation API. */
	private final String simToolNameToSetInSimulationAPI = "REPAST_SIMPHONY";

	/** The instance. */
	private static RepastS_SimulationAdapterAPI instance = new RepastS_SimulationAdapterAPI();

	/**
	 * Instantiates a new repast s_ simulation adapter api.
	 */
	private RepastS_SimulationAdapterAPI() {
		super();
	}

	/**
	 * Initializes the Common Simulation Framework on the RepastS simulation side, based
	 * on the supplied CSF configuration property file. Calls the simulation-adaptor-wide
	 * Simulation API to initialize the simulation run group.
	 * 
	 * NOTE: The current version initialization is hard coded to only work with the two
	 * reference implementation simulations. The CSF configuration filename is used to
	 * switch the configuration based on the simulation. In the future, this filename will
	 * be used to read an XML configuration file from the file system so that the CSF can
	 * be used for any simulation.
	 * 
	 * @param csfConfigurationFileName
	 *            the framework configuration file name
	 * @return the repast s_ simulation run group context
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public RepastS_SimulationRunGroupContext initializeAPI(
			final String csfConfigurationFileName) throws IOException {

		final SimulationRunGroupContext simulationRunGroupContext = simulationAPI
				.initializeAPI(csfConfigurationFileName, simToolNameToSetInSimulationAPI);

		// Set the Repast-Simphony-specific objects, using the Decorator Pattern
		final RepastS_SimulationRunGroupContext repastS_SimulationRunGroupContext = new RepastS_SimulationRunGroupContext(
				simulationRunGroupContext);

		return repastS_SimulationRunGroupContext;
	}

	// LOW: Allow the same simulation agent class to be both distributed and
	// non-distributed.
	/**
	 * Initializes a single CSF Repast Simphony simulation run. This method configures the
	 * (already-created in the simulation API initialization) AgentMapping objects.
	 * 
	 * @param nativeRepastScontextForThisRun
	 *            the native repast context for this run
	 * @param repastS_SimulationRunGroupContext
	 *            the repast s_ simulation run group context
	 * @return the repast s_ simulation run context
	 */
	public RepastS_SimulationRunContext initializeSimulationRun(
			final Context<Object> nativeRepastScontextForThisRun,
			final RepastS_SimulationRunGroupContext repastS_SimulationRunGroupContext) {
		final SimulationRunContext simulationRunContext = simulationAPI
				.initializeSimulationRun(nativeRepastScontextForThisRun,
						repastS_SimulationRunGroupContext.getSimulationRunGroupContext());

		// User Decorator Pattern for RepastS_SimulationRunContext
		final RepastS_SimulationRunContext repastS_SimulationRunContext = new RepastS_SimulationRunContext(
				simulationRunContext);
		repastS_SimulationRunContext
				.setRepastContextForThisRun(nativeRepastScontextForThisRun);

		repastS_SimulationRunContext
				.setRepastRunGroupContext(repastS_SimulationRunGroupContext);
		// Make the context available to the agents in the Repast model
		nativeRepastScontextForThisRun.add(repastS_SimulationRunContext);

		// LOW: Support multiple Simulation Run Groups. For now just assume that
		// there's one.
		// LOW: Handle multiple distributed systems
		// TODO: Move distributed system manager to main level? same for on the
		// distributed side (simulation engine manager)
		repastS_SimulationRunContext.getSimulationDistributedSystemManagers().iterator()
				.next().initializeAgentMappings();

		boolean atLeastOneMappingPerformed = false;

		// Find all of the individual RepastS agents to be mapped in the
		// framework to distributed agents
		// TODO: Move all of this to the main simulation API to simplify Adapter
		// code.
		// (Same for JADE API side)
		@SuppressWarnings({ "rawtypes" })
		final Iterable<Class> simulationAgentsClasses = nativeRepastScontextForThisRun
				.getAgentTypes();

		// For each simulation agent class
		for (@SuppressWarnings("rawtypes")
		final Class simulationAgentClass : simulationAgentsClasses) {
			// LOW: Allow individual simulation agent classes to be either
			// simulation-only
			// or
			// representations of distributed agents.
			// TODO: Handle multiple distributed systems
			if (repastS_SimulationRunContext.getSimulationDistributedSystemManagers()
					.iterator().next().isAgentClassDistributedType(simulationAgentClass)) {
				@SuppressWarnings("unchecked")
				final Class<Object> simulationAgentClazz = simulationAgentClass;
				final Iterable<Object> simulationAgentsInSingleClass = nativeRepastScontextForThisRun
						.getAgentLayer(simulationAgentClazz);

				// TODO: Look into handling multiple classes in the mapping
				/*
				 * if (repastS_SimulationRunContext.getSimulationRunContext()
				 * .getSimulationRunGroupContext() .getSimulationRunGroupConfiguration()
				 * .getSimulationAgentsBelongToOneClass()) {
				 */

				// For an agent class type, for each individual simulation
				// agent, map to an existing free AgentMapping object
				for (final Object simulationAgent : simulationAgentsInSingleClass) {
					atLeastOneMappingPerformed = true;
					mapSimulationSideAgent(simulationAgent,
							repastS_SimulationRunContext.getSimulationRunContext());
				}
				/*
				 * } else {
				 * 
				 * }
				 */
			} else
				continue; // Not an agent we need to map.
		}

		if (atLeastOneMappingPerformed != true)
			throw new CsfRuntimeException(
					"No mapping was performed of simulation agent(s) to distributed autonomous agent and agent model IDs");

		// TODO: Move this whole section to the main simulation API?
		// 1 - Wait for the command from the simulation administrator to start
		// the simulation
		final FRAMEWORK_COMMAND fc = repastS_SimulationRunContext
				.readFrameworkMessageFromSimulationAdministrator()
				.getFrameworkToSimulationEngineCommand();
		if (fc != FRAMEWORK_COMMAND.START_SIMULATION)
			throw new CsfMessagingRuntimeException(
					"Did not understand the message from the simulation administrator");

		// 2 - Message the distributed systems that the simulation has started
		// and is ready to accept messages from the distributed agents.
		final FrameworkMessage msg = new FrameworkMessageImpl(
				SYSTEM_TYPE.SIMULATION_ENGINE, SYSTEM_TYPE.DISTRIBUTED_SYSTEM,
				repastS_SimulationRunContext.getBlankCachedMessageExchangeTemplate());
		msg.setFrameworkToDistributedSystemCommand(FRAMEWORK_COMMAND.START_SIMULATION);
		// TODO: Loop through the multiple distributed systems
		repastS_SimulationRunContext.messageDistributedSystems(msg,
				repastS_SimulationRunContext.getSimulationRunContext());

		// Wait for distributed system to confirm that simulation is ready
		// to begin
		final STATUS st = repastS_SimulationRunContext
				.readFrameworkMessageFromDistributedSystem().getStatus();
		// TODO: Identify which distributed system caused the error.
		// TODO: Set these up as checked exceptions?
		if (st != STATUS.READY_TO_START_SIMULATION)
			throw new CsfMessagingRuntimeException(
					"Did not understand the message from the simulation distributed system.");

		// The distributed agent (models) have previously been mapped.
		// Now we're ready to perform the steps in the simulation.
		return repastS_SimulationRunContext;
	}

	/**
	 * After the Simulation and Common Framework are initialized, the Simulation Adaptor
	 * API (or child class) is initialized, and prior to executing a simulation run, this
	 * method must be called to configure the simulation-side of the AgentMappings for one
	 * type (class) of simulation agent. If multiple agent classes are distributed, this
	 * method must be called for each type.
	 * 
	 * @param simulationAgent
	 *            the simulation agent
	 * @param simulationRunContext
	 *            the simulation run context
	 */
	private void mapSimulationSideAgent(final Object simulationAgent,
			final SimulationRunContext simulationRunContext) {
		simulationAPI.mapSimulationSideAgent(simulationAgent, simulationRunContext);
	}

	/**
	 * Map simulation side agents.
	 * 
	 * @param agentsOfOneType
	 *            the agents of one type
	 * @param simulationRunContext
	 *            the simulation run context
	 * @see mapSimulationSideAgent
	 */
	@SuppressWarnings("unused")
	private void mapSimulationSideAgents(final Iterable<Object> agentsOfOneType,
			final SimulationRunContext simulationRunContext) {
		for (final Object simulationAgent : agentsOfOneType) {
			mapSimulationSideAgent(simulationAgent, simulationRunContext);
		}
	}

}
