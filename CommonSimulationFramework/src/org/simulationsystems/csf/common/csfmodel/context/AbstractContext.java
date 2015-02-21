/*
 * 
 */
package org.simulationsystems.csf.common.csfmodel.context;

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
	/** The namespace str. */
	private String namespaceStr = "http://www.simulationsystems.org/csf/schemas/CsfMessageExchange/0.1.0";

	/** The namespace. */
	private Namespace namespace = Namespace.getNamespace("x", namespaceStr);

	/** The element filter. */
	private Filter<Element> elementFilter = new org.jdom2.filter.ElementFilter();

	/** The cached agent model actor template. */
	protected Element cachedAgentModelActorTemplate = null;

	/** The cached location template. */
	protected Element cachedLocationTemplate = null;

	/** The distributed autonomous agent template. */
	private Element distributedAutonomousAgentTemplate;

	/** The cached message exchange template with empty placeholders. */
	protected Document cachedMessageExchangeTemplateWithEmptyPlaceholders;

	/**
	 * Gets the cached distributed autonomous agent template.
	 *
	 * @return the cached distributed autonomous agent template
	 */
	public Element getCachedDistributedAutonomousAgentTemplate() {
		return distributedAutonomousAgentTemplate.clone();
	}

	/**
	 * Gets the cached agent model actor template.
	 *
	 * @return the cached agent model actor template
	 */
	public Element getCachedAgentModelActorTemplate() {
		return cachedAgentModelActorTemplate.clone();
	}

	/**
	 * Gets the cached location template.
	 *
	 * @return the cached location template
	 */
	public Element getCachedLocationTemplate() {
		return cachedLocationTemplate.clone();
	}

	/**
	 * Sets the up element templates.
	 *
	 * @param doc
	 *            the new up element templates
	 */
	public void setupElementTemplates(Document doc) {
		@SuppressWarnings("unchecked")
		List<Element> distributedAutonomousAgentElements = (List<Element>) XMLUtilities
		.executeXPath(
				doc,
				"/x:CsfMessageExchange/x:ReceivingEntities/x:DistributedSystem/x:DistributedAutonomousAgents/x:DistributedAutonomousAgent",
				namespaceStr, elementFilter);
		distributedAutonomousAgentTemplate = distributedAutonomousAgentElements
				.get(0).clone();

		// Agent Model Template
		List<Element> agentModelsElements = (List<Element>) XMLUtilities
				.executeXPath(distributedAutonomousAgentTemplate,
						"./x:AgentModels", namespaceStr, elementFilter);
		Element agentModels = cachedAgentModelActorTemplate = agentModelsElements
				.get(0);
		Element agentModel = agentModels.getChild("AgentModel", namespace)
				.detach();
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

	/**
	 * Gets the blank cached message exchange template.
	 *
	 * @return the blank cached message exchange template
	 */
	public Document getBlankCachedMessageExchangeTemplate() {
		FrameworkMessage fm = new FrameworkMessageImpl(null, null,
				cachedMessageExchangeTemplateWithEmptyPlaceholders.clone());
		fm.removeDistributedAutonomousAgents(fm.getDocument());
		return fm.getDocument();
	}

	/**
	 * Sets the cached message exchange template with placeholders.
	 *
	 * @param cachedMessageExchangeTemplateWithPlaceholders
	 *            the new cached message exchange template with placeholders
	 */
	public void setCachedMessageExchangeTemplateWithPlaceholders(
			Document cachedMessageExchangeTemplateWithPlaceholders) {
		this.cachedMessageExchangeTemplateWithEmptyPlaceholders = cachedMessageExchangeTemplateWithPlaceholders;
		setupElementTemplates(cachedMessageExchangeTemplateWithPlaceholders);
	}

	/*
	 * Used by the distributed autonomous agents to convert their partial
	 * document back into the full Message Exchange XML, which is included a
	 * FrameworkMessage. The purposes of this is to give the agent access to
	 * convience methods for dealing with the XML
	 */
	// TODO: Move this one high level in API?
	// TODO: Remove the ID from the method signature?
	/**
	 * Convert document sent to distributed autonomous agent to framework
	 * message.
	 *
	 * @param distributedAutononomousAgentElement
	 *            the distributed autononomous agent element
	 * @param distributedAutonomousAgentID
	 *            the distributed autonomous agent id
	 * @param sourceSystem
	 *            the source system
	 * @param targetSystem
	 *            the target system
	 * @return the framework message
	 */
	public FrameworkMessage convertDocumentSentToDistributedAutonomousAgentToFrameworkMessage(
			Element distributedAutononomousAgentElement,
			String distributedAutonomousAgentID, SYSTEM_TYPE sourceSystem,
			SYSTEM_TYPE targetSystem) {

		FrameworkMessage fm = new FrameworkMessageImpl(sourceSystem,
				targetSystem,
				// jade_MAS_RunContext.getCachedMessageExchangeTemplate());
				this.getBlankCachedMessageExchangeTemplate());
		// FIXME: Need a better name if all we're doing is setting the ID.
		// populateDistributedAutonomousAgent
		fm.addDistributedAutonomousAgent(fm.getDocument(),
				distributedAutononomousAgentElement, true);

		return fm;
	}
}
