package org.simulationsystems.csf.distsys.adapters.jade.api;

import java.io.IOException;
import java.util.Set;

import org.simulationsystems.csf.distsys.adapters.jade.api.mocks.MockHumanJADE_Agent;
import org.simulationsystems.csf.distsys.adapters.jade.api.mocks.NativeJADEMockContext;
import org.simulationsystems.csf.distsys.core.api.DistSysRunContext;
import org.simulationsystems.csf.distsys.core.api.DistSysRunGroupContext;
import org.simulationsystems.csf.distsys.core.api.DistributedSystemAPI;

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
public class JADE_MAS_AdapterAPI {
	private DistributedSystemAPI distributedSystemAPI = DistributedSystemAPI
			.getInstance();
	private String distributedSystemNameToSetInDistributedSystemAPI = "JADE";
	// private String fullyQualifiedClassNameForDistributedAgentManager =
	// "org.simulationsystems.csf.sim.engines.adapters.repastS.api.distributedagents.RepastSimphonySimulationDistributedAgentManager";

	private static JADE_MAS_AdapterAPI instance = new JADE_MAS_AdapterAPI();

	/*
	 * Use JADE_DistributedSystemAdapterAPI.getInstance().
	 */
	private JADE_MAS_AdapterAPI() {
		super();
	}

	/*
	 * This method should be called afterRepastSimphonySimulationAdapterAPI.getInstance()
	 * to initialize the common framework simulation based on the supplied configuration
	 * properties.
	 * 
	 * @ param String The path to the Common Simulation Configuration File
	 */
	public JADE_MAS_RunGroupContext initializeAPI(String frameworkConfigurationFileName)
			throws IOException {

		DistSysRunGroupContext distSysRunGroupContext = distributedSystemAPI
				.initializeAPI(frameworkConfigurationFileName,
						distributedSystemNameToSetInDistributedSystemAPI);

		// Set the JADE-specific objects, using the Decorator Pattern
		JADE_MAS_RunGroupContext jade_MAS_RunGroupContext = new JADE_MAS_RunGroupContext(
				distSysRunGroupContext);

		return jade_MAS_RunGroupContext;
	}

	// private DistSysRunContext
	/**
	 * 
	 * The API singleton for clients that are simulation systems adapters to into the
	 * common simulation framework
	 * 
	 */
	public static JADE_MAS_AdapterAPI getInstance() {
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
	// TODO: Wait for all distributed agents to join. For now, all need to be running by
	// the time
	// that this agent receives the message that the simulation has started.
	public JADE_MAS_RunContext initializeSimulationRun(
			NativeJADEMockContext nativeJadeContextForThisRun,
			JADE_MAS_RunGroupContext jade_MAS_RunGroupContext) {

		DistSysRunContext distSysRunContext = distributedSystemAPI
				.initializeSimulationRun(nativeJadeContextForThisRun,
						jade_MAS_RunGroupContext.getDistSysRunGroupContext());

		// User Decorator Pattern for JADE_DistSysRunContext
		JADE_MAS_RunContext jADE_MAS_RunContext = new JADE_MAS_RunContext(
				distSysRunContext);
		jADE_MAS_RunContext.setJadeContextForThisRun(nativeJadeContextForThisRun);

		// jADE_MAS_RunContext.getDistSysRunContext().getSimulationEngineManager().initializeAgentMappings();

		// LOW: Support multiple Simulation Run Groups. For now just assume that there's
		// one.
		jADE_MAS_RunContext.getDistSysRunContext().getDistributedAgentsManager()
				.initializeDistributedAutonomousAgents();

		assignJadeAgentsToDistributedAutonomousAgents(
				nativeJadeContextForThisRun.getMockJADE_Agents(), jADE_MAS_RunContext);

		return jADE_MAS_RunContext;
	}

	/*
	 * @see mapSimulationSideAgent
	 */
	@SuppressWarnings("unused")
	private void assignJadeAgentsToDistributedAutonomousAgents(
			Set<MockHumanJADE_Agent> jadeAgents, JADE_MAS_RunContext jADE_MAS_RunContext) {
		for (MockHumanJADE_Agent jadeAgent : jadeAgents) {
			assignJadeAgentToDistributedAutonomousAgent(jadeAgent, jADE_MAS_RunContext);
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
	private void assignJadeAgentToDistributedAutonomousAgent(
			MockHumanJADE_Agent jadeAgent, JADE_MAS_RunContext jADE_MAS_RunContext) {
		distributedSystemAPI.assignNativeDistributedAutonomousAgent(jadeAgent,
				jADE_MAS_RunContext.getDistSysRunContext());
	}

}
