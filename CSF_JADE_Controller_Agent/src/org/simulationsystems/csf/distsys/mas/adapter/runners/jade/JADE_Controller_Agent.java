package org.simulationsystems.csf.distsys.mas.adapter.runners.jade;

import java.io.IOException;

import org.simulationsystems.csf.common.csfmodel.SYSTEM_TYPE;
import org.simulationsystems.csf.common.csfmodel.csfexceptions.CsfInitializationRuntimeException;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FRAMEWORK_COMMAND;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessageImpl;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.STATUS;
import org.simulationsystems.csf.distsys.adapters.jade.api.JADE_MAS_AdapterAPI;
import org.simulationsystems.csf.distsys.adapters.jade.api.JADE_MAS_RunContext;
import org.simulationsystems.csf.distsys.adapters.jade.api.JADE_MAS_RunGroupContext;
import org.simulationsystems.csf.distsys.adapters.jade.api.mocks.NativeJADEMockContext;

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

		JADE_MAS_AdapterAPI jade_MAS_AdapterAPI = JADE_MAS_AdapterAPI
				.getInstance();
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

		// Listen for START_SIMULATION command from the simulation engine
		FRAMEWORK_COMMAND fc = jade_MAS_RunContext.listenForMessageFromSimulationEngine().getFrameworkToDistributedSystemCommand();
		// TODO: Better error handling.  Send a message back to the simulation engine that this distributed system is terminating
		
		if (fc == null || !fc.equals(FRAMEWORK_COMMAND.START_SIMULATION))
			throw new CsfInitializationRuntimeException(
					"The JADE Controller Agent tried to read message from the simulation engine, but did not understand the command: "
							+ fc.toString());
		System.out
				.println("[JADE Controller Agent] Received message from the simulation engine to start the simulation");

		// Push Status of Ready back to the simulation engine
		FrameworkMessage fm = new FrameworkMessageImpl(
				SYSTEM_TYPE.DISTRIBUTED_SYSTEM, SYSTEM_TYPE.SIMULATION_ENGINE,
				jade_MAS_RunContext.getCachedMessageExchangeTemplate());
		fm.setStatus(STATUS.READY_TO_START_SIMULATION);
		jade_MAS_RunContext.messageSimulationEngine(fm, jade_MAS_RunContext.getDistSysRunContext());

		// Expect the tick information
		fc = jade_MAS_RunContext.listenForMessageFromSimulationEngine().getFrameworkToDistributedSystemCommand();
		// TODO: Better error handling
		if (fc == null || !fc.equals(FRAMEWORK_COMMAND.START_SIMULATION))
			throw new CsfInitializationRuntimeException(
					"The JADE Controller Agent tried to read message from the simulation engine, but did not understand the command: "
							+ fc.toString());
		System.out
				.println("[JADE Controller Agent] Received message from the simulation engine to start the simulation");

	}
}
