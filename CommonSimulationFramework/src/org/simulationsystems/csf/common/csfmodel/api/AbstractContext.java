package org.simulationsystems.csf.common.csfmodel.api;

import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filter;
import org.simulationsystems.csf.common.csfmodel.SYSTEM_TYPE;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessageImpl;
import org.simulationsystems.csf.common.internal.messaging.xml.XMLUtilities;

public abstract class AbstractContext {
	// TODO: Get these two from the configuration
	private String namespaceStr = "http://www.simulationsystems.org/csf/schemas/CsfMessageExchange/0.1.0";
	private Namespace namespace = Namespace.getNamespace("x", namespaceStr);
	private Filter<Element> elementFilter = new org.jdom2.filter.ElementFilter();
	protected Element cachedAgentModelActorTemplate = null;
	protected Element cachedLocationTemplate = null;
	private Element distributedAutonomousAgentTemplate;
	protected Document cachedMessageExchangeTemplateWithEmptyPlaceholders;

	public Element getCachedDistributedAutonomousAgentTemplate() {
		return distributedAutonomousAgentTemplate.clone();
	}

	public Element getCachedAgentModelActorTemplate() {
		return cachedAgentModelActorTemplate.clone();
	}

	public Element getCachedLocationTemplate() {
		return cachedLocationTemplate.clone();
	}

	public void setupElementTemplates(Document doc) {
		@SuppressWarnings("unchecked")
		List<Element> distributedAutonomousAgentElements = (List<Element>) XMLUtilities
				.executeXPath(
						doc,
						"/x:CsfMessageExchange/x:ReceivingEntities/x:DistributedSystem/x:DistributedAutonomousAgents/x:DistributedAutonomousAgent",
						namespaceStr, elementFilter);
		distributedAutonomousAgentTemplate = distributedAutonomousAgentElements.get(0)
				.clone();

		// Agent Model Template
		List<Element> agentModelsElements = (List<Element>) XMLUtilities.executeXPath(
				distributedAutonomousAgentTemplate, "./x:AgentModels", namespaceStr,
				elementFilter);
		Element agentModels = cachedAgentModelActorTemplate = agentModelsElements.get(0);
		Element agentModel = agentModels.getChild("AgentModel", namespace).detach();
		cachedAgentModelActorTemplate = agentModel.getChild("Actor", namespace);

		// Location Template
		@SuppressWarnings("unchecked")
		// TODO: Support multiple actors
		List<Element> agentEnvironmentChangeLocation = (List<Element>) XMLUtilities
				.executeXPath(
						cachedAgentModelActorTemplate,
						"./x:EnvironmentChanges/x:CommonEnvironmentChanges/x:EnvironmentChange",
						namespaceStr, elementFilter);
		cachedLocationTemplate = agentEnvironmentChangeLocation.get(0).detach();
		cachedLocationTemplate.setAttribute("id", "");

	}

	public Document getBlankCachedMessageExchangeTemplate() {
		FrameworkMessage fm = new FrameworkMessageImpl(null, null,
				cachedMessageExchangeTemplateWithEmptyPlaceholders.clone());
		fm.removeDistributedAutonomousAgents(fm.getDocument());
		return fm.getDocument();
	}
	
	
	public void setCachedMessageExchangeTemplateWithPlaceholders(
			Document cachedMessageExchangeTemplateWithPlaceholders) {
		this.cachedMessageExchangeTemplateWithEmptyPlaceholders = cachedMessageExchangeTemplateWithPlaceholders;
		setupElementTemplates(cachedMessageExchangeTemplateWithPlaceholders);
	}
	
	/*
	 * Used by the distributed autonomous agents to convert their partial document back
	 * into the full Message Exchange XML, which is included a FrameworkMessage. The
	 * purposes of this is to give the agent access to convience methods for dealing with
	 * the XML
	 */
	// TODO: Move this one high level in API?
	// TODO: Remove the ID from the method signature?
	public FrameworkMessage convertDocumentSentToDistributedAutonomousAgentToFrameworkMessage(
			Element distributedAutononomousAgentElement, String distributedAutonomousAgentID, SYSTEM_TYPE sourceSystem, SYSTEM_TYPE targetSystem) {

		FrameworkMessage fm = new FrameworkMessageImpl(sourceSystem,
				targetSystem,
				//jade_MAS_RunContext.getCachedMessageExchangeTemplate());
			this.getBlankCachedMessageExchangeTemplate());
		// FIXME: Need a better name if all we're doing is setting the ID.
		// populateDistributedAutonomousAgent
		fm.addDistributedAutonomousAgent(fm.getDocument(),
				distributedAutononomousAgentElement, true);

		return fm;
	}
}
