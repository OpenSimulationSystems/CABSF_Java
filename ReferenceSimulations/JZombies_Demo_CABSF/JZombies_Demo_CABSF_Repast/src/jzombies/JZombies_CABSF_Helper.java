package jzombies;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.Filter;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.opensimulationsystems.cabsf.common.internal.messaging.xml.XMLUtilities;
import org.opensimulationsystems.cabsf.common.model.AgentMapping;
import org.opensimulationsystems.cabsf.common.model.SYSTEM_TYPE;
import org.opensimulationsystems.cabsf.common.model.cabsfexceptions.CabsfInitializationRuntimeException;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FrameworkMessage;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FrameworkMessageImpl;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.Jade_AgentContext_Cabsf;
import org.opensimulationsystems.cabsf.sim.adapters.simengines.repastS.api.RepastS_AgentContext_Cabsf;
import org.opensimulationsystems.cabsf.sim.core.api.distributedsystems.SimulationDistributedSystemManager;

import repast.simphony.space.grid.GridPoint;

// TODO: Auto-generated Javadoc
/**
 * The simulation-specific convenience class for the JZombies_Demo_CABSF
 * simulation. Contains methods to populate, read, and send FrameworkMessage
 * messages. This helper method is used by both the RepastS and JADE agents for
 * populating the messages in the XML.
 *
 * @author Jorge Calderon
 * @version 0.2
 * @since 0.2
 */
public class JZombies_CABSF_Helper {
    private final Filter<Element> elementFilter = new org.jdom2.filter.ElementFilter();
    private RepastS_AgentContext_Cabsf repastS_AgentContext_Cabsf;

    // TODO: Get this from the configuration
    private final String namespaceStr = "http://www.opensimulationsystems.org/cabsf/schemas/CabsfMessageExchange/0.1.0";
    private final Namespace namespace = Namespace.getNamespace("x", namespaceStr);

    // Only used by the JADE agent
    private Jade_AgentContext_Cabsf jade_AgentContext;

    private String frameworkConfigurationFileName;

    /**
     * Instantiates a new JZombies_CABSF_Helper for the calling JADE agent. This
     * method should only be called by JADE agents.
     *
     * @param jade_AgentContext
     *            the Jade_AgentContext_Cabsf
     * @param frameworkConfigurationFileName
     *            the framework configuration file name. May be null, as the
     *            configuration file is not required in the JADE agents, only
     *            the JCA and the RepastS simulation.
     */
    public JZombies_CABSF_Helper(final Jade_AgentContext_Cabsf jade_MAS_AgentContext,
            final String frameworkConfigurationFileName) {
        this.jade_AgentContext = jade_MAS_AgentContext;
        this.frameworkConfigurationFileName = frameworkConfigurationFileName;

        try {
            jade_MAS_AgentContext
                    .initializeJadeAgentForCabsf(frameworkConfigurationFileName);
        } catch (final JDOMException | IOException e) {
            throw new CabsfInitializationRuntimeException(
                    "Error initializing JADE agent", e);
        }
    }

    /**
     * Instantiates this convenience class for the calling RepastS agent. This
     * method should only be called by RepastS agents.
     * <p/>
     * The framework configuration filename is not parameter as that
     * configuration is handled by the RSSR, for the simulation side.
     *
     * @param repastS_AgentContext_Cabsf
     *            the cabsf repast s_ agent context
     */
    public JZombies_CABSF_Helper(
            final RepastS_AgentContext_Cabsf repastS_AgentContext_Cabsf) {
        this.repastS_AgentContext_Cabsf = repastS_AgentContext_Cabsf;
    }

    /**
     * Convert move to point to framework message.
     *
     * @param pointMoveTo
     *            the point move to
     * @param distributedSoftwareAgentID
     *            the dist aut agent id
     * @param distSoftwareAgentModelID
     *            the dist aut agent model id
     * @return the framework message
     */
    public FrameworkMessage convertMoveToPointToFrameworkMessage(
            final List<String> pointMoveTo, final String distributedSoftwareAgentID,
            final String distSoftwareAgentModelID) {
        final FrameworkMessage msg = new FrameworkMessageImpl(
                SYSTEM_TYPE.DISTRIBUTED_SYSTEM, SYSTEM_TYPE.SIMULATION_ENGINE,
                jade_AgentContext.getBlankCachedMessageExchangeTemplate());
        populateZombiesMessage(msg, distributedSoftwareAgentID, distSoftwareAgentModelID,
                pointMoveTo, new ArrayList<String>());

        return msg;
    }

    /**
     * Gets the point with least zombies.
     *
     * @param distributedAutonomousAgentElement
     *            the distributed autonomous agent element
     * @param msg
     *            the msg
     * @return the point with least zombies
     */
    public List<String> getPointWithLeastZombies(
            final Element distributedAutonomousAgentElement, final FrameworkMessage msg) {
        // FIXME: Why does this have to be an element, and not Document?
        final Element agentModelActor = msg.getNextAgentModelActor(
                distributedAutonomousAgentElement, null);
        final List<Element> simulationDefinedEnvironmentChanges = (List<Element>) XMLUtilities
                .executeXPath(agentModelActor,
                        "./x:EnvironmentChanges/x:SimulationDefinedEnvironmentChanges",
                        namespaceStr, elementFilter);

        // FIXME: check the attributes
        final List<Element> locations = (List<Element>) XMLUtilities
                .executeXPath(
                        simulationDefinedEnvironmentChanges.get(0),
                        "./x:EnvironmentChange/x:Location[@category='neighborhood' and @entitytype='Zombie']",
                        namespaceStr, elementFilter);
        XMLUtilities.convertElementToXMLString(distributedAutonomousAgentElement, true);
        final Element location = locations.get(0);

        final String xValue = location.getChild("GridPointX", namespace).getText();
        final String yValue = location.getChild("GridPointY", namespace).getText();
        System.out.println("Zombies Grid Point X: " + xValue);
        System.out.println("Zombies Grid Point Y: " + yValue);
        final List<String> coordinate = new ArrayList<String>();
        coordinate.add(xValue);
        coordinate.add(yValue);

        return coordinate;
    }

    /*
     * public void sendMessageToSimulationAgent(JadeControllerInterface
     * jade_Controller_Agent, FrameworkMessage msg, String messageID, String
     * inReplyToMessageID) { jade_Controller_Agent.receiveMessage(msg,
     * messageID, inReplyToMessageID); }
     */

    /**
     * Populate least zombies point element.
     *
     * @param msg
     *            the msg
     * @param agentModelActor
     *            the agent model actor
     * @param GridPointX
     *            the grid point x
     * @param GridPointY
     *            the grid point y
     * @param cachedLocationTemplate
     *            the cached location template
     * @return the element
     */
    public Element populateLeastZombiesPointElement(final FrameworkMessage msg,
            final Element agentModelActor, final String GridPointX,
            final String GridPointY, final Element cachedLocationTemplate) {
        final Element locationEnvironmentChange = msg
                .getNextNonSelfSimulationDefinedLocationForActor(agentModelActor,
                        cachedLocationTemplate);

        final Element location = locationEnvironmentChange
                .getChild("Location", namespace);
        location.getChild("GridPointX", namespace).setText(GridPointX);
        location.getChild("GridPointY", namespace).setText(GridPointY);
        location.setAttribute("category", "neighborhood");
        location.setAttribute("includecenter", "true");
        location.setAttribute("entitytype", "Zombie");
        return location;
    }

    // FIXME: Remove moveToPoint
    /**
     * Populate zombies message.
     *
     * @param msg
     *            the msg
     * @param distributedAutononmousAgentID
     *            the distributed autononmous agent id
     * @param agentModelID
     *            the agent model id
     * @param thisAgentModelPosition
     *            the this agent model position
     * @param pointLeastZombies
     *            the point least zombies
     * @return the framework message
     */
    public FrameworkMessage populateZombiesMessage(final FrameworkMessage msg,
            final String distributedAutononmousAgentID, final String agentModelID,
            final List<String> thisAgentModelPosition,
            final List<String> pointLeastZombies) {

        final Element distributedAutonomousAgent = msg
                .getNextDistributedSoftwareAgentElement(msg.getDocument(),
                        jade_AgentContext.getCachedDistributedAutonomousAgentTemplate());
        msg.setDistributedAutonomousAgentID(distributedAutonomousAgent,
                distributedAutononmousAgentID);
        final Element agentModelActor = msg.getNextAgentModelActor(
                distributedAutonomousAgent,
                jade_AgentContext.getCachedAgentModelActorTemplate());

        if (thisAgentModelPosition.size() >= 2) {
            msg.setIDForActorInAgentModel(agentModelActor, agentModelID);
            msg.populateThisLocationInAgentModelActor(agentModelActor,
                    thisAgentModelPosition.get(0), thisAgentModelPosition.get(1),
                    jade_AgentContext.getCachedLocationTemplate());
        }

        // Use the shared class in the simulation for this.
        // FIXME: Move this to a common third project?
        if (pointLeastZombies.size() >= 2) {
            populateLeastZombiesPointElement(msg, agentModelActor,
                    pointLeastZombies.get(0), pointLeastZombies.get(1),
                    jade_AgentContext.getCachedLocationTemplate());
        }

        final XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        final String xmlString = outputter.outputString(msg.getDocument());
        System.out.println("Populated Zombie Message: " + xmlString);

        return msg;
    }

    /**
     * Send the simulation agent's local environment information to the
     * corresponding software agent's (e.g. JADE) agent model.
     *
     * @param loggingPrefix
     *            the logging prefix
     * @param simAgent
     *            the simulation agent
     * @param pt
     *            the pt
     * @param pointWithLeastZombies
     *            the point with least zombies
     */
    public void sendMsgFromSimAgentToDistributedAgentModel(final String loggingPrefix,
            final Object simAgent, final GridPoint pt,
            final GridPoint pointWithLeastZombies) {
        // TODO: Add support for multiple distributed systems
        // Get the Agent Mapping
        final SimulationDistributedSystemManager dsm = repastS_AgentContext_Cabsf
                .getRepastS_SimulationRunContext().getSimulationDistributedSystemManager(
                        simAgent);
        final AgentMapping am = dsm.getAgentMappingForObject(simAgent);
        // TODO: Add validation here
        assert (am != null);

        // Construct FrameworkMessage to send to the distributed agent
        final FrameworkMessage msg = new FrameworkMessageImpl(
                SYSTEM_TYPE.SIMULATION_ENGINE, SYSTEM_TYPE.DISTRIBUTED_SYSTEM,
                jade_AgentContext.getBlankCachedMessageExchangeTemplate());
        assert (repastS_AgentContext_Cabsf.getRepastS_SimulationRunContext()
                .getCachedDistributedAutonomousAgentTemplate() != null);

        // Get the distributed autonomous agent element and set the ID
        final Element distributedAutonomousAgentElement = msg
                .getNextDistributedSoftwareAgentElement(msg.getDocument(),
                        jade_AgentContext.getCachedDistributedAutonomousAgentTemplate());
        msg.setDistributedAutonomousAgentID(distributedAutonomousAgentElement,
                am.getSoftwareAgentID());

        // Get the agent model actor and set the ID
        final Element agentModelActor = msg.getNextAgentModelActor(
                distributedAutonomousAgentElement,
                jade_AgentContext.getCachedAgentModelActorTemplate());
        msg.setIDForActorInAgentModel(agentModelActor, am.getAgentModelID());

        // TODO: First get the distributed system manager section.
        // TODO: Add validation here
        assert (am.getAgentModelID() != null);

        // Set up the self agent model actor
        msg.populateThisLocationInAgentModelActor(agentModelActor,
                String.valueOf(pt.getX()), String.valueOf(pt.getY()),
                jade_AgentContext.getCachedLocationTemplate());

        // Populate the Zombies info
        populateLeastZombiesPointElement(msg, agentModelActor,
                String.valueOf(pointWithLeastZombies.getX()),
                String.valueOf(pointWithLeastZombies.getY()),
                jade_AgentContext.getCachedLocationTemplate());

        System.out.println(loggingPrefix
                + "Sending Message to Distributed System: "
                + XMLUtilities.convertDocumentToXMLString(msg.getDocument()
                        .getRootElement(), true));

        // The message has been constructed, now send it over the wire
        repastS_AgentContext_Cabsf.getRepastS_SimulationRunContext()
                .messageDistributedSystems(
                        msg,
                        repastS_AgentContext_Cabsf.getRepastS_SimulationRunContext()
                                .getSimulationRunContext());

    }

}
