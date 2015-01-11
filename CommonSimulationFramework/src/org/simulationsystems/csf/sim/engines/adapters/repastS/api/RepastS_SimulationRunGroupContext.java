package org.simulationsystems.csf.sim.engines.adapters.repastS.api;

import org.jdom2.Document;
import org.simulationsystems.csf.sim.core.api.SimulationRunGroupContext;

public class RepastS_SimulationRunGroupContext {

	private SimulationRunGroupContext simulationRunGroupContext;
	
	public SimulationRunGroupContext getSimulationRunGroupContext() {
		return simulationRunGroupContext;
	}
	public RepastS_SimulationRunGroupContext(SimulationRunGroupContext simulationRunGroupContext) {
		this.simulationRunGroupContext = simulationRunGroupContext;
	}

	public Document getCachedMessageExchangeTemplate() {
		return this.simulationRunGroupContext.getBlankCachedMessageExchangeTemplate();
	}
	
	
/*	public void setCachedMessageExchangeTemplate(Document cachedMessageExchangeTemplateWithEmptyPlaceholders) {
		this.simulationRunGroupContext = cachedMessageExchangeTemplateWithEmptyPlaceholders;
	}*/
}
