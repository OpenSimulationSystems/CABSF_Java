package org.simulationsystems.csf.sim.api.distributedsystems;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.simulationsystems.csf.common.internal.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.internal.systems.DistributedSystem;
import org.simulationsystems.csf.sim.api.SimulationAdapterAPI;
import org.simulationsystems.csf.sim.api.SimulationRunContext;
import org.simulationsystems.csf.sim.api.SimulationRunGroupContext;
import org.simulationsystems.csf.sim.internal.messaging.bridge.abstraction.CommonMessagingRefinedAbstractionAPI;
import org.simulationsystems.csf.sim.internal.messaging.bridge.abstraction.DistributedAgentMessagingAbstraction;
import org.simulationsystems.csf.sim.internal.messaging.bridge.implementation.CommonMessagingImplementationAPI;
import org.simulationsystems.csf.sim.internal.messaging.bridge.implementation.RedisMessagingConcreteImplementation;

/*
 * Class to manage the distributed agents from other systems through the common simulation
 * framework. Also acts as the client to the Bridge Pattern (through composition) to the common
 * messaging.
 */
public class SimulationDistributedSystemManager {
	private SimulationRunContext simulationRunContext;
	private DistributedSystem distributedSystem;
	private ConcurrentHashMap<UUID, AgentMapping> agentMappings = new ConcurrentHashMap<UUID, AgentMapping>();
	private HashSet<UUID> agentsReadyForSimulationSideMapping = new HashSet<UUID>();
	private HashSet<UUID> agentsReadyForDistributedAgentMapping = new HashSet<UUID>();
	private CommonMessagingImplementationAPI commonMessagingImplementationAPI;

	private DistributedAgentMessagingAbstraction distributedAgentMessagingRefinedAbstraction = null;

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

		// Check which Bridge implementation we're going to use, based on what was specified in the
		// configuration.
		if (simulationRunContext
				.getSimulationRunConfiguration()
				.getCommonMessagingConcreteImplStr()
				.equals("org.simulationsystems.csf.sim.internal.messaging.bridge.implementation.RedisMessagingConcreteImplementation")) {
		} else {
			// TODO: Handle this better
			throw new IllegalStateException(
					"Error: Redis not properly configured in the CSF configuration file.");
		}

		// distributedAgentMessagingRefinedAbstraction =
		// simulationRunContext.getSimulationRunConfiguration()
		// = new CommonMessagingRefinedAbstractionAPI(null);

		// Instantiate the correct manager for the common messaging interface (e.g., Redis or web
		// services) using reflection.
		// TODO: Handle exception when unable to instantiate class
		// TODO: ?Handle configuration/reflection for Bridge Refined Abstraction?
		try {
			Class<?> cl = Class.forName(getCommonMessagingConcreteImplStr);
			// Constructor<?> cons = cl.getConstructor(cl.getClass());
			// commonMessagingImplementationAPI =
			// (commonMessagingImplementationAPI) cons.newInstance();
			commonMessagingImplementationAPI = (CommonMessagingImplementationAPI) cl.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		 catch (SecurityException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		distributedAgentMessagingRefinedAbstraction = new CommonMessagingRefinedAbstractionAPI(
				commonMessagingImplementationAPI, simulationRunContext.getSimulationRunConfiguration().getRedisConnectionString());
		
		// TODO: Move this configuration to the Simulation Run Group level?
		distributedAgentMessagingRefinedAbstraction
				.initializeSimulationFrameworkCommonMessagingInterface(simulationRunContext.getSimulationRunConfiguration().getRedisConnectionString());
	}

	protected ConcurrentHashMap<UUID, AgentMapping> getAgentMappings() {
		return agentMappings;
	}

	/*
	 * Creates objects to hold Agent Mappings between the simulation-side and distributed-agent-side
	 * agents. The actual setting of mapped objects occurs later on. See
	 * org.simulationsystems.simulationframework
	 * .simulation.adapters.simulationapps.api.distributedagents
	 * .RepastSimphonySimulationDistributedAgentManager for reference; <br/><br/>
	 * 
	 * It is preferred for Adapter authors to create a Simulation-Toolkit-specific class inheriting
	 * form this class. Its purpose is to convert generic "Object"s back to native
	 * Simulation-Toolkit-specific objects, which aids the API clients at compile time.
	 */
	public AgentMapping createAgentMapping(String fullyQualifiedSimulationAgentName,
			String fullyQualifiedDistributedAgentName) {
		UUID uuid = UUID.randomUUID();
		AgentMapping am = new AgentMapping(uuid, fullyQualifiedSimulationAgentName,
				fullyQualifiedDistributedAgentName);
		agentMappings.put(uuid, am);
		agentsReadyForSimulationSideMapping.add(uuid);

		return am;
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
		createAgentMapping("jzombies.Human", "jade.Agent");
		createAgentMapping("jzombies.Human", "jade.Agent");
		createAgentMapping("jzombies.Human", "jade.Agent");
		createAgentMapping("jzombies.Human", "jade.Agent");
		createAgentMapping("jzombies.Human", "jade.Agent");
	}

	/*
	 * Checks whether this agent belongs to a class that is expected to be distributed outside of
	 * the simulation runtime environment.
	 */
	public boolean isAgentClassDistributedType(Class agentClass) {
		// TODO: Tie this to the simulation configuration

		if (agentClass.getCanonicalName().equals("jzombies.Human"))
			return true;
		else
			return false;

	}

	/*
	 * Assigns a Simulation Agent to an AgentMapping
	 */
	public AgentMapping addSimulationAgentToAgentMapping(Object agent) {
		// Add Validation to make sure mappings exist. / Throw exception

		// Take first available
		AgentMapping am = null;
		try {
			UUID agentMappingToAssign = agentsReadyForSimulationSideMapping.iterator().next();
			am = agentMappings.get(agentMappingToAssign);
			am.setSimulationAgent(agent);
			agentsReadyForSimulationSideMapping.remove(agentMappingToAssign);
			agentsReadyForDistributedAgentMapping.add(agentMappingToAssign);
			System.out.println("Successfully mapped " + agentMappingToAssign.toString()
					+ " class: " + am.getClass().getCanonicalName());
		} catch (java.util.NoSuchElementException e) {
			System.out.println("exception:" + e.getMessage() + "  class: "
					+ agent.getClass().getCanonicalName());
		}

		return am;
	}

	public Object logHelper() {
		return agentMappings;
	}

	public void messageDistributedAgents(FrameworkMessage frameworkMessage,
			SimulationRunContext simulationRunContext) {
		// TODO: Multiple Distributed systems
		distributedAgentMessagingRefinedAbstraction.sendMessageToDistributedAgents(
				frameworkMessage, distributedSystem, simulationRunContext);
	}

}
