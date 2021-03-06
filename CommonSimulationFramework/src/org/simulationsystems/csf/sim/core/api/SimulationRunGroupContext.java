package org.simulationsystems.csf.sim.core.api;

import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.simulationsystems.csf.common.csfmodel.SimulationRunGroup;
import org.simulationsystems.csf.common.csfmodel.context.CsfRunGroupContext;
import org.simulationsystems.csf.common.internal.messaging.xml.XMLUtilities;
import org.simulationsystems.csf.sim.core.api.configuration.SimulationRunGroupConfiguration;

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
