package org.simulationsystems.csf.distsys.adapters.api.jade;

import java.util.HashSet;
import java.util.Set;

import org.jdom2.Document;
import org.simulationsystems.csf.common.csfmodel.FRAMEWORK_COMMAND;
import org.simulationsystems.csf.common.csfmodel.SimulationRunGroup;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.distsys.api.DistSysRunContext;
import org.simulationsystems.csf.distsys.api.configuration.DistSysRunGroupConfiguration;
import org.simulationsystems.csf.distsys.api.distributedautonomousagents.DistributedAutonomousAgent;
import org.simulationsystems.csf.sim.api.SimulationRunContext;
import org.simulationsystems.csf.sim.api.distributedsystems.SimulationDistributedSystemManager;

import repast.simphony.context.Context;

/*
 * Provides the context for the Common Simulation Framework. This Simulation-Toolkit-specific
 * context mirrors the generic DistSysRunContext provided by the Common Framework API. It enables
 * API users to get native Simulation-Toolkit objects instead of generic "Object"s. This aids the
 * API client at compile time. The simulation context is created for the entire simulation run
 * group, unlike in Repast where the simulation context exists per simulation run. Adapter
 * developers should first instantiate DistSysRunContext, before instantiating a
 * Simulation-Toolkit-specific Context such as this class.
 */
public class JADE_MAS_RunContext {
	private DistSysRunContext distSysRunContext;
	Object jade_ContextForThisRun;

	public DistSysRunContext getDistSysRunContext() {
		return distSysRunContext;
	}

	/*
	 * Use the other constructor
	 */
	@SuppressWarnings("unused")
	private JADE_MAS_RunContext() {

	}
	
	/*
	 * Uses the decorator pattern to set up a custom JADE MAS RunContext using the general DistSystRunContext object.
	 */
	public JADE_MAS_RunContext(DistSysRunContext distSysRunContext) {
		this.distSysRunContext = distSysRunContext;

		// TODO: Make initialized based on configuration. For now, hard code one distributed system.
	}

	public DistSysRunGroupConfiguration getDistSysRunGroupConfiguration() {
		return distSysRunContext.getDistributedSystemRunGroupConfiguration();
	}

	public SimulationRunGroup getSimulationRunGroup() {
		return distSysRunContext.getSimulationRunGroup();
	}

	/*
	 * 
	 */
	/*
	 * LOW: Add the ability to support many simultaneous "Context"s
	 */
	public void setJadeContextForThisRun(Object jade_ContextForThisRun) {
		this.jade_ContextForThisRun = jade_ContextForThisRun;

	}

	public Object getCurrentRepastContext() {
		return jade_ContextForThisRun;
	}

	/*
	 * Convenience method to get the cached messgae exchange template
	 */
	public Document getCachedMessageExchangeTemplate() {
		return this.getDistSysRunContext().getDistSysRunGroupContext()
				.getCachedMessageExchangeTemplate();
	}

	public void messageSimulationEngine(FrameworkMessage frameworkMessage,
			DistSysRunContext distSysRunContext) {
		distSysRunContext.messageSimulationEngine(frameworkMessage);
	}

	public FRAMEWORK_COMMAND listenForCommandsFromSimulationEngine() {
		return getDistSysRunContext().listenForCommandsFromSimulationEngine();
	}

	public FrameworkMessage listenForMessageFromSimulationEngine() {
		return getDistSysRunContext().listenForMessageFromSimulationEngine();

	}
	
	public void closeInterface(DistSysRunContext distSysRunContext) {
		distSysRunContext.closeInterface();
	}
	
	public FrameworkMessage requestEnvironmentInformation() {
		 getDistSysRunContext().requestEnvironmentInformation();
		return null;
		
	}
}
