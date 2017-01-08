package org.opensimulationsystems.cabsf.distsys.mas.mocks;

import java.util.List;
import java.util.UUID;

import jzombies.JZombies_CABSF_Helper;

import org.jdom2.Element;
import org.opensimulationsystems.cabsf.common.internal.messaging.xml.XMLUtilities;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FrameworkMessage;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.JadeControllerInterface;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.Jade_AgentContext_Cabsf;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.nativeagents.NativeSoftwareAgent;

public class MockHumanJADE_Agent implements NativeSoftwareAgent {

    private String distributedAutonomousAgentID;
    private final String modelName;

    private final String distributedAutonomousAgentModelID;

    // This is not the same instance as the context running in the controller
    // agent due to
    // the fact that these JADE agents are all autononmous. The important here
    // is to
    // provide access to the API to understand the messages.
    Jade_AgentContext_Cabsf jade_MAS_AgentContext = new Jade_AgentContext_Cabsf();

    private final JZombies_CABSF_Helper jZombies_CABSF_Helper;

    private final String distributedSystemID;

    private String logPrefix = null;
    private final String frameworkConfigurationFileName;

    public MockHumanJADE_Agent(final String distributedSystemID,
            final String distributedAutonomousAgentID, final String distAutAgentModelID,
            final String modelName, final String frameworkConfigurationFileName) {
        jZombies_CABSF_Helper = new JZombies_CABSF_Helper(jade_MAS_AgentContext,
                frameworkConfigurationFileName);

        this.distributedSystemID = distributedSystemID;
        this.distributedAutonomousAgentID = distributedAutonomousAgentID;
        this.distributedAutonomousAgentModelID = distAutAgentModelID;
        this.modelName = modelName;
        this.frameworkConfigurationFileName = frameworkConfigurationFileName;

        logPrefix = "[MockHumanJADE_Agent " + distributedSystemID + " "
                + distributedAutonomousAgentID + " " + distAutAgentModelID + "]";

        jade_MAS_AgentContext.initializeJadeAgentForCabsf("TESTconfigFile");

    }

    private List<String> chooseMoveTowardsLocation(final List<String> selfPoint,
            final List<String> pointWithLeastZombiesPoint) {

        return pointWithLeastZombiesPoint;

    }

    @Override
    public String getDistributedAutonomousAgentID() {
        return distributedAutonomousAgentID;
    }

    @Override
    public String getDistributedAutonomousAgentModelID() {
        return distributedAutonomousAgentModelID;
    }

    @Override
    public String getModelName() {
        return modelName;
    }

    /*
     * Receives the portion of the Message Exchange XML that belongs to this
     * agent
     */
    @Override
    public void receiveMessage(FrameworkMessage msg, final String messageID,
            final String inReplyToMessageID,
            final JadeControllerInterface jade_Controller_Agent) {
        // converts the distributed autonomous agent document back to the full
        // message
        // exchange document that then gets converted into a FrameworkMessage
        // All of the other information not meant for this distributed
        // autonomous agent is
        // not present either in the original or converted XML

        System.out.println("[NativeSoftwareAgent "
                + distributedAutonomousAgentID
                + "] Received message ID: "
                + distributedAutonomousAgentID
                + " Message:"
                + XMLUtilities.convertDocumentToXMLString(msg.getDocument()
                        .getRootElement(), true));

        // The framework message to this distributed autonomous agent can be
        // expected to
        // only contain a single entry for the distributed autonomous agent

        final List<String> selfPoint = msg
                .getThisAgentLocationFromNextSoftwareAgentNextAgentModelActorInFrameworkMessage(msg);
        for (int i = 0; i < selfPoint.size(); i++) {
            System.out.println(logPrefix + " Self Location: " + String.valueOf(i) + " : "
                    + String.valueOf(selfPoint.get(i)));
        }

        final Element distributedAutonomousAgentElement = msg
                .getNextMsgForDistributedSoftwareAgentElement(msg.getDocument(), null);

        final List<String> pointWithLeastZombiesPoint = jZombies_CABSF_Helper
                .getPointWithLeastZombies(distributedAutonomousAgentElement, msg,
                        distributedAutonomousAgentModelID);

        for (int i = 0; i < pointWithLeastZombiesPoint.size(); i++) {
            System.out.println(logPrefix + " Received Zombie location "
                    + String.valueOf(i) + " : "
                    + String.valueOf(pointWithLeastZombiesPoint.get(i)));
        }

        final List<String> pointToMoveTo = chooseMoveTowardsLocation(selfPoint,
                pointWithLeastZombiesPoint);
        // Send the decision on where to move to
        final String newMessageID = UUID.randomUUID().toString();
        final String originalMessageId = messageID;

        msg = jZombies_CABSF_Helper.convertMoveToPointToFrameworkMessage(pointToMoveTo,
                distributedAutonomousAgentID, distributedAutonomousAgentModelID);
        System.out.println(logPrefix
                + " Sending move decision to the JADE Controller Agent: "
                + XMLUtilities.convertDocumentToXMLString(msg.getDocument()
                        .getRootElement(), true));

        jade_Controller_Agent.receiveMessage(msg, newMessageID, originalMessageId);

    }

    public void setDistributedAutonomousAgentID(final String distributedAutonomousAgentID) {
        this.distributedAutonomousAgentID = distributedAutonomousAgentID;
    }
}