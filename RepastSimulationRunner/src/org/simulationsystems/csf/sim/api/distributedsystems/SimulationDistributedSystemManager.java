package org.simulationsystems.csf.sim.api.distributedsystems;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.internal.messaging.bridge.abstraction.CommonMessagingAbstraction;
import org.simulationsystems.csf.common.internal.messaging.bridge.abstraction.CommonMessagingRefinedAbstractionAPI;
import org.simulationsystems.csf.common.internal.messaging.bridge.implementation.CommonMessagingImplementationAPI;
import org.simulationsystems.csf.common.internal.messaging.bridge.implementation.RedisMessagingConcreteImplementation;
import org.simulationsystems.csf.common.internal.systems.AgentMapping;
import org.simulationsystems.csf.common.internal.systems.AgentMappingHelper;
import org.simulationsystems.csf.common.internal.systems.DistributedSystem;
import org.simulationsystems.csf.sim.api.SimulationAPI;
import org.simulationsystems.csf.sim.api.SimulationRunContext;
import org.simulationsystems.csf.sim.api.SimulationRunGroupContext;

/*
 * Class to manage the distributed agents from other systems through the common simulation
 * framework. Also acts as the client to the Bridge Pattern (through composition) to the common
 * messaging.
 */
public class SimulationDistributedSystemManager {
	private SimulationRunContext simulationRunContext;
	private DistributedSystem distributedSystem;

	// TODO: Change the UUIDs to String
	private HashSet<AgentMapping> agentsReadyForSimulationSideMapping = new HashSet<AgentMapping>();
	private HashSet<AgentMapping> fullyInitializedAgentMappings = new HashSet<AgentMapping>();

	private CommonMessagingImplementationAPI commonMessagingImplementationAPI;

	private CommonMessagingAbstraction commonMessagingAbstraction = null;

	public enum CONFIGURATION_KEYS {
		DISTRIBUTED_AGENTS
	}

	@SuppressWarnings("unused")
	private SimulationDistributedSystemManager() {
	}

	public SimulationDistributedSystemManager(SimulationRunContext simulationRunContext,
			String getCommonMessagingConcreteImplStr, DistributedSystem distributedSystem) {
		this.simulationRunContext = simulationRunContext;
		this.distributedSystem = distributedSystem;

		// Check which Bridge implementation we're going to use, based on what was
		// specified in the
		// configuration.
		if (simulationRunContext
				.getSimulationRunConfiguration()
				.getCommonMessagingConcreteImplStr()
				.equals("org.simulationsystems.csf.common.internal.messaging.bridge.implementation.RedisMessagingConcreteImplementation")) {
		} else {
			// TODO: Handle this better
			throw new IllegalStateException(
					"Error: Redis not properly configured in the CSF configuration file.");
		}

		// commonMessagingAbstraction =
		// simulationRunContext.getSimulationRunConfiguration()
		// = new CommonMessagingRefinedAbstractionAPI(null);

		// Instantiate the correct manager for the common messaging interface (e.g., Redis
		// or web
		// services) using reflection.
		// TODO: Handle exception when unable to instantiate class
		// TODO: ?Handle configuration/reflection for Bridge Refined Abstraction?
		try {
			Class<?> cl = Class.forName(getCommonMessagingConcreteImplStr);
			// Constructor<?> cons = cl.getConstructor(cl.getClass());
			// commonMessagingImplementationAPI =
			// (commonMessagingImplementationAPI) cons.newInstance();
			commonMessagingImplementationAPI = (CommonMessagingImplementationAPI) cl
					.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
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

	// TODO: Pull these from the configuration
	public void initializeAgentMappings() {
		// Create AgentMapping objects based on the configured type and number
		// of agents.
		// These objects will be populated with actual mapped simulation-side
		// and
		// distributed-agent-side data.
		// Mocking data for now;
		// TODO: Pull from configuration
		AgentMappingHelper.createAgentMapping(agentsReadyForSimulationSideMapping,
				distributedSystem.getDistributedSystemID(),
				"DistributedSystemAutonomousAgent1", "DistributedAgentModel1",
				"jzombies.Human");
		AgentMappingHelper.createAgentMapping(agentsReadyForSimulationSideMapping,
				distributedSystem.getDistributedSystemID(),
				"DistributedSystemAutonomousAgent2", "DistributedAgentModel2",
				"jzombies.Human");
		AgentMappingHelper.createAgentMapping(agentsReadyForSimulationSideMapping,
				distributedSystem.getDistributedSystemID(),
				"DistributedSystemAutonomousAgent3", "DistributedAgentModel3",
				"jzombies.Human");
		AgentMappingHelper.createAgentMapping(agentsReadyForSimulationSideMapping,
				distributedSystem.getDistributedSystemID(),
				"DistributedSystemAutonomousAgent4", "DistributedAgentModel4",
				"jzombies.Human");
		AgentMappingHelper.createAgentMapping(agentsReadyForSimulationSideMapping,
				distributedSystem.getDistributedSystemID(),
				"DistributedSystemAutonomousAgent5", "DistributedAgentModel5",
				"jzombies.Human");
	}

	/*
	 * Checks whether this agent belongs to a class that is expected to be distributed
	 * outside of the simulation runtime environment.
	 */
	public boolean isAgentClassDistributedType(Class<Object> agentClass) {
		// TODO: Tie this to the simulation configuration
		if (agentClass.getCanonicalName().equals("jzombies.Human"))
			return true;
		else
			return false;

	}

	/*
	 * Assigns a Simulation Agent to an AgentMapping
	 */
	public AgentMapping addSimulationAgentToAgentMapping(Object agentObj) {
		// Add Validation to make sure mappings exist. / Throw exception

		return AgentMappingHelper.addNativeSimulationToDistributedAutononmousAgentToAgentMapping(this.getClass()
				.getCanonicalName().toString(), agentsReadyForSimulationSideMapping,
				fullyInitializedAgentMappings, agentObj);
	}

	public Object logHelper() {
		return fullyInitializedAgentMappings;
	}

	public void messageDistributedAgents(FrameworkMessage frameworkMessage,
			SimulationRunContext simulationRunContext) {
		// TODO: Multiple Distributed systems
		commonMessagingAbstraction.sendMessageToDistributedAgents(frameworkMessage,
				distributedSystem, simulationRunContext);
	}

	public void closeInterface() {
		commonMessagingAbstraction.closeInterface();
	}

	public FrameworkMessage readFrameworkMessageFromSimulationAdministrator() {
		// The target is this side of the framework (simulation engine)
		return commonMessagingAbstraction
				.readFrameworkMessageFromSimulationAdministrator(simulationRunContext
						.getSimulationRunConfiguration().getSimulationEngineID());

	}

	public FrameworkMessage readFrameworkMessageFromDistributedSystem() {
		// The target is this side of the framework (simulation engine)
		return commonMessagingAbstraction
				.readFrameworkMessageFromDistributedSystem(simulationRunContext
						.getSimulationRunConfiguration().getSimulationEngineID());

	}

}
