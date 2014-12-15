package org.simulationsystems.csf.common.csfmodel.messaging.messages;

import org.jdom2.Document;
import org.jdom2.Element;

/*
 * A message from the framework or to the framework.
 */
public interface FrameworkMessage {
	
	public String transformToCommonMessagingXMLString(boolean prettyPrint);

	void setFrameworkToDistributedSystemCommand(
			FRAMEWORK_COMMAND frameworkToDistributedSystemCommand);

	FRAMEWORK_COMMAND getFrameworkToDistributedSystemCommand();

	String toPrettyPrintedXMLString();

	public void setStatus(STATUS readyToStartSimulation);

	Document getDocument();

	void setFrameworkToSimulationEngineCommand(
			FRAMEWORK_COMMAND frameworkToDistributedSystemCommand);

	FRAMEWORK_COMMAND getFrameworkToSimulationEngineCommand();

	STATUS getStatus();

	Element populateThisActorLocationInAgentModel(Element actor, String ID, String gridPointX,
			String gridPointY);

	Element populateDistributedAutonomousAgent(Element distributedAutonomousAgent, String ID);
	
	Element getNextAgentModelActor(Object distributedAutononomousAgent, Element cachedAgentModelTemplate);

	Element getNextDistributedAutonomousAgent(Object doc, Element cacheDistributedAutonomousAgentTemplate);
 
	Element getNextNonSelfLocationForActor(Element actor, Element cachedLocationTemplate);


}
