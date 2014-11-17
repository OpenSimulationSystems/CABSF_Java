package org.simulationsystems.csf.distsys.api;

/*
 * import java.lang.reflect.Constructor; import java.lang.reflect.InvocationTargetException; import
 * java.lang.reflect.Method;
 */

import java.util.HashSet;
import java.util.Set;

import org.simulationsystems.csf.common.csfmodel.SimulationRunGroup;
import org.simulationsystems.csf.common.internal.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.distsys.api.configuration.DistSysRunConfiguration;
import org.simulationsystems.csf.distsys.api.configuration.DistSysRunGroupConfiguration;
import org.simulationsystems.csf.distsys.api.distributedautonomousagents.DistributedAgentsManager;
import org.simulationsystems.csf.distsys.api.distributedautonomousagents.DistributedAutonomousAgent;
import org.simulationsystems.csf.distsys.api.simulationruntime.SimulationEngineManager;
import org.simulationsystems.csf.sim.api.SimulationRunContext;
import org.simulationsystems.csf.sim.api.configuration.SimulationRunConfiguration;
import org.simulationsystems.csf.sim.api.distributedsystems.SimulationDistributedSystemManager;

/*
 * Provides the context for the Common Simulation Framework. Adapter developers may use this context
 * directly, but are encouraged to create separate Simulation-Toolkit-specific context (e.g.,
 * org.simulationsystems.csf.sim.adapters.api.repastS.RepastSimphonySimulationFrameworkContext). The benefit
 * is that the API client would be able to utilize native Simulation-Toolkit-specific objects
 * instead of the generic "Object" that is used by this generic Simulation Framework API.
 * 
 * Adapter developers should instantiate this class first before the Simulation-Toolkit-specific
 * Context object.
 */
public class DistSysRunContext {
	protected DistSysRunGroupConfiguration distSysRunGroupConfiguration; // Simulation
	// Run-group-wide

	private SimulationEngineManager simulationEngineManager;
	private SimulationRunGroup simulationRunGroup;
	private DistSysRunConfiguration distSysRunConfiguration;
	private DistributedAgentsManager distributedAgentsManager;

	private DistSysRunGroupContext distSysRunGroupContext;

	public DistributedAgentsManager getDistributedAgentsManager() {
		return distributedAgentsManager;
	}

	public void setDistributedAgentsManager(DistributedAgentsManager distributedAgentsManager) {
		this.distributedAgentsManager = distributedAgentsManager;
	}

	public DistSysRunConfiguration getDistSysRunConfiguration() {
		return distSysRunConfiguration;
	}

	public DistSysRunGroupContext getDistSysRunGroupContext() {
		return distSysRunGroupContext;
	}

	public void setDistSysRunConfiguration(DistSysRunConfiguration distSysRunConfiguration) {
		this.distSysRunConfiguration = distSysRunConfiguration;
	}

	/*
	 * Creates the context for the Common Simulation Framework.
	 */
	// protected DistSysRunContext(String
	// fullyQualifiedClassNameForDistributedAgentManager) {
	protected DistSysRunContext(SimulationRunGroup simulationRunGroup, DistSysRunGroupContext distSysRunGroupContext) {
		this.simulationRunGroup = simulationRunGroup;
		this.distSysRunGroupContext = distSysRunGroupContext;
	}

	public DistSysRunGroupConfiguration getDistributedSystemRunGroupConfiguration() {
		return distSysRunGroupConfiguration;
	}

	public SimulationRunGroup getSimulationRunGroup() {
		return simulationRunGroup;
	}

	protected void setDistSysConfiguration(
			DistSysRunGroupConfiguration distSysRunGroupConfiguration) {
		this.distSysRunGroupConfiguration = distSysRunGroupConfiguration;
	}


	public void cleanup() {

	}

	protected void setSimulationEngine(SimulationEngineManager simulationEngineManager) {
		this.simulationEngineManager = simulationEngineManager;

	}

	protected void setSimulationRunConfiguration(
			DistSysRunConfiguration distributedSystemSimulationRunConfigurationSimulationRunConfiguration) {
		this.distSysRunConfiguration = distributedSystemSimulationRunConfigurationSimulationRunConfiguration;
	}
	
	public void messageSimulationEngine(FrameworkMessage frameworkMessage) {
		simulationEngineManager.sendMessage(frameworkMessage, this);
	}
	
	public void closeInterface() {
		distributedAgentsManager.closeInterface();
	}

	public void listenForCommandsFromSimulationAdministrator() {
		simulationEngineManager.listenForCommandsFromSimulationAdministrator();
		
	}
}
