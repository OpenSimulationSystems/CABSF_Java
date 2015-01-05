package org.simulationsystems.csf.distsys.adapters.jade.api;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.jdom2.Document;
import org.jdom2.Element;
import org.simulationsystems.csf.common.csfmodel.SYSTEM_TYPE;
import org.simulationsystems.csf.common.csfmodel.csfexceptions.CsfInitializationRuntimeException;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FRAMEWORK_COMMAND;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessageImpl;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.STATUS;
import org.simulationsystems.csf.common.internal.messaging.xml.XMLUtilities;
import org.simulationsystems.csf.distsys.adapters.jade.api.nativeagents.NativeDistributedAutonomousAgent;
import org.simulationsystems.csf.distsys.adapters.jade.api.nativeagents.NativeJADEMockContext;
import org.simulationsystems.csf.distsys.core.api.DistSysRunContext;
import org.simulationsystems.csf.distsys.core.api.DistSysRunGroupContext;
import org.simulationsystems.csf.distsys.core.api.DistributedSystemAPI;
import org.simulationsystems.csf.distsys.core.api.distributedautonomousagents.DistributedAgentModel;
import org.simulationsystems.csf.distsys.core.api.distributedautonomousagents.DistributedAgentsManager;
import org.simulationsystems.csf.distsys.core.api.distributedautonomousagents.DistributedAutonomousAgent;

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
	private JADE_MAS_RunContext jade_MAS_RunContext;
	private JadeController jadeControllerAgent;

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
			JADE_MAS_RunGroupContext jade_MAS_RunGroupContext,
			JadeController jadeControllerAgent,
			Set<NativeDistributedAutonomousAgent> nativeAgentsSet) {
		this.jadeControllerAgent = jadeControllerAgent;

		DistSysRunContext distSysRunContext = distributedSystemAPI
				.initializeSimulationRun(nativeJadeContextForThisRun,
						jade_MAS_RunGroupContext.getDistSysRunGroupContext());

		// User Decorator Pattern for JADE_DistSysRunContext
		jade_MAS_RunContext = new JADE_MAS_RunContext(distSysRunContext);
		jade_MAS_RunContext.setJadeContextForThisRun(nativeJadeContextForThisRun);

		// jade_MAS_RunContext.getDistSysRunContext().getSimulationEngineManager().initializeAgentMappings();

		// LOW: Support multiple Simulation Run Groups. For now just assume that there's
		// one.
		jade_MAS_RunContext
				.getDistSysRunContext()
				.getDistributedAgentsManager()
				.initializeDistributedAutonomousAgents(nativeJadeContextForThisRun,
						nativeAgentsSet);

		// TODO: Remove these old set of assign methods
		/*
		 * assignJadeAgentsToDistributedAutonomousAgents(
		 * nativeJadeContextForThisRun.getMockJADE_Agents(), jade_MAS_RunContext)
		 */;

		// Listen for START_SIMULATION command from the simulation engine
		FrameworkMessage msg = jade_MAS_RunContext.listenForMessageFromSimulationEngine();
		System.out
				.println("[JADE Controller Agent] Received framework message from the simulation engine: "
						+ XMLUtilities.convertDocumentToXMLString(msg.getDocument(), true));
		FRAMEWORK_COMMAND fc = msg.getFrameworkToDistributedSystemCommand();
		// TODO: Better error handling. Send a message back to the simulation engine that
		// this distributed system is terminating

		if (fc == null || !fc.equals(FRAMEWORK_COMMAND.START_SIMULATION)) {
			String fcStr = "null";
			if (fc != null)
				fcStr = fc.toString();
			throw new CsfInitializationRuntimeException(
					"The JADE Controller Agent tried to read message from the simulation engine, but did not understand the command: "
							+ fcStr
							+ " It's possible that a previous simulation didn't complete, and a message was left in the Redis cache, which has been picked up in this new simulation run.  Next time try flushing the Redis cache if the simulation does not end normally, before running a new simulation.");
		}

		// Push Status of Ready back to the simulation engine
		FrameworkMessage fm = new FrameworkMessageImpl(SYSTEM_TYPE.DISTRIBUTED_SYSTEM,
				SYSTEM_TYPE.SIMULATION_ENGINE,
				jade_MAS_RunContext.getCachedMessageExchangeTemplate());
		fm.setStatus(STATUS.READY_TO_START_SIMULATION);
		jade_MAS_RunContext.messageSimulationEngine(fm,
				jade_MAS_RunContext.getDistSysRunContext());

		// TODO:Move all of this up one API level
		// Get the tick information from the simulation engine
		// Distribute message to the JADE Agent
		jade_MAS_RunContext.setDam(jade_MAS_RunContext.getDistSysRunContext()
				.getDistributedAgentsManager());

		jade_MAS_RunContext.setJadeControllerAgent(jadeControllerAgent);

		return jade_MAS_RunContext;
	}

	/*
	 * @see mapSimulationSideAgent
	 */
	@SuppressWarnings("unused")
	private void assignJadeAgentsToDistributedAutonomousAgents(
			Set<NativeDistributedAutonomousAgent> jadeAgents,
			JADE_MAS_RunContext jade_MAS_RunContext) {
		for (NativeDistributedAutonomousAgent jadeAgent : jadeAgents) {
			assignJadeAgentToDistributedAutonomousAgent(jadeAgent, jade_MAS_RunContext);
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
			NativeDistributedAutonomousAgent jadeAgent,
			JADE_MAS_RunContext jade_MAS_RunContext) {
		distributedSystemAPI.assignNativeDistributedAutonomousAgent(jadeAgent,
				jade_MAS_RunContext.getDistSysRunContext());
	}

}
