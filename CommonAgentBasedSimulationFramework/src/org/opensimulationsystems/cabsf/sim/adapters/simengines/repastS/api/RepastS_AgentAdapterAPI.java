package org.opensimulationsystems.cabsf.sim.adapters.simengines.repastS.api;

/**
 * The Repast Simphony Agent API
 * 
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class RepastS_AgentAdapterAPI {

	/** The instance. */
	private static RepastS_AgentAdapterAPI instance = new RepastS_AgentAdapterAPI();

	/**
	 * Gets the single instance of RepastS_AgentAdapterAPI.
	 * 
	 * @return single instance of RepastS_AgentAdapterAPI
	 */
	public static RepastS_AgentAdapterAPI getInstance() {
		return instance;
	}

	/**
	 * Disabled constructor
	 */
	private RepastS_AgentAdapterAPI() {
		super();
	}

	/**
	 * Gets the agent context.
	 * 
	 * @return the agent context
	 */
	public RepastS_AgentContext_Cabsf getAgentContext() {
		return new RepastS_AgentContext_Cabsf();

	}

}
