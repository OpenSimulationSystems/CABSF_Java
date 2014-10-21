package org.simulationsystems.simulationframework.simulation.api.messaging;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/*
 * Represents a single distributed system containing one or more distributed agents
 */
public class DistributedSystemAgentSet {
	UUID distributedSystemUUID;
	Set<UUID> distributedAgents = new HashSet<UUID>();
	DISTRIBUTED_AGENT_SET_FLAG distributedAgentSetFlag;

	public DistributedSystemAgentSet(UUID distributedSystemUUID) {
		this.distributedSystemUUID = distributedSystemUUID;
	}

	public void addDistributedAgent(UUID uuid) {
		distributedAgents.add(uuid);
	}

	public DISTRIBUTED_AGENT_SET_FLAG getDistributedAgentSetFlag() {
		return distributedAgentSetFlag;
	}

	public void setDistributedAgentSetFlag(DISTRIBUTED_AGENT_SET_FLAG distributedAgentSetFlag) {
		this.distributedAgentSetFlag = distributedAgentSetFlag;
	}

	public UUID getDistributedSystemUUID() {
		return distributedSystemUUID;
	}

}
