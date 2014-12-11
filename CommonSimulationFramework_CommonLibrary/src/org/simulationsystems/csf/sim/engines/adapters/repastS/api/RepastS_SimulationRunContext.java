package org.simulationsystems.csf.sim.engines.adapters.repastS.api;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jdom2.Document;
import org.jdom2.Element;
import org.simulationsystems.csf.common.csfmodel.SimulationRunGroup;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.sim.core.api.SimulationRunContext;
import org.simulationsystems.csf.sim.core.api.SimulationRunGroupContext;
import org.simulationsystems.csf.sim.core.api.configuration.SimulationRunGroupConfiguration;
import org.simulationsystems.csf.sim.core.api.distributedsystems.SimulationDistributedSystemManager;

import repast.simphony.context.Context;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;
import repast.simphony.util.SimUtilities;

/*
 * Provides the context for the Common Simulation Framework. This Simulation-Toolkit-specific
 * context mirrors the generic DistSysRunContext provided by the Common Framework API. It enables
 * API users to get native Simulation-Toolkit objects instead of generic "Object"s. This aids the
 * API client at compile time. The simulation context is created for the entire simulation run
 * group, unlike in Repast where the simulation context exists per simulation run. Adapter
 * developers should first instantiate DistSysRunContext, before instantiating a
 * Simulation-Toolkit-specific Context such as this class.
 */
public class RepastS_SimulationRunContext {
	private SimulationRunContext simulationRunContext;
	Context<Object> repastS_ContextForThisRun;
	// TODO: Move this up to the main API level?
	Set<SimulationDistributedSystemManager> simulationDistributedSystemManagers = new HashSet<SimulationDistributedSystemManager>();
	private RepastS_SimulationRunGroupContext repastS_SimulationRunGroupContext;

	public SimulationRunContext getSimulationRunContext() {
		return simulationRunContext;
	}

	/*
	 * Convenience method to get the cached messgae exchange template
	 */
	public Document getCachedMessageExchangeTemplate() {
		return this.getSimulationRunContext().getSimulationRunGroupContext()
				.getCachedMessageExchangeTemplate();
	}
	
	public Element getCachedAgentModelActorTemplate() {
		return this.getSimulationRunContext().getSimulationRunGroupContext()
				.getCachedAgentModelActorTemplate();
	}

	/*
	 * Use the other constructor
	 */
	@SuppressWarnings("unused")
	private RepastS_SimulationRunContext() {

	}

	public RepastS_SimulationRunContext(SimulationRunContext simulationRunContext) {
		this.simulationRunContext = simulationRunContext;

		// TODO: Make initialized based on configuration. For now, hard code one
		// distributed system.
		// TODO: Handle multiple distributed systems
		// TODO: Move this to the main API level? Same for the distributed side?
		SimulationDistributedSystemManager dam = simulationRunContext
				.getSimulationDistributedSystemManagers().iterator().next();
		simulationDistributedSystemManagers.add(dam);
	}

	public SimulationRunGroupConfiguration getSimulationRunGroupConfiguration() {
		return simulationRunContext.getSimulationRunGroupConfiguration();
	}

	public SimulationRunGroup getSimulationRunGroup() {
		return simulationRunContext.getSimulationRunGroup();
	}

	/*
	 * 
	 */
	/*
	 * LOW: Add the ability to support many simultaneous "Context"s
	 */
	protected void setRepastContextForThisRun(Context<Object> repastS_ContextForThisRun) {
		this.repastS_ContextForThisRun = repastS_ContextForThisRun;

	}

	public Context<Object> getCurrentRepastContext() {
		return repastS_ContextForThisRun;
	}

	/*
	 * Returns the Simulation Distributed Agent Managers.
	 */
	public Set<SimulationDistributedSystemManager> getSimulationDistributedSystemManagers() {
		return simulationDistributedSystemManagers;
	}

	public void messageDistributedSystems(FrameworkMessage frameworkMessage,
			SimulationRunContext simulationRunContext) {
		simulationRunContext.messageDistributedSystems(frameworkMessage);
	}

	public FrameworkMessage readFrameworkMessageFromSimulationAdministrator() {
		return getSimulationRunContext()
				.readFrameworkMessageFromSimulationAdministrator();
	}

	public FrameworkMessage readFrameworkMessageFromDistributedSystem() {
		return getSimulationRunContext().readFrameworkMessageFromDistributedSystem();
	}

	public void closeInterface(SimulationRunContext simulationRunContext) {
		simulationRunContext.closeInterface();
	}

	public void setRepastRunGroupContext(
			RepastS_SimulationRunGroupContext repastS_SimulationRunGroupContext) {
		this.repastS_SimulationRunGroupContext = repastS_SimulationRunGroupContext;
	}

	public RepastS_SimulationRunGroupContext getRepastS_SimulationRunGroupContext() {
		return repastS_SimulationRunGroupContext;
	}

}
