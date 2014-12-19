package org.simulationsystems.csf.distsys.mas.adapter.runners.jade;

import java.io.IOException;
import java.util.List;

import org.jdom2.Element;
import org.simulationsystems.csf.common.csfmodel.SYSTEM_TYPE;
import org.simulationsystems.csf.common.csfmodel.csfexceptions.CsfInitializationRuntimeException;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FRAMEWORK_COMMAND;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessageImpl;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.STATUS;
import org.simulationsystems.csf.distsys.adapters.jade.api.JADE_MAS_AdapterAPI;
import org.simulationsystems.csf.distsys.adapters.jade.api.JADE_MAS_RunContext;
import org.simulationsystems.csf.distsys.adapters.jade.api.JADE_MAS_RunGroupContext;
import org.simulationsystems.csf.distsys.adapters.jade.api.mocks.MockHumanJADE_Agent;
import org.simulationsystems.csf.distsys.adapters.jade.api.mocks.NativeJADEMockContext;
import org.simulationsystems.csf.distsys.core.api.distributedautonomousagents.DistributedAgentModel;
import org.simulationsystems.csf.distsys.core.api.distributedautonomousagents.DistributedAgentsManager;
import org.simulationsystems.csf.distsys.core.api.distributedautonomousagents.DistributedAutonomousAgent;

public class JADE_Controller_Agent {
	JADE_MAS_AdapterAPI jade_MAS_AdapterAPI;

	public static void main(String[] args) {
		String frameworkConfigurationFileName = null;
		if (args.length >= 1)
			frameworkConfigurationFileName = args[0];
		// TODO: Add Validation of CSF configuration file
		if (frameworkConfigurationFileName == null)
			throw new CsfInitializationRuntimeException(
					"The configuration directory must be provided");

		JADE_MAS_AdapterAPI jade_MAS_AdapterAPI = JADE_MAS_AdapterAPI.getInstance();
		JADE_MAS_RunGroupContext jade_MAS_RunGroupContext = null;

		try {
			jade_MAS_RunGroupContext = jade_MAS_AdapterAPI
					.initializeAPI(frameworkConfigurationFileName);
		} catch (IOException e) {
			throw new CsfInitializationRuntimeException(
					"Error in initializing the JADE Controller Agent", e);
		}

		// Initialize simulation run
		// TODO: Fix the native JADE context
		JADE_MAS_RunContext jade_MAS_RunContext = jade_MAS_AdapterAPI
				.initializeSimulationRun(new NativeJADEMockContext(),
						jade_MAS_RunGroupContext);

	}
}
