package org.simulationsystems.csf.distsys.adapters.jade.api.nativeagents;

import java.util.HashSet;
import java.util.Set;

public class NativeJADEMockContext {
	private Set<NativeDistributedAutonomousAgent> nativeDistributedAutonomousAgents = new HashSet<NativeDistributedAutonomousAgent>();
	
	public void addAgent(NativeDistributedAutonomousAgent nativeDistributedAutonomousAgent) {
		nativeDistributedAutonomousAgents.add(nativeDistributedAutonomousAgent);
	}
	public Set<NativeDistributedAutonomousAgent> getMockJADE_Agents() {
		return nativeDistributedAutonomousAgents;
	}
	
	
}
