package org.opensimulationsystems.cabsf.distsys.core.api;

/*
 * import java.lang.reflect.Constructor; import java.lang.reflect.InvocationTargetException; import
 * java.lang.reflect.Method;
 */

import java.util.HashSet;
import java.util.Set;

import org.opensimulationsystems.cabsf.common.model.SimulationRunGroup;
import org.opensimulationsystems.cabsf.common.model.configuration.RunGroupConfiguration;
import org.opensimulationsystems.cabsf.common.model.context.CabsfRunContext;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FrameworkMessage;
import org.opensimulationsystems.cabsf.distsys.core.api.configuration.DistSysRunConfiguration;
import org.opensimulationsystems.cabsf.distsys.core.api.simulationruntime.SimulationEngineManager;
import org.opensimulationsystems.cabsf.distsys.core.api.softwareagents.DistributedAgentsManager;

/*
 * Provides the context for the Common Agent-Based Simulation Framework. Adapter developers may use this context
 * directly, but are encouraged to create separate Simulation-Toolkit-specific context (e.g.,
 * org.opensimulationsystems.cabsf.sim.adapters.simengines.repastS.api.RepastSimphonySimulationFrameworkContext). The benefit
 * is that the API client would be able to utilize native Simulation-Toolkit-specific objects
 * instead of the generic "Object" that is used by this generic Simulation Framework API.
 *
 * Adapter developers should instantiate this class first before the Simulation-Toolkit-specific
 * Context object.
 */
public class DistSysRunContext extends CabsfRunContext {
	/**
	 * <pre>
	 *           0..*     0..*
	 * DistSysRunContext ------------------------- DistSysRunContext
	 *           distSysRunContext1        &lt;       distSysRunContext
	 * </pre>
	 */
	private Set<DistSysRunContext> distSysRunContext;

	/**
	 * <pre>
	 *           0..*     0..*
	 * DistSysRunContext ------------------------- DistSysRunContext
	 *           distSysRunContext        &gt;       distSysRunContext1
	 * </pre>
	 */
	private Set<DistSysRunContext> distSysRunContext1;

	protected RunGroupConfiguration runGroupConfiguration; // Simulation
	// Run-group-wide

	private SimulationEngineManager simulationEngineManager;

	private final SimulationRunGroup simulationRunGroup;

	private DistSysRunConfiguration distSysRunConfiguration;
	private DistributedAgentsManager distributedAgentsManager;
	private DistSysRunGroupContext distSysRunGroupContext;

	/*
	 * Creates the context for the Common Agent-Based Simulation Framework.
	 */
	// protected DistSysRunContext(String
	// fullyQualifiedClassNameForDistributedAgentManager) {
	protected DistSysRunContext(final SimulationRunGroup simulationRunGroup,
			final DistSysRunGroupContext distSysRunGroupContext) {
		this.simulationRunGroup = simulationRunGroup;
		this.distSysRunGroupContext = distSysRunGroupContext;
	}

	public void cleanup() {

	}

	public void closeInterface() {
		simulationEngineManager.closeInterface();
	}

	public DistributedAgentsManager getDistributedAgentsManager() {
		return distributedAgentsManager;
	}

	public RunGroupConfiguration getDistributedSystemRunGroupConfiguration() {
		return runGroupConfiguration;
	}

	public DistSysRunConfiguration getDistSysRunConfiguration() {
		return distSysRunConfiguration;
	}

	public Set<DistSysRunContext> getDistSysRunContext() {
		if (this.distSysRunContext == null) {
			this.distSysRunContext = new HashSet<DistSysRunContext>();
		}
		return this.distSysRunContext;
	}

	public DistSysRunGroupContext getDistSysRunGroupContext() {
		return distSysRunGroupContext;
	}

	public SimulationEngineManager getSimulationEngineManager() {
		return simulationEngineManager;
	}

	public SimulationRunGroup getSimulationRunGroup() {
		return simulationRunGroup;
	}

	public RunGroupConfiguration getSimulationRunGroupConfiguration() {
		return runGroupConfiguration;
	}

	public FrameworkMessage listenForMessageFromSimulationEngine() {
		return simulationEngineManager.listenForMessageFromSimulationEngine();

	}

	public void messageSimulationEngine(final FrameworkMessage frameworkMessage) {
		simulationEngineManager.sendMessage(frameworkMessage, this,
				distSysRunConfiguration.getSimulationEngineID());
	}

	public FrameworkMessage requestEnvironmentInformation() {
		return simulationEngineManager.requestEnvironmentInformation();

	}

	public void setDistributedAgentsManager(
			final DistributedAgentsManager distributedAgentsManager) {
		this.distributedAgentsManager = distributedAgentsManager;
	}

	public void setDistSysRunConfiguration(
			final DistSysRunConfiguration distSysRunConfiguration) {
		this.distSysRunConfiguration = distSysRunConfiguration;
	}

	public void setDistSysRunGroupContext(
			final DistSysRunGroupContext distSysRunGroupContext) {
		this.distSysRunGroupContext = distSysRunGroupContext;
	}

	protected void setSimulationEngine(
			final SimulationEngineManager simulationEngineManager) {
		this.simulationEngineManager = simulationEngineManager;

	}

	protected void setSimulationRunConfiguration(
			final DistSysRunConfiguration distributedSystemSimulationRunConfigurationSimulationRunConfiguration) {
		this.distSysRunConfiguration = distributedSystemSimulationRunConfigurationSimulationRunConfiguration;
	}

	protected void setSimulationRunGroupConfiguration(
			final RunGroupConfiguration runGroupConfiguration) {
		this.runGroupConfiguration = runGroupConfiguration;
	}

}
