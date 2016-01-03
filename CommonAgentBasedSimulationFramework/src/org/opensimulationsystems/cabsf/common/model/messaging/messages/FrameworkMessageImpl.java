package org.opensimulationsystems.cabsf.common.model.messaging.messages;

import java.io.IOException;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.opensimulationsystems.cabsf.common.internal.messaging.MessagingUtilities;
import org.opensimulationsystems.cabsf.common.internal.messaging.xml.transformers.FrameworkMessageDocumentHelper;
import org.opensimulationsystems.cabsf.common.model.SYSTEM_TYPE;
import org.opensimulationsystems.cabsf.common.model.cabsfexceptions.CabsfCheckedException;

/**
 * The implementation class for the FrameworkMessage interface which wraps the
 * CabsfMessageExchange XML Document
 *
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class FrameworkMessageImpl implements FrameworkMessage {

    /** The framework message document helper. */
    private final FrameworkMessageDocumentHelper frameworkMessageDocumentHelper = new FrameworkMessageDocumentHelper(
            this);

    /** The document. */
    private Document document;

    /** The source system type. */
    private SYSTEM_TYPE sourceSystemType;

    /** The target system type. */
    private SYSTEM_TYPE targetSystemType;

    /**
     * Instantiates a new framework message impl.
     */
    @SuppressWarnings("unused")
    private FrameworkMessageImpl() {

    }

    /*
     * Used for outgoing messages. Pass in the framework message Document
     * template
     */
    /**
     * Instantiates a new framework message impl.
     *
     * @param sourceSystemType
     *            the source system type
     * @param targetSystemType
     *            the target system type
     * @param document
     *            a Document to use as a template. Use an agent's cached
     *            template when creating a new message, or an existing Document
     *            if you're converting an XML Document into a FrameworkMessage
     *            to use the helper methods.
     */
    public FrameworkMessageImpl(final SYSTEM_TYPE sourceSystemType,
            final SYSTEM_TYPE targetSystemType, final Document document) {

        this.document = document;

        this.sourceSystemType = sourceSystemType;
        this.targetSystemType = targetSystemType;
    }

    /*
     * Used for incoming messages. Pass in the XML String from the framework
     */
    /**
     * Instantiates a new framework message impl.
     *
     * @param sourceSystemType
     *            the source system type
     * @param targetSystemType
     *            the target system type
     * @param xmlString
     *            the xml string
     * @throws CabsfCheckedException
     *             the cabsf checked exception
     */
    public FrameworkMessageImpl(final SYSTEM_TYPE sourceSystemType,
            final SYSTEM_TYPE targetSystemType, final String xmlString)
                    throws CabsfCheckedException {
        super();
        try {
            this.document = MessagingUtilities.createDocumentFromString(xmlString);
        } catch (JDOMException | IOException e) {
            // TODO: Where to catch this?
            throw new CabsfCheckedException("Unable to parse the message XML", e);
        }

        this.sourceSystemType = sourceSystemType;
        this.targetSystemType = targetSystemType;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opensimulationsystems.cabsf.common.model.messaging.messages.
     * FrameworkMessage# addDistributedAutonomousAgent(org.jdom2.Document,
     * org.jdom2.Element, boolean)
     */
    @Override
    public Document addSoftwareAgent(final Document CabsfMessageExchangeDoc,
            final Element distributedAutononomousAgentElement,
            final boolean removeChildren) {
        return frameworkMessageDocumentHelper.addDistributedAutonomousAgent(
                CabsfMessageExchangeDoc, distributedAutononomousAgentElement,
                removeChildren);
    }

    @Override
    public Element getAboutAgentModelElementFromDistributedSoftwareAgentElement(
            final Element distributedAutonomousAgentElement,
            final Element cachedAgentModelTemplate, final String agentModelID) {
        return frameworkMessageDocumentHelper
                .getAboutAgentModelElementFromDistributedSoftwareAgentElement(
                        distributedAutonomousAgentElement, cachedAgentModelTemplate,
                        agentModelID);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opensimulationsystems.cabsf.common.model.messaging.messages.
     * FrameworkMessage# getAgentModels(org.jdom2.Element)
     */
    @Override
    public List<Element> getAgentModels(final Element distributedAutonomousAgentElement) {
        return frameworkMessageDocumentHelper
                .getAgentModels(distributedAutonomousAgentElement);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opensimulationsystems.cabsf.common.model.messaging.messages.
     * FrameworkMessage# getDistributedAutonomousAgentElements(java.lang.Object)
     */
    @Override
    public List<Element> getDistributedSoftwareAgentElements(final Object doc) {
        return frameworkMessageDocumentHelper.getDistributedAutonomousAgentElements(doc);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opensimulationsystems.cabsf.common.model.messaging.messages.
     * FrameworkMessage# getDistributedAutonomousAgentID(org.jdom2.Element)
     */
    @Override
    public String getSoftwareAgentID(
            final Element distributedAutononomousAgentElement) {
        return frameworkMessageDocumentHelper
                .getDistributedAutonomousAgentID(distributedAutononomousAgentElement);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opensimulationsystems.cabsf.common.model.messaging.messages.
     * FrameworkMessage# getDocument()
     */
    @Override
    public Document getDocument() {
        return document;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opensimulationsystems.cabsf.common.model.messaging.messages.
     * FrameworkMessage# getFirstAgentModelActorAgentModelID(org.jdom2.Element)
     */
    @Override
    public String getFirstAboutAgentModelID(final Element agentModel) {
        return frameworkMessageDocumentHelper
                .getFirstAgentModelActorAgentModelID(agentModel);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opensimulationsystems.cabsf.common.model.messaging.messages.
     * FrameworkMessage# getFrameworkToDistributedSystemCommand()
     */
    @Override
    public FRAMEWORK_COMMAND getFrameworkToDistributedSystemCommand() {
        return frameworkMessageDocumentHelper.getFrameworkToDistributedSystemCommand();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opensimulationsystems.cabsf.common.model.messaging.messages.
     * FrameworkMessage# getFrameworkToSimulationEngineCommand()
     */
    @Override
    public FRAMEWORK_COMMAND getFrameworkToSimulationEngineCommand() {
        return frameworkMessageDocumentHelper.getFrameworkToSimulationEngineCommand();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opensimulationsystems.cabsf.common.model.messaging.messages.
     * FrameworkMessage# getNextAgentModelActor(java.lang.Object,
     * org.jdom2.Element)
     */
    @Override
    public Element getNextAboutAgentModelFromDistributedSoftwareAgentElement(
            final Object distributedAutonomousAgent,
            final Element cachedAgentModelActorTemplate) {
        return frameworkMessageDocumentHelper.getNextAboutAgentModel(
                distributedAutonomousAgent, cachedAgentModelActorTemplate);
    }

    // FIXME: Need to to rename these two getNext Function so it's obvious they
    // are only
    // used for writing messages
    /*
     * (non-Javadoc)
     *
     * @see org.opensimulationsystems.cabsf.common.model.messaging.messages.
     * FrameworkMessage# getNextDistributedAutonomousAgent(org.jdom2.Document,
     * org.jdom2.Element)
     */
    @Override
    public Element getNextMsgForDistributedSoftwareAgentElement(final Document doc,
            final Element cacheDistributedSoftwareAgentTemplate) {
        return frameworkMessageDocumentHelper.getNextDistributedSoftwareAgent(doc,
                cacheDistributedSoftwareAgentTemplate);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opensimulationsystems.cabsf.common.model.messaging.messages.
     * FrameworkMessage#
     * getNextNonSelfSimulationDefinedLocationForActor(org.jdom2.Element,
     * org.jdom2.Element)
     */
    @Override
    public Element getNextNonSelfSimulationDefinedLocationForActor(final Element actor,
            final Element cachedLocationTemplate) {
        return frameworkMessageDocumentHelper
                .getNextNonSelfSimulationDefinedLocationForActor(actor,
                        cachedLocationTemplate);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opensimulationsystems.cabsf.common.model.messaging.messages.
     * FrameworkMessage# getSelfLocation(org.jdom2.Element,
     * org.opensimulationsystems
     * .cabsf.common.model.messaging.messages.FrameworkMessage)
     */
    @Override
    public List<String> getSelfLocationFromFirstAgentModel(
            final Element distributedAutononomousAgentElement, final FrameworkMessage msg) {
        return frameworkMessageDocumentHelper.getSelfLocationFromFirstAgentModel(
                distributedAutononomousAgentElement, msg);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opensimulationsystems.cabsf.common.model.messaging.messages.
     * FrameworkMessage#
     * getSimulationDefinedEnvironmentChangesElement(org.jdom2.Element)
     */
    @Override
    public Element getSimulationDefinedEnvironmentChangesElement(final Element actor) {
        return frameworkMessageDocumentHelper
                .getSimulationDefinedEnvironmentChangesElement(actor);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opensimulationsystems.cabsf.common.model.messaging.messages.
     * FrameworkMessage #getStatus ()
     */
    @Override
    public STATUS getStatus() {
        return frameworkMessageDocumentHelper.getStatus();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opensimulationsystems.cabsf.common.model.messaging.messages.
     * FrameworkMessage# getSelfLocation
     * (org.opensimulationsystems.cabsf.common.
     * model.messaging.messages.FrameworkMessage)
     */
    @Override
    public List<String> getThisAgentLocationFromNextSoftwareAgentNextAgentModelActorInFrameworkMessage(
            final FrameworkMessage msg) {
        return frameworkMessageDocumentHelper
                .getSelfLocationFromNextDistributedAutonomousAgentNextAgentModelActor(msg);

    }

    /*
     * (non-Javadoc)
     *
     * @see org.opensimulationsystems.cabsf.common.model.messaging.messages.
     * FrameworkMessage#
     * populateThisLocationInAgentModelActor(org.jdom2.Element,
     * java.lang.String, java.lang.String, org.jdom2.Element)
     */
    @Override
    public Element populateThisLocationInAgentModelActor(final Element actor,
            final String gridPointX, final String gridPointY,
            final Element cachedLocationTemplate) {
        return frameworkMessageDocumentHelper.populateThisActorLocationInAgentModel(
                actor, gridPointX, gridPointY, cachedLocationTemplate);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opensimulationsystems.cabsf.common.model.messaging.messages.
     * FrameworkMessage# removeDistributedAutonomousAgents(org.jdom2.Document)
     */
    @Override
    public void removeDistributedSoftwareAgentElements(
            final Document CabsfMessageExchangeDoc) {
        frameworkMessageDocumentHelper
        .removeDistributedAutonomousAgents(CabsfMessageExchangeDoc);

    }

    /*
     * (non-Javadoc)
     *
     * @see org.opensimulationsystems.cabsf.common.model.messaging.messages.
     * FrameworkMessage# setDistributedAutonomousAgentID(org.jdom2.Element,
     * java.lang.String)
     */
    @Override
    public Element setDistributedSoftwareAgentID(
            final Element distributedAutonomousAgent, final String ID) {
        return frameworkMessageDocumentHelper.setDistributedAutonomousAgentID(
                distributedAutonomousAgent, ID);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opensimulationsystems.cabsf.common.model.messaging.messages.
     * FrameworkMessage# setFrameworkToDistributedSystemCommand
     * (org.opensimulationsystems
     * .cabsf.common.model.messaging.messages.FRAMEWORK_COMMAND)
     */
    @Override
    public void setFrameworkToDistributedSystemCommand(
            final FRAMEWORK_COMMAND frameworkToDistributedSystemCommand) {
        frameworkMessageDocumentHelper
        .setFrameworkToDistributedSystemCommand(frameworkToDistributedSystemCommand);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opensimulationsystems.cabsf.common.model.messaging.messages.
     * FrameworkMessage# setFrameworkToSimulationEngineCommand
     * (org.opensimulationsystems
     * .cabsf.common.model.messaging.messages.FRAMEWORK_COMMAND)
     */
    @Override
    public void setFrameworkToSimulationEngineCommand(
            final FRAMEWORK_COMMAND frameworkToSimulationEngineCommand) {
        frameworkMessageDocumentHelper
        .setFrameworkToSimulationEngineCommnad(frameworkToSimulationEngineCommand);
    }

    /*
     * @Override public void setIDForAboutAgentModel(final Element
     * aboutAgentModel, final String agentModelID) {
     * frameworkMessageDocumentHelper.setIDForAboutAgentModel(aboutAgentModel,
     * agentModelID);
     * 
     * }
     */

    /*
     * (non-Javadoc)
     *
     * @see org.opensimulationsystems.cabsf.common.model.messaging.messages.
     * FrameworkMessage# setIDForActorInAgentModel(org.jdom2.Element,
     * java.lang.String)
     */
    @Override
    public void setIDForAboutAgentModel(final Element actor, final String ID) {
        frameworkMessageDocumentHelper.setIDForAboutAgentModel(actor, ID);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opensimulationsystems.cabsf.common.model.messaging.messages.
     * FrameworkMessage #setStatus
     * (org.opensimulationsystems.cabsf.common.model.messaging.messages.STATUS)
     */
    @Override
    public void setStatus(final STATUS status) {
        frameworkMessageDocumentHelper.setStatus(status);
    }

    /*
     * Returns the String representation of the message. For outgoing messages,
     * this String representation will not always be the version to be sent to
     * the common messaging interface. All transformations must be performed
     * before this String representation reflects the version to be sent to the
     * common messaging interface.
     */
    /*
     * (non-Javadoc)
     *
     * @see org.opensimulationsystems.cabsf.common.model.messaging.messages.
     * FrameworkMessage# toPrettyPrintedXMLString()
     */
    @Override
    public String toPrettyPrintedXMLString() {
        return transformToCommonMessagingXMLString(true);

    }

    /*
     * (non-Javadoc)
     *
     * @see org.opensimulationsystems.cabsf.common.model.messaging.messages.
     * FrameworkMessage# transformToCommonMessagingXMLString(boolean)
     */
    @Override
    public String transformToCommonMessagingXMLString(final boolean prettyPrint) {
        // TODO: Mapping between agent IDs and UUIDs in this class?
        // Message all of the agents in each each target distributed system
        // for (UUID distributedSystemAgentUUID :
        // distributedSystem.getDistributedAgentUUIDs()) {
        // }

        // LOW: Drive the pretty print parameter from the configuration
        return frameworkMessageDocumentHelper.frameworkMessageToXMLString(document,
                prettyPrint);
    }
}
