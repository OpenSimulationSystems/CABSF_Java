package org.opensimulationsystems.cabsf.distsys.adapters.jade.api;

import java.io.IOException;

import org.jdom2.JDOMException;
import org.opensimulationsystems.cabsf.common.internal.messaging.MessagingUtilities;
import org.opensimulationsystems.cabsf.common.model.cabsfexceptions.CabsfInitializationRuntimeException;
import org.opensimulationsystems.cabsf.common.model.context.AgentContext_Cabsf;

/**
 * The JADE MAS Agent Context. This class is used by the JADE agents as the
 * mechanism for communicating with the CABSF.<br/>
 * Note: In the current release, the agents must also use the JADE MAS Adapter
 * Context, which is separate this agent-level context.
 *
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class Jade_AgentContext_Cabsf extends AgentContext_Cabsf {

    /** The Jade_AdapterAPI api. */
    Jade_AdapterAPI jade_MAS_AdapterAPI;

    /**
     * Initialize cabsf agent.
     *
     * @param frameworkConfigurationFileName
     *            the framework configuration file name
     * @throws JDOMException
     *             the JDOM exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void initializeJadeAgentForCabsf(final String frameworkConfigurationFileName) {
        try {
            // TODO: Analyze whether this could can be removed.
            /*
             * Jade_AdapterAPI jade_MAS_AdapterAPI =
             * Jade_AdapterAPI.getInstance();
             *
             * try { jade_MAS_RunGroupContext = jade_MAS_AdapterAPI
             * .initializeAPI(frameworkConfigurationFileName); } catch
             * (IOException e) { throw new CabsfInitializationRuntimeException(
             * "Error in initializing the JADE Agent Context", e); }
             *
             * // Initialize simulation run jade_MAS_RunContext =
             * jade_MAS_AdapterAPI.initializeSimulationRun( new
             * NativeJADEMockContext(), jade_MAS_RunGroupContext,
             * isJADE_ControllerAgent);
             */

            setupCachedMessageExchangeTemplateWithPlaceholders(MessagingUtilities
                    .createCachedMessageExchangeTemplateWithPlaceholders());

        } catch (final JDOMException | IOException e) {
            throw new CabsfInitializationRuntimeException(
                    "Error initializing JADE agent", e);
        }
    }

}
