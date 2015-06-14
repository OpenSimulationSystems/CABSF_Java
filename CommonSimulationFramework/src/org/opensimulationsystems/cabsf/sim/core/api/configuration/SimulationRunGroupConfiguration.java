package org.opensimulationsystems.cabsf.sim.core.api.configuration;

import java.util.ArrayList;

import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filter;

/**
 * The configuration object for a simulation run group.
 * 
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class SimulationRunGroupConfiguration {
	// TODO set all of these in the configuration file
	/** The simulation engine id. */
	private final String simulationEngineID = "REPAST_SIMPHONY";

	/** The namespace str. */
	private final String namespaceStr = "http://www.simulationsystems.org/csf/schemas/CsfMessageExchange/0.1.0";

	/** The namespace. */
	private final Namespace namespace = Namespace.getNamespace("x", namespaceStr);

	/** The location template. */
	private final Element locationTemplate = null;

	/** The element filter. */
	private final Filter<Element> elementFilter = new org.jdom2.filter.ElementFilter();

	/** The simulation agents belong to one class. */
	private boolean simulationAgentsBelongToOneClass = false;

	// TODO: Actually populate these values
	// LOW: Support Multiple Simulation Run Configurations
	/** The simulation run configurations. */
	ArrayList<SimulationRunConfiguration> simulationRunConfigurations;

	/** The framework configuration file name name. */
	private final String frameworkConfigurationFileNameName;

	/**
	 * Instantiates a new simulation run group configuration.
	 * 
	 * @param frameworkConfigurationFileNameName
	 *            the framework configuration file name name
	 */
	public SimulationRunGroupConfiguration(final String frameworkConfigurationFileNameName) {
		// TODO: Read the actual values from the configuration file. Add methods to get
		// into the
		// configuration values.
		this.frameworkConfigurationFileNameName = frameworkConfigurationFileNameName;
		if (frameworkConfigurationFileNameName
				.equals("PLACEHOLDER_FOR_CSF_CONFIGURATION_FILE")) {
			this.simulationAgentsBelongToOneClass = true;
		}
	}

	/**
	 * Gets the framework configuration file name name.
	 * 
	 * @return the framework configuration file name name
	 */
	public String getFrameworkConfigurationFileNameName() {
		return frameworkConfigurationFileNameName;
	}

	/**
	 * Gets the simulation agents belong to one class.
	 * 
	 * @return the simulation agents belong to one class
	 */
	public boolean getSimulationAgentsBelongToOneClass() {
		return simulationAgentsBelongToOneClass;
	}

	/**
	 * Gets the simulation engine id.
	 * 
	 * @return the simulation engine id
	 */
	public String getSimulationEngineID() {
		return simulationEngineID;
	}

	/**
	 * Gets the simulation run configuration.
	 * 
	 * @return the simulation run configuration
	 */
	public SimulationRunConfiguration getSimulationRunConfiguration() {
		return simulationRunConfigurations.get(0);

	}

}
