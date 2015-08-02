package org.opensimulationsystems.cabsf.sim.adapters.simengines.repastS.api;

import java.util.HashSet;
import java.util.Set;

import org.jdom2.Document;
import org.jdom2.Element;
import org.opensimulationsystems.cabsf.common.model.SYSTEM_TYPE;
import org.opensimulationsystems.cabsf.common.model.SimulationRunGroup;
import org.opensimulationsystems.cabsf.common.model.configuration.RunGroupConfiguration;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FRAMEWORK_COMMAND;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FrameworkMessage;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FrameworkMessageImpl;
import org.opensimulationsystems.cabsf.sim.core.api.SimulationRunContext;
import org.opensimulationsystems.cabsf.sim.core.api.distributedsystems.SimulationDistributedSystemManager;

import repast.simphony.context.Context;

/**
 * Provides the context for the Common Agent-Based Simulation Framework. This
 * Simulation-Toolkit-specific context mirrors the generic DistSysRunContext provided by
 * the Common Framework API. It enables API users to get native Simulation-Toolkit objects
 * instead of generic "Object"s. This aids the API client at compile time. The simulation
 * context is created for the entire simulation run group, unlike in Repast where the
 * simulation context exists per simulation run. Adapter developers should first
 * instantiate DistSysRunContext, before instantiating a Simulation-Toolkit-specific
 * Context such as this class.
 *
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class RepastS_SimulationRunContext {

	/** The simulation run context. */
	private SimulationRunContext simulationRunContext;

	/** The repast s_ context for this run. */
	Context<Object> repastS_ContextForThisRun;

	// TODO: Move this up to the main API level?
	/** The simulation distributed system managers. */
	Set<SimulationDistributedSystemManager> simulationDistributedSystemManagers = new HashSet<SimulationDistributedSystemManager>();

	/** The repast s_ simulation run group context. */
	private RepastS_SimulationRunGroupContext repastS_SimulationRunGroupContext;

	/**
	 * Instantiates a new repast s_ simulation run context.
	 */
	@SuppressWarnings("unused")
	private RepastS_SimulationRunContext() {

	}

	/**
	 * Instantiates a new repast s_ simulation run context.
	 *
	 * @param simulationRunContext
	 *            the simulation run context
	 */
	public RepastS_SimulationRunContext(final SimulationRunContext simulationRunContext) {
		this.simulationRunContext = simulationRunContext;

		// TODO: Make initialized based on configuration. For now, hard code one
		// distributed system.
		// TODO: Handle multiple distributed systems
		// TODO: Move this to the main API level? Same for the distributed side?
		final SimulationDistributedSystemManager dam = simulationRunContext
				.getSimulationDistributedSystemManagers().iterator().next();
		simulationDistributedSystemManagers.add(dam);
	}

	/**
	 * Close interface.
	 *
	 * @param simulationRunContext
	 *            the simulation run context
	 */
	public void closeInterface(final SimulationRunContext simulationRunContext) {
		simulationRunContext.closeInterface();
	}

	// TODO: These can probably be removed now that we added the abstract AgentContext
	/**
	 * Gets the blank cached message exchange template.
	 *
	 * @return the blank cached message exchange template
	 */
	public Document getBlankCachedMessageExchangeTemplate() {
		return this.getSimulationRunContext().getSimulationRunGroupContext()
				.getBlankCachedMessageExchangeTemplate();
	}

	/**
	 * Gets the cached agent model actor template.
	 *
	 * @return the cached agent model actor template
	 */
	public Element getCachedAgentModelActorTemplate() {
		return this.getSimulationRunContext().getSimulationRunGroupContext()
				.getCachedAgentModelActorTemplate();
	}

	/**
	 * Gets the cached distributed autonomous agent template.
	 *
	 * @return the cached distributed autonomous agent template
	 */
	public Element getCachedDistributedAutonomousAgentTemplate() {
		return this.getSimulationRunContext().getSimulationRunGroupContext()
				.getCachedAgentModelActorTemplate();
	}

	/**
	 * Gets the cached location template.
	 *
	 * @return the cached location template
	 */
	public Element getCachedLocationTemplate() {
		return this.getSimulationRunContext().getSimulationRunGroupContext()
				.getCachedLocationTemplate();
	}

	/**
	 * Gets the current repast context.
	 *
	 * @return the current repast context
	 */
	public Context<Object> getCurrentRepastContext() {
		return repastS_ContextForThisRun;
	}

	/**
	 * Gets the repast s_ simulation run group context.
	 *
	 * @return the repast s_ simulation run group context
	 */
	public RepastS_SimulationRunGroupContext getRepastS_SimulationRunGroupContext() {
		return repastS_SimulationRunGroupContext;
	}

	/*
	 * Returns the Simulation Distributed Agent Managers.
	 */
	/**
	 * Gets the simulation distributed system managers.
	 *
	 * @return the simulation distributed system managers
	 */
	public Set<SimulationDistributedSystemManager> getSimulationDistributedSystemManagers() {
		return simulationDistributedSystemManagers;
	}

	/**
	 * Gets the simulation run context.
	 *
	 * @return the simulation run context
	 */
	public SimulationRunContext getSimulationRunContext() {
		return simulationRunContext;
	}

	/**
	 * Gets the simulation run group.
	 *
	 * @return the simulation run group
	 */
	public SimulationRunGroup getSimulationRunGroup() {
		return simulationRunContext.getSimulationRunGroup();
	}

	/**
	 * Gets the simulation run group configuration.
	 *
	 * @return the simulation run group configuration
	 */
	public RunGroupConfiguration getSimulationRunGroupConfiguration() {
		return simulationRunContext.getSimulationRunGroupContext()
				.getSimulationRunGroupConfiguration();
	}

	/**
	 * Message distributed systems.
	 *
	 * @param frameworkMessage
	 *            the framework message
	 * @param simulationRunContext
	 *            the simulation run context
	 */
	public void messageDistributedSystems(final FrameworkMessage frameworkMessage,
			final SimulationRunContext simulationRunContext) {
		simulationRunContext.messageDistributedSystems(frameworkMessage);
	}

	/**
	 * Read framework message from distributed system.
	 *
	 * @return the framework message
	 */
	public FrameworkMessage readFrameworkMessageFromDistributedSystem() {
		return getSimulationRunContext().readFrameworkMessageFromDistributedSystem();
	}

	/**
	 * Read framework message from simulation administrator.
	 *
	 * @return the framework message
	 */
	public FrameworkMessage readFrameworkMessageFromSimulationAdministrator() {
		return getSimulationRunContext()
				.readFrameworkMessageFromSimulationAdministrator();
	}

	/*
	 * LOW: Add the ability to support many simultaneous "Context"s
	 */
	/**
	 * Sets the repast context for this run.
	 *
	 * @param repastS_ContextForThisRun
	 *            the new repast context for this run
	 */
	protected void setRepastContextForThisRun(
			final Context<Object> repastS_ContextForThisRun) {
		this.repastS_ContextForThisRun = repastS_ContextForThisRun;

	}

	/**
	 * Sets the repast run group context.
	 *
	 * @param repastS_SimulationRunGroupContext
	 *            the new repast run group context
	 */
	public void setRepastRunGroupContext(
			final RepastS_SimulationRunGroupContext repastS_SimulationRunGroupContext) {
		this.repastS_SimulationRunGroupContext = repastS_SimulationRunGroupContext;
	}

	/**
	 * Terminate simulation run.
	 */
	public void terminateSimulationRun() {
		// 2 - Message the distributed systems that the simulation has started
		// and is ready to accept messages from the distributed agents.
		final FrameworkMessage msg = new FrameworkMessageImpl(
				SYSTEM_TYPE.SIMULATION_ENGINE, SYSTEM_TYPE.DISTRIBUTED_SYSTEM,
				getBlankCachedMessageExchangeTemplate());
		msg.setFrameworkToDistributedSystemCommand(FRAMEWORK_COMMAND.STOP_SIMULATION);
		// TODO: Loop through the multiple distributed systems
		messageDistributedSystems(msg, getSimulationRunContext());

	}

}
