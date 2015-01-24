package org.simulationsystems.csf.common.csfmodel.messaging.messages;

import java.util.List;

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

	Element populateThisLocationInAgentModelActor(Element actor, String gridPointX,
			String gridPointY, Element cachedLocationTemplate);

	Element setDistributedAutonomousAgentID(Element distributedAutonomousAgent, String ID);
	
	Element getNextAgentModelActor(Object distributedAutononomousAgent, Element cachedAgentModelTemplate);

	Element getNextDistributedAutonomousAgent(Document doc, Element cacheDistributedAutonomousAgentTemplate);
 
	Element getNextNonSelfSimulationDefinedLocationForActor(Element actor, Element cachedLocationTemplate);

	Element getSimulationDefinedEnvironmentChangesElement(Element actor);
	
	List<Element> getDistributedAutonomousAgentElements(Object csfMessageExchangeDoc);

	String getDistributedAutonomousAgentID(
			Element distributedAutononomousAgentElement);

	List<Element> getAgentModels(Element distributedAutonomousAgentElement);

	String getFirstAgentModelActorAgentModelID(Element agentModel);

	public Document addDistributedAutonomousAgent(Document csfMessageExchangeDoc, Element distributedAutononomousAgentElement, boolean removeChildren);

	public void removeDistributedAutonomousAgents(Document csfMessageExchangeDoc);

	List<String> getSelfLocation(Element distributedAutononomousAgentElement,
			FrameworkMessage msg);

	List<String> getSelfLocation(FrameworkMessage msg);

	void setIDForActorInAgentModel(Element actor, String ID);
}
