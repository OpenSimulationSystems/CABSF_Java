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
import org.opensimulationsystems.cabsf.common.model.context.AgentContext;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FrameworkMessage;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FrameworkMessageImpl;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.JADE_MAS_AgentContext;
import org.opensimulationsystems.cabsf.sim.adapters.simengines.repastS.api.RepastS_AgentContext;
import org.opensimulationsystems.cabsf.sim.core.api.distributedsystems.SimulationDistributedSystemManager;

import repast.simphony.space.grid.GridPoint;

/*
 * Convenience class provided to the simulation and agent authors.  Unlike all other classes, this class has references to both the Repast Agent Context and JADE Agent Context. This allows all JZombies-specific code to be in one place.
 */
public class JZombies_CABSF_Helper {
	private final Filter<Element> elementFilter = new org.jdom2.filter.ElementFilter();
	private RepastS_AgentContext repastS_AgentContext;

	// TODO: Get this from the configuration
	private final String namespaceStr = "http://www.opensimulationsystems.org/cabsf/schemas/CabsfMessageExchange/0.1.0";
	private final Namespace namespace = Namespace.getNamespace("x", namespaceStr);
	private JADE_MAS_AgentContext jade_MAS_AgentContext;

	private final AgentContext agentContext;

	public JZombies_CABSF_Helper(final JADE_MAS_AgentContext jade_MAS_AgentContext) {
		this.jade_MAS_AgentContext = jade_MAS_AgentContext;
		agentContext = jade_MAS_AgentContext;
		try {
			jade_MAS_AgentContext.initializeCabsfAgent("TEST");
		} catch (final JDOMException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public JZombies_CABSF_Helper(final RepastS_AgentContext repastS_AgentContext) {
		this.repastS_AgentContext = repastS_AgentContext;
		agentContext = repastS_AgentContext;
	}

	public FrameworkMessage convertMoveToPointToFrameworkMessage(
			final List<String> pointMoveTo, final String distAutAgentID,
			final String distAutAgentModelID) {
		final FrameworkMessage msg = new FrameworkMessageImpl(
				SYSTEM_TYPE.DISTRIBUTED_SYSTEM, SYSTEM_TYPE.SIMULATION_ENGINE,
				agentContext.getBlankCachedMessageExchangeTemplate());
		populateZombiesMessage(msg, distAutAgentID, distAutAgentModelID, pointMoveTo,
				new ArrayList<String>());

		return msg;
	}

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
	 * public void sendMessageToSimulationAgent(JadeControllerInterface jade_Controller_Agent,
	 * FrameworkMessage msg, String messageID, String inReplyToMessageID) {
	 * jade_Controller_Agent.receiveMessage(msg, messageID, inReplyToMessageID); }
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
	public FrameworkMessage populateZombiesMessage(final FrameworkMessage msg,
			final String distributedAutononmousAgentID, final String agentModelID,
			final List<String> thisAgentModelPosition,
			final List<String> pointLeastZombies) {

		final Element distributedAutonomousAgent = msg.getNextDistributedAutonomousAgent(
				msg.getDocument(),
				agentContext.getCachedDistributedAutonomousAgentTemplate());
		msg.setDistributedAutonomousAgentID(distributedAutonomousAgent,
				distributedAutononmousAgentID);
		final Element agentModelActor = msg.getNextAgentModelActor(
				distributedAutonomousAgent,
				agentContext.getCachedAgentModelActorTemplate());

		if (thisAgentModelPosition.size() >= 2) {
			msg.setIDForActorInAgentModel(agentModelActor, agentModelID);
			msg.populateThisLocationInAgentModelActor(agentModelActor,
					thisAgentModelPosition.get(0), thisAgentModelPosition.get(1),
					agentContext.getCachedLocationTemplate());
		}

		// Use the shared class in the simulation for this.
		// FIXME: Move this to a common third project?
		if (pointLeastZombies.size() >= 2)
			populateLeastZombiesPointElement(msg, agentModelActor,
					pointLeastZombies.get(0), pointLeastZombies.get(1),
					agentContext.getCachedLocationTemplate());

		final XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
		final String xmlString = outputter.outputString(msg.getDocument());
		System.out.println("Populated Zombie Message: " + xmlString);

		return msg;
	}

	public void sendMessageToDistributedAutonomousAgentModelFromSimulationAgent(
			final String loggingPrefix, final Object obj, final GridPoint pt,
			final GridPoint pointWithLeastZombies) {
		// TODO: Add support for multiple distributed systems
		// Get the Agent Mapping
		final SimulationDistributedSystemManager dsm = repastS_AgentContext
				.getRepastS_SimulationRunContext()
				.getSimulationDistributedSystemManagers().iterator().next();
		final AgentMapping am = dsm.getAgentMappingForObject(obj);
		// TODO: Add validation here
		assert (am != null);

		// Construct FrameworkMessage to send to the distributed agent
		final FrameworkMessage msg = new FrameworkMessageImpl(
				SYSTEM_TYPE.SIMULATION_ENGINE, SYSTEM_TYPE.DISTRIBUTED_SYSTEM,
				agentContext.getBlankCachedMessageExchangeTemplate());
		assert (repastS_AgentContext.getRepastS_SimulationRunContext()
				.getCachedDistributedAutonomousAgentTemplate() != null);

		// Get the distributed autonomous agent element and set the ID
		final Element distributedAutonomousAgentElement = msg
				.getNextDistributedAutonomousAgent(msg.getDocument(),
						agentContext.getCachedDistributedAutonomousAgentTemplate());
		msg.setDistributedAutonomousAgentID(distributedAutonomousAgentElement,
				am.getDistributedAutonomousAgentID());

		// Get the agent model actor and set the ID
		final Element agentModelActor = msg.getNextAgentModelActor(
				distributedAutonomousAgentElement,
				agentContext.getCachedAgentModelActorTemplate());
		msg.setIDForActorInAgentModel(agentModelActor,
				am.getDistributedAutonomousAgentModelID());

		// TODO: First get the distributed system manager section.
		// TODO: Add validation here
		assert (am.getDistributedAutonomousAgentModelID() != null);

		// Set up the self agent model actor
		msg.populateThisLocationInAgentModelActor(agentModelActor,
				String.valueOf(pt.getX()), String.valueOf(pt.getY()),
				agentContext.getCachedLocationTemplate());

		// Populate the Zombies info
		populateLeastZombiesPointElement(msg, agentModelActor,
				String.valueOf(pointWithLeastZombies.getX()),
				String.valueOf(pointWithLeastZombies.getY()),
				agentContext.getCachedLocationTemplate());

		System.out.println(loggingPrefix
				+ "Sending Message to Distributed System: "
				+ XMLUtilities.convertDocumentToXMLString(msg.getDocument()
						.getRootElement(), true));

		// The message has been constructed, now send it over the wire
		repastS_AgentContext.getRepastS_SimulationRunContext().messageDistributedSystems(
				msg,
				repastS_AgentContext.getRepastS_SimulationRunContext()
				.getSimulationRunContext());

	}

}
