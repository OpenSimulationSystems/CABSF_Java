package org.simulationsystems.csf.sim.engines.adapters.repastS.api;

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
import org.simulationsystems.csf.sim.core.api.distributedsystems.SimulationDistributedSystemManager;

import repast.simphony.context.Context;

/**
 * This API is only for use by developers of adapters to connect simulation tools (such as
 * Repast) and agent-based systems (such as JADE) into the common simulation framework.
 * Simulation and Agent developers using such systems should use the appropriate
 * adapter(s). The following highlights the where in the overall system this code sits:<br/>
 * <br/>
 * 
 * Common Framework---> ***COMMON FRAMEWORK API*** --> Simulation and Agent
 * RepastSimphonyRepastSimphonySimulationAdapterAPI(s) --> Simulations and Agents (Such as
 * Repast simulations and JADE agents) --> End Users of Simulation<br/>
 * <br/>
 * 
 * Currently supported Adaptors (Implementors of this API):<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Repast - Via the
 * "Repast Simulation RepastS_SimulationRunnerMain" Application, which is both an
 * RepastSimphonyRepastSimphonySimulationAdapterAPI into the common simulation framework
 * and its own application programmatically running Repast as a library.<br/>
 * <br/>
 * 
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;JADE - Via the
 * "Common Simulation Framework RepastSimphonyRepastSimphonySimulationAdapterAPI JADE Agent"
 * .<br/>
 * <br/>
 * 
 * THIS PACKAGE WILL BE MOVED TO A SEPARATE JAR. TEMPORARILY HERE WIH THE REPAST
 * SIMULATION WRAPPER (RSW)<br/>
 * 
 * @author Jorge Calderon
 */
public class RepastS_SimulationAdapterAPI {
	private SimulationAPI simulationAPI = SimulationAPI.getInstance();
	private String simToolNameToSetInSimulationAPI = "REPAST_SIMPHONY";
	// private String fullyQualifiedClassNameForDistributedAgentManager =
	// "org.simulationsystems.csf.sim.engines.adapters.repastS.api.distributedagents.RepastSimphonySimulationDistributedAgentManager";

	private static RepastS_SimulationAdapterAPI instance = new RepastS_SimulationAdapterAPI();

	/*
	 * Use getInstance() instead.
	 */
	private RepastS_SimulationAdapterAPI() {
		super();
	}

	/*
	 * This method should be called afterRepastSimphonySimulationAdapterAPI.getInstance()
	 * to initialize the common framework simulation based on the supplied configuration
	 * properties.
	 * 
	 * @ param String The path to the Common Simulation Configuration File
	 */
	public RepastS_SimulationRunGroupContext initializeAPI(
			String frameworkConfigurationFileName) throws IOException {

		SimulationRunGroupContext simulationRunGroupContext = simulationAPI
				.initializeAPI(frameworkConfigurationFileName,
						simToolNameToSetInSimulationAPI);

		// Set the Repast-Simphony-specific objects, using the Decorator Pattern
		RepastS_SimulationRunGroupContext repastS_SimulationRunGroupContext = new RepastS_SimulationRunGroupContext(
				simulationRunGroupContext);

		return repastS_SimulationRunGroupContext;
	}

	/**
	 * 
	 * The API singleton for clients that are simulation systems adapters to into the
	 * common simulation framework
	 * 
	 */
	public static RepastS_SimulationAdapterAPI getInstance() {
		return instance;
	}

	/*
	 * Initialize the simulation run in Repast Simphony. This method configures the
	 * (already-created in the simulation API initialization) AgentMapping objects. Repast
	 * Simphony-specific simulation run initialization
	 * 
	 * JADE_DistSysRunContext result from initializing the API is passed in, in this
	 * method.
	 */
	// LOW: Allow the same simulation agent class to be both distributed and
	// non-distributed.
	public RepastS_SimulationRunContext initializeSimulationRun(
			Context<Object> nativeRepastContextForThisRun,
			RepastS_SimulationRunGroupContext repastS_SimulationRunGroupContext) {
		SimulationRunContext simulationRunContext = simulationAPI
				.initializeSimulationRun(nativeRepastContextForThisRun,
						repastS_SimulationRunGroupContext.getSimulationRunGroupContext());

		// User Decorator Pattern for RepastS_SimulationRunContext
		RepastS_SimulationRunContext repastS_SimulationRunContext = new RepastS_SimulationRunContext(
				simulationRunContext);
		repastS_SimulationRunContext
				.setRepastContextForThisRun(nativeRepastContextForThisRun);

		repastS_SimulationRunContext
				.setRepastRunGroupContext(repastS_SimulationRunGroupContext);
		// Make the context available to the agents in the Repast model
		nativeRepastContextForThisRun.add(repastS_SimulationRunContext);

		// LOW: Support multiple Simulation Run Groups. For now just assume that there's
		// one.
		// LOW: Handle multiple distributed systems
		// TODO: Move distributed system manager to main level? same for on the
		// distributed side (simulation engine manager)
		repastS_SimulationRunContext.getSimulationDistributedSystemManagers().iterator()
				.next().initializeAgentMappings();

		boolean atLeastOneMappingPerformed = false;
		// Find all of the individual Repast agents to be mapped in the framework to
		// distributed
		// agents
		// TODO: Move all of this to the main simulation API to simplify Adapter code.
		// (Same for JADE API side)
		@SuppressWarnings({ "rawtypes" })
		Iterable<Class> simulationAgentsClasses = nativeRepastContextForThisRun
				.getAgentTypes();
		// For each simulation agent class
		for (@SuppressWarnings("rawtypes")
		Class simulationAgentClass : simulationAgentsClasses) {
			// LOW: Allow individual simulation agent classes to be either simulation-only
			// or
			// representations of distributed agents.
			// TODO: Handle multiple distributed systems
			if (repastS_SimulationRunContext.getSimulationDistributedSystemManagers()
					.iterator().next().isAgentClassDistributedType(simulationAgentClass)) {
				@SuppressWarnings("unchecked")
				Class<Object> simulationAgentClazz = simulationAgentClass;
				Iterable<Object> simulationAgentsInSingleClass = nativeRepastContextForThisRun
						.getAgentLayer(simulationAgentClazz);

				/*
				 * if (repastS_SimulationRunContext.getSimulationRunContext()
				 * .getSimulationRunGroupContext() .getSimulationRunGroupConfiguration()
				 * .getSimulationAgentsBelongToOneClass()) {
				 */
				// For a distributed agent class type, for each individual simulation
				// agent, map to
				// an existing free AgentMapping object
				for (Object simulationAgent : simulationAgentsInSingleClass) {
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
		// 1 - Wait for the command from the simulation administrator to start the
		// simulation
		FRAMEWORK_COMMAND fc = repastS_SimulationRunContext
				.readFrameworkMessageFromSimulationAdministrator()
				.getFrameworkToSimulationEngineCommand();
		if (fc != FRAMEWORK_COMMAND.START_SIMULATION)
			throw new CsfMessagingRuntimeException(
					"Did not understand the message from the simulation administrator");

		// 2 - Message the distributed systems that the simulation has started
		// and is ready to accept messages from the distributed agents.
		FrameworkMessage msg = new FrameworkMessageImpl(SYSTEM_TYPE.SIMULATION_ENGINE,
				SYSTEM_TYPE.DISTRIBUTED_SYSTEM,
				repastS_SimulationRunContext.getBlankCachedMessageExchangeTemplate());
		msg.setFrameworkToDistributedSystemCommand(FRAMEWORK_COMMAND.START_SIMULATION);
		// TODO: Loop through the multiple distributed systems
		repastS_SimulationRunContext.messageDistributedSystems(msg,
				repastS_SimulationRunContext.getSimulationRunContext());

		// Wait for distributed system to confirm that simulation is ready
		// to begin
		STATUS st = repastS_SimulationRunContext
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

	/*
	 * @see mapSimulationSideAgent
	 */
	@SuppressWarnings("unused")
	private void mapSimulationSideAgents(Iterable<Object> agentsOfOneType,
			SimulationRunContext simulationRunContext) {
		for (Object simulationAgent : agentsOfOneType) {
			mapSimulationSideAgent(simulationAgent, simulationRunContext);
		}
	}

	/*
	 * After the Simulation and Common Framework are initialized, the Simulation Adaptor
	 * API (or child class) is initialized, and prior to executing a simulation run, this
	 * method must be called to configure the simulation-side of the AgentMappings for one
	 * type (class) of simulation agent. If multiple agent classes are distributed, this
	 * method must be called for each type. This is done prior to the distributed
	 * agent-side mappings.
	 * 
	 * Use this method to send a single Simulation Agent object.
	 * 
	 * @see mapSimulationSideAgents
	 */
	private void mapSimulationSideAgent(Object simulationAgent,
			SimulationRunContext simulationRunContext) {
		simulationAPI.mapSimulationSideAgent(simulationAgent, simulationRunContext);
	}

}
