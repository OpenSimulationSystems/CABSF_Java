package org.simulationsystems.csf.distsys.core.api;

import org.jdom2.Document;
import org.simulationsystems.csf.common.csfmodel.SimulationRunGroup;
import org.simulationsystems.csf.common.csfmodel.api.CsfRunGroupContext;
import org.simulationsystems.csf.distsys.core.api.configuration.DistSysRunGroupConfiguration;

public class DistSysRunGroupContext extends CsfRunGroupContext  {
	private SimulationRunGroup simulationRunGroup;
	private DistSysRunGroupConfiguration distSysRunGroupConfiguration;

	// LOW: Support Multiple simulation run groups/run contexts
	private DistSysRunContext distSysRunContext;


	public DistSysRunGroupContext() {
	}

	public DistSysRunGroupConfiguration getSimulationConfiguration() {
		return distSysRunGroupConfiguration;
	}

	public SimulationRunGroup getSimulationRunGroup() {
		return simulationRunGroup;
	}

	public void setDistributedSystemSimulationRunGroup(SimulationRunGroup simulationRunGroup) {
		this.simulationRunGroup = simulationRunGroup;
	}

	public void setDistSysRunGroupConfiguration(
			DistSysRunGroupConfiguration distSysRunGroupConfiguration) {
		this.distSysRunGroupConfiguration = distSysRunGroupConfiguration;

	}

	public void setDistSysRunContext(
			DistSysRunContext distSysRunContext) {
		this.distSysRunContext = distSysRunContext;

	}




}
