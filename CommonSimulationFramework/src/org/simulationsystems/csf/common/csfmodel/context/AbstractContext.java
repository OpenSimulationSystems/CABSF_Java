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

/**
 * The abstract parent of all context classes. This class provides common functionality
 * across the contexts, such a method to generate a FrameworkMessage from the message
 * exchange (CsfMessageExchange) template
 * 
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public abstract class AbstractContext {
	// TODO: Get these two from the configuration
	// TODO: Only use either the string or the namespace.
	/** The namespace str. */
	private final String namespaceStr = "http://www.simulationsystems.org/csf/schemas/CsfMessageExchange/0.1.0";

	/** The namespace. */
	private final Namespace namespace = Namespace.getNamespace("x", namespaceStr);

	/** The element filter. */
	private final Filter<Element> elementFilter = new org.jdom2.filter.ElementFilter();

	/** The cached agent model actor template. */
	protected Element cachedAgentModelActorTemplate = null;

	/** The cached location template. */
	protected Element cachedLocationTemplate = null;

	/** The distributed autonomous agent template. */
	private Element distributedAutonomousAgentTemplate;

	/** The cached message exchange template with empty placeholders. */
	protected Document cachedMessageExchangeTemplateWithEmptyPlaceholders;

	// TODO: Move this one high level in API?
	// TODO: Remove the ID from the method signature?
	/**
	 * Used by the distributed autonomous agents to convert the XML fragment in the
	 * original incoming message back into a full Message Exchange XML, then wrap it in a
	 * FrameworkMessage. The purposes of this is to give the agent access to convenience
	 * methods in FrameworkMessage for working with the XML.
	 * 
	 * @param distributedAutononomousAgentElement
	 *            the distributed autononomous agent element
	 * @param distributedAutonomousAgentID
	 *            the distributed autonomous agent id.
	 * @param sourceSystem
	 *            the source system. Can be null if only creating the FrameworkMessage to
	 *            use the convenience methods and not actually sending this
	 *            FrameworkMessage across the wire.
	 * @param targetSystem
	 *            the target system. Can be null if only creating the FrameworkMessage to
	 *            use the convenience methods and not actually sending this
	 *            FrameworkMessage across the wire.
	 * @return the FrameworkMessage
	 */
	public FrameworkMessage convertDocumentSentToDistributedAutonomousAgentToFrameworkMessage(
			final Element distributedAutononomousAgentElement,
			final String distributedAutonomousAgentID, final SYSTEM_TYPE sourceSystem,
			final SYSTEM_TYPE targetSystem) {

		final FrameworkMessage fm = new FrameworkMessageImpl(sourceSystem, targetSystem,
		// jade_MAS_RunContext.getCachedMessageExchangeTemplate());
				this.getBlankCachedMessageExchangeTemplate());
		// FIXME: Need a better name if all we're doing is setting the ID.
		// populateDistributedAutonomousAgent
		fm.addDistributedAutonomousAgent(fm.getDocument(),
				distributedAutononomousAgentElement, true);

		return fm;
	}

	/**
	 * Gets the blank cached message exchange template.
	 * 
	 * @return the blank cached message exchange template
	 */
	public Document getBlankCachedMessageExchangeTemplate() {
		final FrameworkMessage fm = new FrameworkMessageImpl(null, null,
				cachedMessageExchangeTemplateWithEmptyPlaceholders.clone());
		fm.removeDistributedAutonomousAgents(fm.getDocument());
		return fm.getDocument();
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
	 * Gets the cached distributed autonomous agent template.
	 * 
	 * @return the cached distributed autonomous agent template
	 */
	public Element getCachedDistributedAutonomousAgentTemplate() {
		return distributedAutonomousAgentTemplate.clone();
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
	 * Sets the cached message exchange template with placeholders.
	 * 
	 * @param cachedMessageExchangeTemplateWithPlaceholders
	 *            the new cached message exchange template with placeholders
	 */
	public void setCachedMessageExchangeTemplateWithPlaceholders(
			final Document cachedMessageExchangeTemplateWithPlaceholders) {
		this.cachedMessageExchangeTemplateWithEmptyPlaceholders = cachedMessageExchangeTemplateWithPlaceholders;
		setupElementTemplates(cachedMessageExchangeTemplateWithPlaceholders);
	}

	/**
	 * Sets the up all of the element templates.
	 * 
	 * @param doc
	 *            the new up element templates
	 */
	public void setupElementTemplates(final Document doc) {
		@SuppressWarnings("unchecked")
		final List<Element> distributedAutonomousAgentElements = (List<Element>) XMLUtilities
				.executeXPath(
						doc,
						"/x:CsfMessageExchange/x:ReceivingEntities/x:DistributedSystem/x:DistributedAutonomousAgents/x:DistributedAutonomousAgent",
						namespaceStr, elementFilter);
		distributedAutonomousAgentTemplate = distributedAutonomousAgentElements.get(0)
				.clone();

		// Agent Model Template
		final List<Element> agentModelsElements = (List<Element>) XMLUtilities
				.executeXPath(distributedAutonomousAgentTemplate, "./x:AgentModels",
						namespaceStr, elementFilter);
		final Element agentModels = cachedAgentModelActorTemplate = agentModelsElements
				.get(0);
		final Element agentModel = agentModels.getChild("AgentModel", namespace).detach();
		cachedAgentModelActorTemplate = agentModel.getChild("Actor", namespace);

		// Location Template
		@SuppressWarnings("unchecked")
		final// TODO: Support multiple actors
		List<Element> agentEnvironmentChangeLocation = (List<Element>) XMLUtilities
				.executeXPath(
						cachedAgentModelActorTemplate,
						"./x:EnvironmentChanges/x:CommonEnvironmentChanges/x:EnvironmentChange",
						namespaceStr, elementFilter);
		cachedLocationTemplate = agentEnvironmentChangeLocation.get(0).detach();
		cachedLocationTemplate.setAttribute("id", "");

	}
}
