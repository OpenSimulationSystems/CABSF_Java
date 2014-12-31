package org.simulationsystems.csf.distsys.adapters.jade.api;

import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.simulationsystems.csf.common.csfmodel.SYSTEM_TYPE;
import org.simulationsystems.csf.common.csfmodel.api.AgentContext;
import org.simulationsystems.csf.common.csfmodel.api.CsfRunGroupContext;
import org.simulationsystems.csf.common.csfmodel.csfexceptions.CsfInitializationRuntimeException;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessageImpl;
import org.simulationsystems.csf.common.internal.messaging.MessagingUtilities;
import org.simulationsystems.csf.common.internal.systems.DistributedSystem;
import org.simulationsystems.csf.distsys.adapters.jade.api.mocks.NativeJADEMockContext;
import org.simulationsystems.csf.distsys.core.api.DistSysRunContext;
import org.simulationsystems.csf.distsys.core.api.DistSysRunGroupContext;
import org.simulationsystems.csf.distsys.core.api.DistributedSystemAPI;

//TODO: Extend the run group or the run?  maybe both the run group and run should derive from a higher class that contains the methods we need
public class JADE_MAS_AgentContext extends AgentContext {
/*	public JADE_MAS_RunGroupContext getJade_MAS_RunGroupContext() {
		return jade_MAS_RunGroupContext;
	}

	public JADE_MAS_RunContext getJade_MAS_RunContext() {
		return jade_MAS_RunContext;
	}*/

	JADE_MAS_AdapterAPI jade_MAS_AdapterAPI;
/*	JADE_MAS_RunGroupContext jade_MAS_RunGroupContext;
	JADE_MAS_RunContext jade_MAS_RunContext;*/

	// FIXME: remove?
	public void initializeCsfAgent(String frameworkConfigurationFileName
		) throws JDOMException, IOException {

/*		JADE_MAS_AdapterAPI jade_MAS_AdapterAPI = JADE_MAS_AdapterAPI.getInstance();

		try {
			jade_MAS_RunGroupContext = jade_MAS_AdapterAPI
					.initializeAPI(frameworkConfigurationFileName);
		} catch (IOException e) {
			throw new CsfInitializationRuntimeException(
					"Error in initializing the JADE Agent Context", e);
		}

		// Initialize simulation run
		jade_MAS_RunContext = jade_MAS_AdapterAPI.initializeSimulationRun(
				new NativeJADEMockContext(), jade_MAS_RunGroupContext,
				isJADE_ControllerAgent);*/
		
		setCachedMessageExchangeTemplateWithPlaceholders(MessagingUtilities
				.createCachedMessageExchangeTemplateWithPlaceholders());
		
	}

}
