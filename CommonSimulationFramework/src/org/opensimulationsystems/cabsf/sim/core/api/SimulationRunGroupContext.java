package org.opensimulationsystems.cabsf.sim.core.api;

import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.opensimulationsystems.cabsf.common.csfmodel.SimulationRunGroup;
import org.opensimulationsystems.cabsf.common.csfmodel.context.CsfRunGroupContext;
import org.opensimulationsystems.cabsf.common.internal.messaging.xml.XMLUtilities;
import org.opensimulationsystems.cabsf.sim.core.api.configuration.SimulationRunGroupConfiguration;

public class SimulationRunGroupContext extends CsfRunGroupContext {
	private SimulationRunGroup simulationRunGroup;
	private SimulationRunGroupConfiguration simulationRunGroupConfiguration;
	private Document cachedMessageExchangeTemplate;
	
	// LOW: Support Multiple simulation run groups/run contexts
	private SimulationRunContext simulationRunContext;

	public SimulationRunGroupContext() {
	}

	public SimulationRunGroupConfiguration getSimulationRunGroupConfiguration() {
		return simulationRunGroupConfiguration;
	}

	public SimulationRunGroup getSimulationRunGroup() {
		return simulationRunGroup;
	}

	public void setSimulationRunGroup(SimulationRunGroup simulationRunGroup) {
		this.simulationRunGroup = simulationRunGroup;
	}

	public void setSimulationConfiguration(SimulationRunGroupConfiguration simulationRunGroupConfiguration) {
		this.simulationRunGroupConfiguration = simulationRunGroupConfiguration; 
		
	}

	public void setSimulationRunContext(SimulationRunContext simulationRunContext) {
		this.simulationRunContext = simulationRunContext;
		
	}

	public void setSimulationRunGroupConfiguration(SimulationRunGroupConfiguration simulationRunGroupConfiguration) {
		this.simulationRunGroupConfiguration = simulationRunGroupConfiguration;
	}
	
}
