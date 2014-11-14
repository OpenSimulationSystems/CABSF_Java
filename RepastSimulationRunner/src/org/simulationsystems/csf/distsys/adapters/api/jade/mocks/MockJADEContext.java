package org.simulationsystems.csf.distsys.adapters.api.jade.mocks;

import java.util.HashSet;
import java.util.Set;

public class MockJADEContext {
	private Set<MockJADE_Agent> mockJADE_Agents = new HashSet<MockJADE_Agent>();
	
	public void addAgent(MockJADE_Agent mockJADE_Agent) {
		mockJADE_Agents.add(mockJADE_Agent);
	}
	public Set<MockJADE_Agent> getMockJADE_Agents() {
		return mockJADE_Agents;
	}
	
	
}
