package org.opensimulationsystems.cabsf.distsys.adapters.jade.api.nativeagents;

import java.util.HashSet;
import java.util.Set;

/**
 * Mock context for JADE agent.
 * 
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
// TODO: Determine if this is still needed
public class NativeJADEMockContext {

	/** The native distributed autonomous agents. */
	private final Set<NativeSoftwareAgent> nativeSoftwareAgents = new HashSet<NativeSoftwareAgent>();

	/**
	 * Adds the agent.
	 * 
	 * @param nativeSoftwareAgent
	 *            the native distributed autonomous agent
	 */
	public void addAgent(
			final NativeSoftwareAgent nativeSoftwareAgent) {
		nativeSoftwareAgents.add(nativeSoftwareAgent);
	}

	/**
	 * Gets the mock jad e_ agents.
	 * 
	 * @return the mock jad e_ agents
	 */
	public Set<NativeSoftwareAgent> getMockJADE_Agents() {
		return nativeSoftwareAgents;
	}

}
