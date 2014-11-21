package org.simulationsystems.csf.common.csfmodel.messaging.messages;

import org.simulationsystems.csf.common.csfmodel.FRAMEWORK_COMMAND;

/*
 * A message from the framework or to the framework.
 */
public interface FrameworkMessage {
	
	public String transformToCommonMessagingXMLString(boolean prettyPrint);

	void setFrameworkCommandToDistSysInDocument(
			FRAMEWORK_COMMAND frameworkToDistributedSystemCommand);

	FRAMEWORK_COMMAND getFrameworkToDistributedSystemCommand();
	
	FRAMEWORK_COMMAND getFrameworkCommand();

	String toPrettyPrintedXMLString();

	public void setStatus(STATUS readyToStartSimulation);
}
