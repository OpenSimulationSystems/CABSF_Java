package org.opensimulationsystems.cabsf.distsys.adapters.jade.api;

import java.io.IOException;

import org.jdom2.JDOMException;
import org.opensimulationsystems.cabsf.common.csfmodel.context.AgentContext;
import org.opensimulationsystems.cabsf.common.internal.messaging.MessagingUtilities;

/**
 * The JADE MAS Agent Context. This class is used by the JADE agents as the mechanism for
 * communicating with the CSF.<br/>
 * Note: In the current release, the agents must also use the JADE MAS Adapter Context,
 * which is separate this agent-level context.
 * 
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class JADE_MAS_AgentContext extends AgentContext {

	/** The JADE_MAS_AdapterAPI api. */
	JADE_MAS_AdapterAPI jade_MAS_AdapterAPI;

	/**
	 * Initialize csf agent.
	 * 
	 * @param frameworkConfigurationFileName
	 *            the framework configuration file name
	 * @throws JDOMException
	 *             the JDOM exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void initializeCsfAgent(final String frameworkConfigurationFileName)
			throws JDOMException, IOException {

		// TODO: Analyze whether this could can be removed.
		/*
		 * JADE_MAS_AdapterAPI jade_MAS_AdapterAPI = JADE_MAS_AdapterAPI.getInstance();
		 * 
		 * try { jade_MAS_RunGroupContext = jade_MAS_AdapterAPI
		 * .initializeAPI(frameworkConfigurationFileName); } catch (IOException e) { throw
		 * new CabsfInitializationRuntimeException(
		 * "Error in initializing the JADE Agent Context", e); }
		 * 
		 * // Initialize simulation run jade_MAS_RunContext =
		 * jade_MAS_AdapterAPI.initializeSimulationRun( new NativeJADEMockContext(),
		 * jade_MAS_RunGroupContext, isJADE_ControllerAgent);
		 */

		setCachedMessageExchangeTemplateWithPlaceholders(MessagingUtilities
				.createCachedMessageExchangeTemplateWithPlaceholders());

	}

}
