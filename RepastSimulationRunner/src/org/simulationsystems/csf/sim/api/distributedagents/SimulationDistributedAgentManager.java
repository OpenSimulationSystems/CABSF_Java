package org.simulationsystems.csf.sim.api.distributedagents;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.simulationsystems.csf.common.internal.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.sim.api.SimulationAdapterAPI;
import org.simulationsystems.csf.sim.api.SimulationRunContext;
import org.simulationsystems.csf.sim.api.SimulationRunGroupContext;
import org.simulationsystems.csf.sim.internal.messaging.bridge.abstraction.SimulationDistributedAgentMessagingManager;

/*
 * Class to manage the distributed agents from other systems through the common simulation framework.  
 */
public class SimulationDistributedAgentManager {
	private SimulationRunContext simulationRunContext;
	private ConcurrentHashMap<UUID, AgentMapping> agentMappings = new ConcurrentHashMap<UUID, AgentMapping>();
	private HashSet<UUID> agentsReadyForSimulationSideMapping = new HashSet<UUID>();
	private HashSet<UUID> agentsReadyForDistributedAgentMapping = new HashSet<UUID>();
	private SimulationDistributedAgentMessagingManager simulationDistributedAgentMessagingManager;

	public enum CONFIGURATION_KEYS {
		DISTRIBUTED_AGENTS
	}

	// private agentMappingsDirectory;

	@SuppressWarnings("unused")
	private SimulationDistributedAgentManager() {
	}

	public SimulationDistributedAgentManager(SimulationRunContext simulationRunContext,
			String simulationDistributedAgentMessagingManagerStr) {
		this.simulationRunContext = simulationRunContext;

		// Instantiate the correct manager for the common messaging interface (e.g., Redis or web
		// services) using reflection.
		// TODO: Handle exception when unable to instantiate class
		try {
			Class<?> cl = Class.forName(simulationDistributedAgentMessagingManagerStr);
			// Constructor<?> cons = cl.getConstructor(cl.getClass());
			// simulationDistributedAgentMessagingManager =
			// (SimulationDistributedAgentMessagingManager) cons.newInstance();
			simulationDistributedAgentMessagingManager = (SimulationDistributedAgentMessagingManager) cl
					.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} /*
		 * catch (InvocationTargetException e) { e.printStackTrace(); } catch (NoSuchMethodException
		 * e) { e.printStackTrace(); }
		 */catch (SecurityException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		simulationDistributedAgentMessagingManager
				.initializeSimulationFrameworkCommonMessagingInterface();
	}

	public SimulationDistributedAgentMessagingManager getSimulationDistributedAgentMessagingManager() {
		return simulationDistributedAgentMessagingManager;
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

	public void messageDistributedAgents(FrameworkMessage frameworkMessage) {
		simulationDistributedAgentMessagingManager.sendMessageToDistributedAgents(frameworkMessage);
	}


}
