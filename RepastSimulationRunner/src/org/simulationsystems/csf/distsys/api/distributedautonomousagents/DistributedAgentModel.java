package org.simulationsystems.csf.distsys.api.distributedautonomousagents;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.simulationsystems.csf.distsys.api.DistSysRunContext;

public class DistributedAgentModel {
	String distributedAgentModelID;
	String name;
	
	private Object distributedNativeAgentModelObject = new Object();


	public DistributedAgentModel(DistSysRunContext distSysRunContext,
			String distributedAgentModelID, String damName) {
		this.distributedAgentModelID = distributedAgentModelID;
		this.name = damName;

		if (distributedAgentModelID == null)
			this.distributedAgentModelID = UUID.randomUUID().toString();
	}
	
	public Object assignAgentModel(Object distributedNativeAgentModelObject) {
		this.distributedNativeAgentModelObject = distributedNativeAgentModelObject;
		
		return distributedNativeAgentModelObject;
	}
}
