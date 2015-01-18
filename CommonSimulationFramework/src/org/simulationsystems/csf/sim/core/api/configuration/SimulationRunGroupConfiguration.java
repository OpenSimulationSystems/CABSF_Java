package org.simulationsystems.csf.sim.core.api.configuration;

import java.util.ArrayList;
import java.util.HashMap;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filter;
import org.simulationsystems.csf.common.csfmodel.SIMULATION_TYPE;
import org.simulationsystems.csf.sim.core.api.SimulationRunGroupContext;
import org.simulationsystems.csf.sim.core.api.distributedsystems.SimulationDistributedSystemManager;
import org.simulationsystems.csf.sim.core.api.distributedsystems.SimulationDistributedSystemManager.CONFIGURATION_KEYS;

public class SimulationRunGroupConfiguration {
	//TODO set all of these in the configuration file
	private String simulationEngineID = "REPAST_SIMPHONY";
	private String namespaceStr = "http://www.simulationsystems.org/csf/schemas/CsfMessageExchange/0.1.0";
	private Namespace namespace = Namespace.getNamespace("x", namespaceStr);
	private Element locationTemplate=null;
	private Filter<Element> elementFilter = new org.jdom2.filter.ElementFilter();

	private boolean simulationAgentsBelongToOneClass = false;


	// TODO: Actually populate these values
	// LOW: Support Multiple Simulation Run Configurations
	ArrayList<SimulationRunConfiguration> simulationRunConfigurations;
	private String frameworkConfigurationFileNameName;

	/*
	 * DistributedSystemSimulationRunGroup Level.
	 */
	public SimulationRunGroupConfiguration(String frameworkConfigurationFileNameName) {
		// TODO: Read the actual values from the configuration file. Add methods to get into the
		// configuration values.
		this.frameworkConfigurationFileNameName = frameworkConfigurationFileNameName;
		if (frameworkConfigurationFileNameName.equals("PLACEHOLDER_FOR_CSF_CONFIGURATION_FILE")) {
			this.simulationAgentsBelongToOneClass = true;
		}
	}
	
	public boolean getSimulationAgentsBelongToOneClass() {
		return simulationAgentsBelongToOneClass;
	}

	public String getFrameworkConfigurationFileNameName() {
		return frameworkConfigurationFileNameName;
	}

	public String getSimulationEngineID() {
		return simulationEngineID;
	}
	
	// LOW: see above
	public SimulationRunConfiguration getSimulationRunConfiguration() {
		return simulationRunConfigurations.get(0);

	}

}
