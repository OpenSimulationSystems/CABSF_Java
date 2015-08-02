package org.opensimulationsystems.cabsf.sim.core.api.distributedsystems;

import java.util.HashSet;

import org.opensimulationsystems.cabsf.common.internal.messaging.bridge.abstraction.CommonMessagingAbstraction;
import org.opensimulationsystems.cabsf.common.internal.messaging.bridge.abstraction.CommonMessagingRefinedAbstractionAPI;
import org.opensimulationsystems.cabsf.common.internal.messaging.bridge.implementation.CommonMessagingImplementationAPI;
import org.opensimulationsystems.cabsf.common.internal.systems.AgentMappingHelper;
import org.opensimulationsystems.cabsf.common.internal.systems.DistributedSystem;
import org.opensimulationsystems.cabsf.common.model.AgentMapping;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FrameworkMessage;
import org.opensimulationsystems.cabsf.sim.core.api.SimulationRunContext;

/**
 * Manages the distributed agents from other systems through the common simulation
 * framework. Also acts as the client to the Bridge Pattern (through composition) to the
 * common messaging.
 *
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class SimulationDistributedSystemManager {

	/**
	 * The Enum CONFIGURATION_KEYS.
	 *
	 * @author Jorge Calderon
	 * @version 0.1
	 * @since 0.1
	 */
	public enum CONFIGURATION_KEYS {
		/** The distributed agents. */
		DISTRIBUTED_AGENTS
	}

	/** The simulation run context. */
	private SimulationRunContext simulationRunContext;

	/** The distributed system. */
	private DistributedSystem distributedSystem;

	/** The fully initialized agent mappings. */
	private final HashSet<AgentMapping> fullyInitializedAgentMappings = new HashSet<AgentMapping>();

	/** The common messaging implementation api. */
	private CommonMessagingImplementationAPI commonMessagingImplementationAPI;

	/** The common messaging abstraction. */
	private CommonMessagingAbstraction commonMessagingAbstraction = null;

	/**
	 * Instantiates a new simulation distributed system manager.
	 */
	@SuppressWarnings("unused")
	private SimulationDistributedSystemManager() {
	}

	/**
	 * Instantiates a new simulation distributed system manager.
	 *
	 * @param simulationRunContext
	 *            the simulation run context
	 * @param getCommonMessagingConcreteImplStr
	 *            the get common messaging concrete impl str
	 * @param distributedSystem
	 *            the distributed system
	 */
	public SimulationDistributedSystemManager(
			final SimulationRunContext simulationRunContext,
			final String getCommonMessagingConcreteImplStr,
			final DistributedSystem distributedSystem) {
		this.simulationRunContext = simulationRunContext;
		this.distributedSystem = distributedSystem;

		// Check which Bridge implementation we're going to use, based on what was
		// specified in the
		// configuration.
		if (simulationRunContext
				.getSimulationRunConfiguration()
				.getCommonMessagingConcreteImplStr()
				.equals("org.opensimulationsystems.cabsf.common.internal.messaging.bridge.implementation.RedisMessagingConcreteImplementation")) {
		} else {
			// TODO: Handle this better
			throw new IllegalStateException(
					"Error: Redis not properly configured in the CABSF configuration file.");
		}

		// Instantiate the correct manager for the common messaging interface (e.g., Redis
		// or web
		// services) using reflection.
		// TODO: Handle exception when unable to instantiate class
		// TODO: ?Handle configuration/reflection for Bridge Refined Abstraction?
		try {
			final Class<?> cl = Class.forName(getCommonMessagingConcreteImplStr);
			// Constructor<?> cons = cl.getConstructor(cl.getClass());
			// commonMessagingImplementationAPI =
			// (commonMessagingImplementationAPI) cons.newInstance();
			commonMessagingImplementationAPI = (CommonMessagingImplementationAPI) cl
					.newInstance();
			// FIXME: Look up all catch clauses and handle the errors appropriately
		} catch (final InstantiationException e) {
			e.printStackTrace();
		} catch (final IllegalAccessException e) {
			e.printStackTrace();
		} catch (final IllegalArgumentException e) {
			e.printStackTrace();
		} catch (final SecurityException e) {
			e.printStackTrace();
		} catch (final ClassNotFoundException e) {
			e.printStackTrace();
		}

		// TODO: Dynamically get the appropriate connection string based on the configured
		// common
		// interface
		commonMessagingAbstraction = new CommonMessagingRefinedAbstractionAPI(
				commonMessagingImplementationAPI, simulationRunContext
				.getSimulationRunConfiguration().getRedisConnectionString(),
				simulationRunContext.getSimulationRunConfiguration()
				.getSimulationEngineID());

		// TODO: Move this configuration to the Simulation Run Group level?
		commonMessagingAbstraction
		.initializeSimulationFrameworkCommonMessagingInterface(simulationRunContext
				.getSimulationRunConfiguration().getRedisConnectionString());
	}

	/*
	 * Assigns a Simulation Agent to an AgentMapping
	 */
	/**
	 * Adds the simulation agent to agent mapping.
	 *
	 * @param agentObj
	 *            the agent obj
	 * @return the agent mapping
	 */
	public AgentMapping addSimulationAgentToAgentMapping(final Object agentObj) {
		// Add Validation to make sure mappings exist. / Throw exception

		return AgentMappingHelper
				.addNativeSimulationAgentToAgentMapping(this
						.getClass().getCanonicalName().toString(), simulationRunContext
						.getSimulationRunGroupContext()
						.getSimulationRunGroupConfiguration()
						.getAgentsReadyForSimulationSideMapping(),
						fullyInitializedAgentMappings, agentObj);
	}

	/**
	 * Close interface.
	 */
	public void closeInterface() {
		commonMessagingAbstraction.closeInterface();
	}

	/**
	 * Gets the agent mapping for object.
	 *
	 * @param obj
	 *            the obj
	 * @return the agent mapping for object
	 */
	public AgentMapping getAgentMappingForObject(final Object obj) {
		for (final AgentMapping am : fullyInitializedAgentMappings) {
			if (am.getSimulationAgent().equals(obj)) {
				return am;
			}
		}
		return null;
	}

	// TODO: Pull these from the configuration
	/**
	 * Initialize agent mappings.
	 */
	public void createAgentMappingObjects() {
		// Create AgentMapping objects based on the configured type and number
		// of agents.
		// These objects will be populated with actual mapped simulation-side
		// and
		// distributed-agent-side data.
		// Mocking data for now;
		// TODO: Pull from configuration

		simulationRunContext.getSimulationRunGroupContext()
		.getSimulationRunGroupConfiguration().createAgentMappingObjects();

	}

	/*
	 * Checks whether this agent belongs to a class that is expected to be distributed
	 * outside of the simulation runtime environment.
	 */
	// TODO: Revist how this is being used.
	/**
	 * Checks if is agent class distributed type.
	 *
	 * @param agentClass
	 *            the agent class
	 * @return true, if is agent class distributed type
	 */
	public boolean isAgentClassDistributedType(final Class<Object> agentClass) {
		if (simulationRunContext.getSimulationRunGroupContext()
				.getSimulationRunGroupConfiguration().getAgentTypes()
				.contains(agentClass.getCanonicalName())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Log helper.
	 *
	 * @return the object
	 */
	public Object logHelper() {
		return fullyInitializedAgentMappings;
	}

	/**
	 * Message distributed agents.
	 *
	 * @param frameworkMessage
	 *            the framework message
	 * @param simulationRunContext
	 *            the simulation run context
	 */
	public void messageDistributedAgents(final FrameworkMessage frameworkMessage,
			final SimulationRunContext simulationRunContext) {
		// TODO: Multiple Distributed systems
		commonMessagingAbstraction.sendMessageToDistributedAgents(frameworkMessage,
				distributedSystem, simulationRunContext);
	}

	/**
	 * Read framework message from distributed system.
	 *
	 * @return the framework message
	 */
	public FrameworkMessage readFrameworkMessageFromDistributedSystem() {
		// The target is this side of the framework (simulation engine)
		return commonMessagingAbstraction
				.readFrameworkMessageFromDistributedSystem(simulationRunContext
						.getSimulationRunConfiguration().getSimulationEngineID());

	}

	/**
	 * Read framework message from simulation administrator.
	 *
	 * @return the framework message
	 */
	public FrameworkMessage readFrameworkMessageFromSimulationAdministrator() {
		// The target is this side of the framework (simulation engine)
		return commonMessagingAbstraction
				.readFrameworkMessageFromSimulationAdministrator(simulationRunContext
						.getSimulationRunConfiguration().getSimulationEngineID());

	}

}
