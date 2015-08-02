package org.opensimulationsystems.cabsf.sim.core.api;

import org.jdom2.Document;
import org.opensimulationsystems.cabsf.common.model.SimulationRunGroup;
import org.opensimulationsystems.cabsf.common.model.configuration.RunGroupConfiguration;
import org.opensimulationsystems.cabsf.common.model.context.CabsfRunGroupContext;

public class SimulationRunGroupContext extends CabsfRunGroupContext {
	private SimulationRunGroup simulationRunGroup;
	private RunGroupConfiguration runGroupConfiguration;
	private Document cachedMessageExchangeTemplate;

	// LOW: Support Multiple simulation run groups/run contexts
	private SimulationRunContext simulationRunContext;

	public SimulationRunGroupContext() {
	}

	public SimulationRunGroup getSimulationRunGroup() {
		return simulationRunGroup;
	}

	public RunGroupConfiguration getSimulationRunGroupConfiguration() {
		return runGroupConfiguration;
	}

	public void setSimulationRunContext(final SimulationRunContext simulationRunContext) {
		this.simulationRunContext = simulationRunContext;

	}

	public void setSimulationRunGroup(final SimulationRunGroup simulationRunGroup) {
		this.simulationRunGroup = simulationRunGroup;
	}

	public void setRunGroupConfiguration(
			final RunGroupConfiguration runGroupConfiguration) {
		this.runGroupConfiguration = runGroupConfiguration;
	}

}
