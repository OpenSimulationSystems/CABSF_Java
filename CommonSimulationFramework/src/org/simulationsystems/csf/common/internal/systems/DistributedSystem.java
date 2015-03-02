package org.simulationsystems.csf.common.internal.systems;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a single distributed system containing one or more distributed agents.
 * 
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class DistributedSystem {

	/** The distributed system id. */
	String distributedSystemID;

	/** The distributed agent uui ds. */
	Set<UUID> distributedAgentUUIDs = new HashSet<UUID>();

	/** The all UUIDs flag. */
	boolean allUUIDsFlag = false;

	/**
	 * Instantiates a new distributed system.
	 * 
	 * @param distributedSystemID
	 *            the distributed system id
	 */
	public DistributedSystem(final String distributedSystemID) {
		if (distributedSystemID == null)
			this.distributedSystemID = UUID.randomUUID().toString();
		else
			this.distributedSystemID = distributedSystemID;
	}

	/**
	 * Adds the distributed agent uuid.
	 * 
	 * @param uuid
	 *            the uuid
	 */
	public void addDistributedAgentUUID(final UUID uuid) {
		distributedAgentUUIDs.add(uuid);
	}

	/**
	 * Gets the distributed agent uui ds.
	 * 
	 * @return the distributed agent uui ds
	 */
	public Set<UUID> getDistributedAgentUUIDs() {
		return distributedAgentUUIDs;
	}

	/**
	 * Gets the distributed system id.
	 * 
	 * @return the distributed system id
	 */
	public String getDistributedSystemID() {
		return distributedSystemID;
	}

	/*
	 * TODO: Analyze if we still need this functionality: Flag for whether the message
	 * should be sent to all agents (depending on the context from the caller, for one
	 * distributed system or many distributed systems). If set to true and
	 * distributedAgentUUIDs is not empty, it is assumed that that set is the complete
	 * list of agents and the distributed system may either use the agent UUIDs in this
	 * set, or the ones from their own collection (which should be the same). Both the
	 * flag and the agents are passed to the common messaging system. If the flag is set
	 * to true and the distributedAgentUUIDs is empty, only the flag is passed to the
	 * common messaging system/distributed system. It is preferred to leave
	 * distributedAgentUUIDs empty when messaging a large number of distributed agents,
	 * for network performance reasons.
	 */
	// TODO: Move this to the message level
	/**
	 * Sets the all UUIDs flag.
	 * 
	 * @param flag
	 *            the new all uui ds flag
	 */
	public void setallUUIDsFlag(final boolean flag) {
		allUUIDsFlag = flag;
	}

	/**
	 * Sets the distributed agent UUIDs.
	 * 
	 * @param uuids
	 *            the new distributed agent uui ds
	 */
	public void setDistributedAgentUUIDs(final Set<UUID> uuids) {
		distributedAgentUUIDs = uuids;
	}

}
