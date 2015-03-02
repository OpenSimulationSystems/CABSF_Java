package org.simulationsystems.csf.distsys.adapters.jade.api.nativeagents;

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
	private final Set<NativeDistributedAutonomousAgent> nativeDistributedAutonomousAgents = new HashSet<NativeDistributedAutonomousAgent>();

	/**
	 * Adds the agent.
	 * 
	 * @param nativeDistributedAutonomousAgent
	 *            the native distributed autonomous agent
	 */
	public void addAgent(
			final NativeDistributedAutonomousAgent nativeDistributedAutonomousAgent) {
		nativeDistributedAutonomousAgents.add(nativeDistributedAutonomousAgent);
	}

	/**
	 * Gets the mock jad e_ agents.
	 * 
	 * @return the mock jad e_ agents
	 */
	public Set<NativeDistributedAutonomousAgent> getMockJADE_Agents() {
		return nativeDistributedAutonomousAgents;
	}

}
