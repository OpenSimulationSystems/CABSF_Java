package org.simulationsystems.csf.distsys.adapters.jade.api;

import java.util.HashSet;
import java.util.Set;

import org.jdom2.Document;
import org.jdom2.Element;
import org.simulationsystems.csf.common.csfmodel.SimulationRunGroup;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FRAMEWORK_COMMAND;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.distsys.core.api.DistSysRunContext;
import org.simulationsystems.csf.distsys.core.api.configuration.DistSysRunGroupConfiguration;
import org.simulationsystems.csf.distsys.core.api.distributedautonomousagents.DistributedAutonomousAgent;
import org.simulationsystems.csf.sim.core.api.SimulationRunContext;
import org.simulationsystems.csf.sim.core.api.distributedsystems.SimulationDistributedSystemManager;

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

	
	public Element getCachedAgentModelActorTemplate() {
		return this.getDistSysRunContext().getDistSysRunGroupContext()
				.getCachedAgentModelActorTemplate();
	}
	
	public Element getCachedLocationTemplate() {
		return this.getDistSysRunContext().getDistSysRunGroupContext()
				.getCachedLocationTemplate();
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

	public Object getCurrentJADE_Context() {
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


	public FrameworkMessage listenForMessageFromSimulationEngine() {
		return getDistSysRunContext().listenForMessageFromSimulationEngine();

	}
	
	public void closeInterface(DistSysRunContext distSysRunContext) {
		distSysRunContext.closeInterface();
	}
	
	//FIXME: Need this?
	public FrameworkMessage requestEnvironmentInformation() {
		 getDistSysRunContext().requestEnvironmentInformation();
		return null;
		
	}

	public void initializeAgentMappings() {
		// TODO Auto-generated method stub
		
	}
}
