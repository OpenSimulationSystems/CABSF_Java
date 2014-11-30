package org.simulationsystems.csf.distsys.api.distributedautonomousagents;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.simulationsystems.csf.distsys.api.DistSysRunContext;

public class DistributedAgentModel {
	String distributedAgentModelID;
	String name;
	
	private Object distributedNativeAgentModelObject = new Object();


	public DistributedAgentModel(DistSysRunContext distSysRunContext,
			String distributedAgentModelID, String distributedAgentModelName) {
		this.distributedAgentModelID = distributedAgentModelID;
		this.name = distributedAgentModelName;

		if (distributedAgentModelID == null)
			this.distributedAgentModelID = UUID.randomUUID().toString();
	}
	
	public void setDistributedNativeAgentModelObject(Object distributedNativeAgentModelObject) {
		this.distributedNativeAgentModelObject = distributedNativeAgentModelObject;
		
		return;
	}
}
