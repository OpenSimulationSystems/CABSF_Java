package org.simulationsystems.csf.distsys.api;

/*
 * import java.lang.reflect.Constructor; import java.lang.reflect.InvocationTargetException; import
 * java.lang.reflect.Method;
 */

import java.util.HashSet;
import java.util.Set;

import org.simulationsystems.csf.common.csfmodel.SimulationRunGroup;
import org.simulationsystems.csf.common.internal.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.distsys.api.configuration.DistributedSystemSimulationRunConfiguration;
import org.simulationsystems.csf.distsys.api.configuration.DistributedSystemSimulationRunGroupConfiguration;
import org.simulationsystems.csf.distsys.api.simulationruntime.SimulationEngineManager;
import org.simulationsystems.csf.sim.api.configuration.SimulationRunConfiguration;

/*
 * Provides the context for the Common Simulation Framework. Adapter developers may use this context
 * directly, but are encouraged to create separate Simulation-Toolkit-specific context (e.g.,
 * org.simulationsystems.csf.sim.adapters.api.RepastSimphonySimulationFrameworkContext). The benefit
 * is that the API client would be able to utilize native Simulation-Toolkit-specific objects
 * instead of the generic "Object" that is used by this generic Simulation Framework API.
 * 
 * Adapter developers should instantiate this class first before the Simulation-Toolkit-specific
 * Context object.
 */
public class DistributedSystemSimulationRunContext {
	protected DistributedSystemSimulationRunGroupConfiguration distributedSystemSimulationRunGroupConfiguration; // Simulation
	// Run-group-wide

	private SimulationEngineManager simulationEngineManager;
	private SimulationRunGroup simulationRunGroup;
	private DistributedSystemSimulationRunConfiguration distributedSystemSimulationRunConfiguration;

	public DistributedSystemSimulationRunConfiguration getDistributedSystemSimulationRunConfiguration() {
		return distributedSystemSimulationRunConfiguration;
	}

	public void setDistributedSystemSimulationRunConfiguration(
			DistributedSystemSimulationRunConfiguration distributedSystemSimulationRunConfiguration) {
		this.distributedSystemSimulationRunConfiguration = distributedSystemSimulationRunConfiguration;
	}

	/*
	 * Creates the context for the Common Simulation Framework.
	 */
	// protected DistributedSystemSimulationRunContext(String
	// fullyQualifiedClassNameForDistributedAgentManager) {
	protected DistributedSystemSimulationRunContext(SimulationRunGroup simulationRunGroup) {
		this.simulationRunGroup = simulationRunGroup;
	}

	public DistributedSystemSimulationRunGroupConfiguration getDistributedSystemSimulationRunGroupConfiguration() {
		return distributedSystemSimulationRunGroupConfiguration;
	}

	public SimulationRunGroup getSimulationRunGroup() {
		return simulationRunGroup;
	}

	protected void setSimulationConfiguration(
			DistributedSystemSimulationRunGroupConfiguration simulationRunGroupConfiguration) {
		this.distributedSystemSimulationRunGroupConfiguration = simulationRunGroupConfiguration;
	}


	public void cleanup() {

	}

	public void setSimulationEngine(SimulationEngineManager simulationEngineManager) {
		this.simulationEngineManager = simulationEngineManager;

	}

	public void setSimulationRunConfiguration(
			DistributedSystemSimulationRunConfiguration distributedSystemSimulationRunConfigurationSimulationRunConfiguration) {
		this.distributedSystemSimulationRunConfiguration = distributedSystemSimulationRunConfigurationSimulationRunConfiguration;
	}

	public void messageSimulationEngine(FrameworkMessage frameworkMessage,
			DistributedSystemSimulationRunContext distributedSystemSimulationRunContext) {
		simulationEngineManager.sendMessage(frameworkMessage, this);
	}
}
