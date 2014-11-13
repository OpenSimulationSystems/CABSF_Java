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
import org.simulationsystems.csf.distsys.api.distributedautonomousagents.DistributedAutonomousAgent;
import org.simulationsystems.csf.distsys.api.simulationruntime.SimulationEngineManager;
import org.simulationsystems.csf.sim.api.configuration.SimulationRunConfiguration;

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
	private DistributedAutonomousAgent distributedAutonomousAgent;
	private SimulationRunGroup simulationRunGroup;
	private DistSysRunConfiguration distSysRunConfiguration;

	public DistributedAutonomousAgent getDistributedAutonomousAgentManager() {
		return distributedAutonomousAgent;
	}

	protected void setDistributedAutonomousAgentManager(
			DistributedAutonomousAgent distributedAutonomousAgent) {
		this.distributedAutonomousAgent = distributedAutonomousAgent;
	}


	public DistSysRunConfiguration getDistSysRunConfiguration() {
		return distSysRunConfiguration;
	}

	protected void setDistSysRunConfiguration(
			DistSysRunConfiguration distSysRunConfiguration) {
		this.distSysRunConfiguration = distSysRunConfiguration;
	}

	/*
	 * Creates the context for the Common Simulation Framework.
	 */
	// protected DistSysRunContext(String
	// fullyQualifiedClassNameForDistributedAgentManager) {
	protected DistSysRunContext(SimulationRunGroup simulationRunGroup) {
		this.simulationRunGroup = simulationRunGroup;
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

	public void messageSimulationEngine(FrameworkMessage frameworkMessage,
			DistSysRunContext distSysRunContext) {
		simulationEngineManager.sendMessage(frameworkMessage, this);
	}
}
