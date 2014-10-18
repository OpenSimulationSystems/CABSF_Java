package org.simulationsystems.simulationframework.simulation.adapters.api;

/*import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;*/

import org.simulationsystems.simulationframework.simulation.adapters.api.distributedagents.SimulationDistributedAgentManager;

/*
 * Provides the context for the Common Simulation Framework.  Adapter developers may use this context directly, but are encouraged to create separate Simulation-Toolkit-specific context (e.g., org.simulationsystems.simulationframework.simulation.adapters.simulationapps.api.RepastSimphonySimulationFrameworkContext).  The benefit is that the API client would be able to utilize native Simulation-Toolkit-specific objects instead of the generic "Object" that is used by this generic Simulation Framework API.
 * 
 * Adapter developers should instantiate this class first before the Simulation-Toolkit-specific Context object.
 */
public class SimulationFrameworkContext {
	protected SimulationConfiguration simulationConfiguration; //Simulation Run-group-wide
	private SimulationDistributedAgentManager simulationDistributedAgentManager;
	private SimulationRunGroup currentSimulationRunGroup;
	private Object simulationToolContext;

	/*
	 * Convenience method to return the context of the Simulation Software. For example, for Repast
	 * Simphony, this method would return a repast.simphony.context.Context<T>
	 */
	public Object getSimulationToolContext() {
		return simulationToolContext;
	}

	protected void setSimulationToolContext(Object simulationToolContext) {
		this.simulationToolContext = simulationToolContext;
	}

	/*
	 * Creates the context for the Common Simulation Framework.
	 */
	//protected SimulationFrameworkContext(String fullyQualifiedClassNameForDistributedAgentManager) {
		protected SimulationFrameworkContext() {

		// Check if the Adaptor author is providing a custom DistributedAgentManager
		// TODO: Add exception/return if unable to create object or cast object
		// Class<?> c;
/*		if (fullyQualifiedClassNameForDistributedAgentManager != null) {
			try {
				Class<?> cl = Class.forName(fullyQualifiedClassNameForDistributedAgentManager);
				Constructor<?> cons = cl.getConstructor(cl.getClass());
				simulationDistributedAgentManager = (SimulationDistributedAgentManager) cons
						.newInstance(this);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} else
			*/
			simulationDistributedAgentManager = new SimulationDistributedAgentManager(this);
	}

	public SimulationDistributedAgentManager getCurrentSimulationDistributedAgentManager() {
		return simulationDistributedAgentManager;
	}

	public void setCurrentSimulationDistributedAgentManager(
			SimulationDistributedAgentManager simulationDistributedAgentManager) {
		this.simulationDistributedAgentManager = simulationDistributedAgentManager;
	}

	public SimulationConfiguration getSimulationConfiguration() {
		return simulationConfiguration;
	}

	public SimulationRunGroup getSimulationRunGroup() {
		return currentSimulationRunGroup;
	}

	protected void setCurrentSimulationRunGroup(SimulationRunGroup currentSimulationRunGroup) {
		this.currentSimulationRunGroup = currentSimulationRunGroup;
	}

	protected void setSimulationConfiguration(SimulationConfiguration simulationConfiguration) {
		this.simulationConfiguration = simulationConfiguration;
	}

	/*
	 * @see mapSimulationSideAgent
	 */
	public void mapSimulationSideAgents(Iterable<Object> agentsOfOneType) {
		for (Object simulationAgent : agentsOfOneType) {
			mapSimulationSideAgent(simulationAgent);
		}
	}

	/*
	 * After the Simulation and Common Framework are initialized, the Simulation Adaptor API (or
	 * child class) is initialized, and prior to executing a simulation run, this method must be
	 * called to configure the simulation-side of the AgentMappings for one type (class) of
	 * simulation agent. If multiple agent classes are distributed, this method must be called for
	 * each type. This is done prior to the distributed agent-side mappings.
	 * 
	 * Use this method to send a single Simulation Agent object.
	 * 
	 * @see mapSimulationSideAgents
	 */
	public void mapSimulationSideAgent(Object simulationAgent) {
		mapSimulationSideAgent(simulationAgent);
	}
	
	public void cleanup() {
		
	}
}
