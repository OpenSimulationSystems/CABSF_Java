package org.simulationsystems.csf.distsys.adapters.jade.api;

import java.io.IOException;

import org.jdom2.Document;
import org.simulationsystems.csf.common.csfmodel.SYSTEM_TYPE;
import org.simulationsystems.csf.common.csfmodel.csfexceptions.CsfInitializationRuntimeException;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessageImpl;
import org.simulationsystems.csf.common.internal.systems.DistributedSystem;
import org.simulationsystems.csf.distsys.adapters.jade.api.mocks.NativeJADEMockContext;
import org.simulationsystems.csf.distsys.core.api.DistSysRunContext;
import org.simulationsystems.csf.distsys.core.api.DistSysRunGroupContext;
import org.simulationsystems.csf.distsys.core.api.DistributedSystemAPI;

public class JADE_MAS_AgentContext {
	JADE_MAS_AdapterAPI jade_MAS_AdapterAPI;
	JADE_MAS_RunGroupContext jade_MAS_RunGroupContext;
	JADE_MAS_RunContext jade_MAS_RunContext;

	// FIXME: Change
	public void initializeCsfAgent(String frameworkConfigurationFileName) {

		JADE_MAS_AdapterAPI jade_MAS_AdapterAPI = JADE_MAS_AdapterAPI.getInstance();

		try {
			jade_MAS_RunGroupContext = jade_MAS_AdapterAPI
					.initializeAPI(frameworkConfigurationFileName);
		} catch (IOException e) {
			throw new CsfInitializationRuntimeException(
					"Error in initializing the JADE Agent Context", e);
		}

		// Initialize simulation run
		// FIXME: Remove mock native JADE Context
		jade_MAS_RunContext = jade_MAS_AdapterAPI.initializeSimulationRun(
				new NativeJADEMockContext(), jade_MAS_RunGroupContext);
	}

	/*
	 * Used by the distributed autonomous agents to convert their partial document back
	 * into the full Message Exchange XML, which is included a FrameworkMessage. The
	 * purposes of this is to give the agent access to convience methods for dealing with
	 * the XML
	 */
	// TODO: Move this one high level in API?
	public FrameworkMessage convertDocumentToDistributedAutonomousAgentToFrameworkMessage(Document distributedAutonomusAgentDocument, String ID) {
		
		FrameworkMessage fm = new FrameworkMessageImpl(
				SYSTEM_TYPE.SIMULATION_ENGINE, SYSTEM_TYPE.DISTRIBUTED_SYSTEM,
				jade_MAS_RunContext.getCachedMessageExchangeTemplate());
		fm.populateDistributedAutonomousAgent(distributedAutonomusAgentDocument.getRootElement(), ID);
		return fm;
	}
}
