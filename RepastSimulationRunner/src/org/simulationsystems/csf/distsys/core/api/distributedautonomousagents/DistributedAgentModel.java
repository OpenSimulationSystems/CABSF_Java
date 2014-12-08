package org.simulationsystems.csf.distsys.core.api.distributedautonomousagents;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.simulationsystems.csf.distsys.core.api.DistSysRunContext;

public class DistributedAgentModel {
	String distributedAgentModelID;
	String name;
	
	public DistributedAgentModel(DistSysRunContext distSysRunContext,
			String distributedAgentModelID, String distributedAgentModelName) {
		this.distributedAgentModelID = distributedAgentModelID;
		this.name = distributedAgentModelName;

		if (distributedAgentModelID == null)
			this.distributedAgentModelID = UUID.randomUUID().toString();
	}
	

}
