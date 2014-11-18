package org.simulationsystems.csf.common.internal.messaging.messages;

import org.simulationsystems.csf.common.csfmodel.FRAMEWORK_COMMAND;
import org.simulationsystems.csf.common.internal.systems.DistributedSystem;

/*
 * A message from the framework or to the framework.
 */
public interface FrameworkMessage {
	
	public String transformToCommonMessagingXMLString(DistributedSystem distributedSystem);

	void setFrameworkToDistributedSystemCommand(
			FRAMEWORK_TO_DISTRIBUTEDSYSTEM_COMMAND frameworkToDistributedSystemCommand);

	FRAMEWORK_TO_DISTRIBUTEDSYSTEM_COMMAND getFrameworkToDistributedSystemCommand();
	
	FRAMEWORK_COMMAND getFrameworkCommand();
}
