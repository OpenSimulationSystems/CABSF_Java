package org.opensimulationsystems.cabsf.distsys.core.api.distributedautonomousagents;

import java.util.UUID;

import org.opensimulationsystems.cabsf.distsys.core.api.DistSysRunContext;

/**
 * A distributed autonomous agent model
 * 
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class DistributedAgentModel {

	/** The distributed agent model id. */
	String distributedAgentModelID;

	/** The name. */
	String name;

	/**
	 * Instantiates a new distributed agent model.
	 * 
	 * @param distSysRunContext
	 *            the dist sys run context
	 * @param distributedAgentModelID
	 *            the distributed agent model id
	 * @param distributedAgentModelName
	 *            the distributed agent model name
	 */
	public DistributedAgentModel(final DistSysRunContext distSysRunContext,
			final String distributedAgentModelID, final String distributedAgentModelName) {
		this.distributedAgentModelID = distributedAgentModelID;
		this.name = distributedAgentModelName;

		if (distributedAgentModelID == null)
			this.distributedAgentModelID = UUID.randomUUID().toString();
	}

	/**
	 * Gets the distributed agent model id.
	 * 
	 * @return the distributed agent model id
	 */
	public String getDistributedAgentModelID() {
		return distributedAgentModelID;
	}

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

}
