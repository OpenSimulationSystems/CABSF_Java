package org.opensimulationsystems.cabsf.distsys.adapters.jade.api;


public class Jade_AgentAdapterAPI {

    /** The instance. */
    private static Jade_AgentAdapterAPI instance = new Jade_AgentAdapterAPI();

    /**
     * Gets the single instance of RepastS_AgentAdapterAPI.
     *
     * @return single instance of RepastS_AgentAdapterAPI
     */
    public static Jade_AgentAdapterAPI getInstance() {
        return instance;
    }

    /**
     * Disabled constructor
     */
    private Jade_AgentAdapterAPI() {
        super();
    }

    /**
     * Gets the agent context.
     *
     * @return the agent context
     */
    public Jade_AgentContext_Cabsf getJadeAgentContext() {
        return new Jade_AgentContext_Cabsf();

    }
}
