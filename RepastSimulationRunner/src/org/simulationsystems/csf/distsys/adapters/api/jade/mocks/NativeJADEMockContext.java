package org.simulationsystems.csf.distsys.adapters.api.jade.mocks;

import java.util.HashSet;
import java.util.Set;

public class NativeJADEMockContext {
	private Set<MockHumanJADE_Agent> mockHumanJADE_Agents = new HashSet<MockHumanJADE_Agent>();
	
	public void addAgent(MockHumanJADE_Agent mockHumanJADE_Agent) {
		mockHumanJADE_Agents.add(mockHumanJADE_Agent);
	}
	public Set<MockHumanJADE_Agent> getMockJADE_Agents() {
		return mockHumanJADE_Agents;
	}
	
	
}
