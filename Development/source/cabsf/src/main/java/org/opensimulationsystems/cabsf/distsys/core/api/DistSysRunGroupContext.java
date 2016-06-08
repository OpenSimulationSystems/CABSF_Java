package org.opensimulationsystems.cabsf.distsys.core.api;

import org.opensimulationsystems.cabsf.common.model.SimulationRunGroup;
import org.opensimulationsystems.cabsf.common.model.configuration.RunGroupConfiguration;
import org.opensimulationsystems.cabsf.common.model.context.CabsfRunGroupContext;

public class DistSysRunGroupContext extends CabsfRunGroupContext {
	private SimulationRunGroup simulationRunGroup;
	private RunGroupConfiguration runGroupConfiguration;

	// LOW: Support Multiple simulation run groups/run contexts
	private DistSysRunContext distSysRunContext;

	public DistSysRunGroupContext() {
	}

	public RunGroupConfiguration getRunGroupConfiguration() {
		return runGroupConfiguration;
	}

	public SimulationRunGroup getRunGroup() {
		return simulationRunGroup;
	}

	public void setDistributedSystemSimulationRunGroup(
			final SimulationRunGroup simulationRunGroup) {
		this.simulationRunGroup = simulationRunGroup;
	}

	public void setDistSysRunContext(final DistSysRunContext distSysRunContext) {
		this.distSysRunContext = distSysRunContext;

	}

	public void setRunGroupConfiguration(
			final RunGroupConfiguration runGroupConfiguration) {
		this.runGroupConfiguration = runGroupConfiguration;

	}

}
