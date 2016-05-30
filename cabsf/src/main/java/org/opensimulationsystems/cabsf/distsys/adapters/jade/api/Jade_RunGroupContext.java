package org.opensimulationsystems.cabsf.distsys.adapters.jade.api;

import org.jdom2.Document;
import org.opensimulationsystems.cabsf.distsys.core.api.DistSysRunGroupContext;

// TODO: Auto-generated Javadoc
/**
 * The run group context for the JADE MAS.
 *
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class Jade_RunGroupContext {

    /** The DistSysRunGroupContextp context. */
    private final DistSysRunGroupContext distSysRunGroupContext;

    /**
     * Instantiates a new Jade_RunGroupContext.
     *
     * @param distSysRunGroupContext
     *            the DistSysRunGroupContextp context
     */
    public Jade_RunGroupContext(final DistSysRunGroupContext distSysRunGroupContext) {
        this.distSysRunGroupContext = distSysRunGroupContext;
    }

    /**
     * Gets the cached message exchange template.
     *
     * @return the cached message exchange template
     */
    public Document getCachedMessageExchangeTemplate() {
        return this.distSysRunGroupContext.getBlankCachedMessageExchangeTemplate();
    }

    /**
     * Gets the DistSysRunGroupContextp context.
     *
     * @return the DistSysRunGroupContextp context
     */
    public DistSysRunGroupContext getDistSysRunGroupContext() {
        return distSysRunGroupContext;
    }

}
