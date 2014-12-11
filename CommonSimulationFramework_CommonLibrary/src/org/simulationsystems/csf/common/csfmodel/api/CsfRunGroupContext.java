package org.simulationsystems.csf.common.csfmodel.api;

import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filter;
import org.simulationsystems.csf.common.internal.messaging.xml.XMLUtilities;

public class CsfRunGroupContext {
	// TODO: Get these two from the configuration
	private String namespaceStr = "http://www.simulationsystems.org/csf/schemas/CsfMessageExchange/0.1.0";
	private Namespace namespace = Namespace.getNamespace("x", namespaceStr);
	private Filter<Element> elementFilter = new org.jdom2.filter.ElementFilter();
	protected Element cachedAgentModelActorTemplate = null;
	protected Element cachedLocationTemplate = null;

	public Element getCachedAgentModelActorTemplate() {
		return cachedAgentModelActorTemplate.clone();
	}

	public Element  getCachedLocationTemplate() {
		return cachedLocationTemplate.clone();
	}

	public void setupElementTemplates(Document doc) {
		// Agent Model Template
		List<Element> agentModelActor = (List<Element>) XMLUtilities
				.executeXPath(
						doc,
						"/x:CsfMessageExchange/x:ReceivingEntities/x:DistributedSystem/x:DistributedAutonomousAgents/x:DistributedAutonomousAgent/x:AgentModels/x:AgentModel[1]/x:Actor",
						namespaceStr, elementFilter);
		cachedAgentModelActorTemplate = agentModelActor.get(0);

		// Location Template
		@SuppressWarnings("unchecked")
		// TODO: Support multiple actors
		List<Element> agentLocation = (List<Element>) XMLUtilities
				.executeXPath(
						doc,
						"/x:CsfMessageExchange/x:ReceivingEntities/x:DistributedSystem/x:DistributedAutonomousAgents/x:DistributedAutonomousAgent/x:AgentModels/x:AgentModel/x:Actor/x:EnvironmentChanges/x:CommonEnvironmentChanges/x:EnvironmentChange/x:Location",
						namespaceStr, elementFilter);
		cachedLocationTemplate = agentLocation.get(0).clone();
		cachedLocationTemplate.setAttribute("id", "");
	}
}
