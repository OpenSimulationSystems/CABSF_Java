package org.simulationsystems.csf.sim.api.configuration;

import java.util.ArrayList;
import java.util.HashMap;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filter;
import org.simulationsystems.csf.sim.api.SimulationRunGroupContext;
import org.simulationsystems.csf.sim.api.distributedsystems.SimulationDistributedSystemManager;
import org.simulationsystems.csf.sim.api.distributedsystems.SimulationDistributedSystemManager.CONFIGURATION_KEYS;

public class SimulationRunGroupConfiguration {
	private String namespaceStr = "http://www.simulationsystems.org/csf/schemas/CsfMessageExchange/0.1.0";
	private Namespace namespace = Namespace.getNamespace("x", namespaceStr);
	Element locationTemplate=null;
	private Filter<Element> elementFilter = new org.jdom2.filter.ElementFilter();

	
	// TODO: Actually populate these values
	// LOW: Support Multiple Simulation Run Configurations
	ArrayList<SimulationRunConfiguration> simulationRunConfigurations;

	/*
	 * DistributedSystemSimulationRunGroup Level.
	 */
	public SimulationRunGroupConfiguration() {
		// TODO: Read the actual values from the configuration file. Add methods to get into the
		// configuration values.


	}
	
	// LOW: see above
	public SimulationRunConfiguration getSimulationRunConfiguration() {
		return simulationRunConfigurations.get(0);

	}

}
