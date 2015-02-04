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
import org.simulationsystems.csf.common.csfmodel.AgentMapping;
import org.simulationsystems.csf.common.csfmodel.SYSTEM_TYPE;
import org.simulationsystems.csf.common.csfmodel.context.AgentContext;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessageImpl;
import org.simulationsystems.csf.common.internal.messaging.xml.XMLUtilities;
import org.simulationsystems.csf.distsys.adapters.jade.api.JADE_MAS_AgentContext;
import org.simulationsystems.csf.distsys.adapters.jade.api.JadeControllerMock;
import org.simulationsystems.csf.sim.adapters.simengines.repastS.api.RepastS_AgentContext;
import org.simulationsystems.csf.sim.core.api.distributedsystems.SimulationDistributedSystemManager;

import repast.simphony.space.grid.GridPoint;

/*
 * Convenience class provided to the simulation and agent authors.  Unlike all other classes, this class has references to both the Repast Agent Context and JADE Agent Context. This allows all JZombies-specific code to be in one place.
 */
public class JZombies_Csf {
	private Filter<Element> elementFilter = new org.jdom2.filter.ElementFilter();
	private RepastS_AgentContext repastS_AgentContext;

	// TODO: Get this from the configuration
	private String namespaceStr = "http://www.simulationsystems.org/csf/schemas/CsfMessageExchange/0.1.0";
	private Namespace namespace = Namespace.getNamespace("x", namespaceStr);
	private JADE_MAS_AgentContext jade_MAS_AgentContext;

	private AgentContext agentContext;

	public JZombies_Csf(JADE_MAS_AgentContext jade_MAS_AgentContext) {
		this.jade_MAS_AgentContext = jade_MAS_AgentContext;
		agentContext = (AgentContext) jade_MAS_AgentContext;
		try {
			jade_MAS_AgentContext.initializeCsfAgent("TEST");
		} catch (JDOMException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public JZombies_Csf(RepastS_AgentContext repastS_AgentContext) {
		this.repastS_AgentContext = repastS_AgentContext;
		agentContext = (AgentContext) repastS_AgentContext;
	}

	public Element populateLeastZombiesPointElement(FrameworkMessage msg,
			Element agentModelActor, String GridPointX, String GridPointY,
			Element cachedLocationTemplate) {
		Element locationEnvironmentChange = msg
				.getNextNonSelfSimulationDefinedLocationForActor(agentModelActor,
						cachedLocationTemplate);

		Element location = locationEnvironmentChange.getChild("Location", namespace);
		location.getChild("GridPointX", namespace).setText(GridPointX);
		location.getChild("GridPointY", namespace).setText(GridPointY);
		location.setAttribute("category", "neighborhood");
		location.setAttribute("includecenter", "true");
		location.setAttribute("entitytype", "Zombie");
		return location;
	}

	public void sendMessageToDistributedAutonomousAgentModelFromSimulationAgent(
			String loggingPrefix, Object obj, GridPoint pt, GridPoint pointWithLeastZombies) {
		// TODO: Add support for multiple distributed systems
		// Get the Agent Mapping
		SimulationDistributedSystemManager dsm = repastS_AgentContext
				.getRepastS_SimulationRunContext()
				.getSimulationDistributedSystemManagers().iterator().next();
		AgentMapping am = dsm.getAgentMappingForObject(obj);
		// TODO: Add validation here
		assert (am != null);

		// Construct FrameworkMessage to send to the distributed agent
		FrameworkMessage msg = new FrameworkMessageImpl(SYSTEM_TYPE.SIMULATION_ENGINE,
				SYSTEM_TYPE.DISTRIBUTED_SYSTEM,
				agentContext.getBlankCachedMessageExchangeTemplate());
		assert (repastS_AgentContext.getRepastS_SimulationRunContext()
				.getCachedDistributedAutonomousAgentTemplate() != null);
		
		// Get the distributed autonomous agent element and set the ID
		Element distributedAutonomousAgentElement = msg
				.getNextDistributedAutonomousAgent(msg.getDocument(),
						agentContext.getCachedDistributedAutonomousAgentTemplate());
		msg.setDistributedAutonomousAgentID(distributedAutonomousAgentElement,
				am.getDistributedAutonomousAgentID());

		// Get the agent model actor and set the ID
		Element agentModelActor = msg.getNextAgentModelActor(
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

	/*
	 * public void sendMessageToSimulationAgent(JadeControllerMock jade_Controller_Agent,
	 * FrameworkMessage msg, String messageID, String inReplyToMessageID) {
	 * jade_Controller_Agent.receiveMessage(msg, messageID, inReplyToMessageID); }
	 */

	public FrameworkMessage convertMoveToPointToFrameworkMessage(
			List<String> pointMoveTo, String distAutAgentID, String distAutAgentModelID) {
		FrameworkMessage msg = new FrameworkMessageImpl(SYSTEM_TYPE.DISTRIBUTED_SYSTEM,
				SYSTEM_TYPE.SIMULATION_ENGINE,
				agentContext.getBlankCachedMessageExchangeTemplate());
		populateZombiesMessage(msg, distAutAgentID, distAutAgentModelID, pointMoveTo,
				new ArrayList<String>());

		return msg;
	}

	public List<String> getPointWithLeastZombies(
			Element distributedAutonomousAgentElement, FrameworkMessage msg) {
		// FIXME: Why does this have to be an element, and not Document?
		Element agentModelActor = msg.getNextAgentModelActor(
				distributedAutonomousAgentElement, null);
		List<Element> simulationDefinedEnvironmentChanges = (List<Element>) XMLUtilities
				.executeXPath(agentModelActor,
						"./x:EnvironmentChanges/x:SimulationDefinedEnvironmentChanges",
						namespaceStr, elementFilter);

		// FIXME: check the attributes
		List<Element> locations = (List<Element>) XMLUtilities
				.executeXPath(
						simulationDefinedEnvironmentChanges.get(0),
						"./x:EnvironmentChange/x:Location[@category='neighborhood' and @entitytype='Zombie']",
						namespaceStr, elementFilter);
		XMLUtilities.convertElementToXMLString(distributedAutonomousAgentElement, true);
		Element location = locations.get(0);

		String xValue = location.getChild("GridPointX", namespace).getText();
		String yValue = location.getChild("GridPointY", namespace).getText();
		System.out.println("Zombies Grid Point X: " + xValue);
		System.out.println("Zombies Grid Point Y: " + yValue);
		List<String> coordinate = new ArrayList<String>();
		coordinate.add(xValue);
		coordinate.add(yValue);

		return coordinate;
	}

	// FIXME: Remove moveToPoint
	public FrameworkMessage populateZombiesMessage(FrameworkMessage msg,
			String distributedAutononmousAgentID, String agentModelID,
			List<String> thisAgentModelPosition, List<String> pointLeastZombies) {

		Element distributedAutonomousAgent = msg.getNextDistributedAutonomousAgent(
				msg.getDocument(),
				agentContext.getCachedDistributedAutonomousAgentTemplate());
		msg.setDistributedAutonomousAgentID(distributedAutonomousAgent,
				distributedAutononmousAgentID);
		Element agentModelActor = msg.getNextAgentModelActor(distributedAutonomousAgent,
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
			populateLeastZombiesPointElement(msg, agentModelActor, pointLeastZombies.get(0),
					pointLeastZombies.get(1), agentContext.getCachedLocationTemplate());

		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
		String xmlString = outputter.outputString(msg.getDocument());
		System.out.println("Populated Zombie Message: " + xmlString);

		return msg;
	}

}
