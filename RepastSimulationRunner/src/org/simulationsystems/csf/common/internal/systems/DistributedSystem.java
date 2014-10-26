package org.simulationsystems.csf.common.internal.systems;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/*
 * Represents a single distributed system containing one or more distributed agents.
 */
public class DistributedSystem {
	String distributedSystemID;
	Set<UUID> distributedAgentUUIDs = new HashSet<UUID>();
	boolean allUUIDsFlag = false;

	public DistributedSystem(String distributedSystemID) {
		if (distributedSystemID==null)
			this.distributedSystemID = distributedSystemID.toString();
		else
			this.distributedSystemID = distributedSystemID;
	}

	public void setDistributedAgentUUIDs(Set<UUID> uuids) {
		distributedAgentUUIDs = uuids;
	}

	public void addDistributedAgentUUID(UUID uuid) {
		distributedAgentUUIDs.add(uuid);
	}

	/*
	 * Flag for whether the message should be sent to all agents (depending on the context from the
	 * caller, for one distributed system or many distributed sytems). If set to true and
	 * distributedAgentUUIDs is not empty, it is assumed that that set is the complete list of agents
	 * and the distributed system may either use the agent UUIDs in this set, or the ones from their
	 * own collection (which should be the same). Both the flag and the agents are passed to the
	 * common messaging system. If the flag is set to true and the distributedAgentUUIDs is empty, only
	 * the flag is passed to the common messaging system/distributed system. It is preferred to
	 * leave distributedAgentUUIDs empty when messaging a large number of distributed agents, for
	 * network performance reasons.
	 */
	public void setallUUIDsFlag(boolean flag) {
		allUUIDsFlag = flag;
	}

	public Set<UUID> getDistributedAgentUUIDs() {
		return distributedAgentUUIDs;
	}

	public String getDistributedSystemID() {
		return distributedSystemID;
	}

}
