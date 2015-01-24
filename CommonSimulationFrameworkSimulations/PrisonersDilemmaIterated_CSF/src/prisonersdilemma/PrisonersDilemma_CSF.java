package prisonersdilemma;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.Filter;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.simulationsystems.csf.common.csfmodel.SYSTEM_TYPE;
import org.simulationsystems.csf.common.csfmodel.api.AgentContext;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessageImpl;
import org.simulationsystems.csf.common.internal.messaging.xml.XMLUtilities;
import org.simulationsystems.csf.common.internal.systems.AgentMapping;
import org.simulationsystems.csf.distsys.adapters.jade.api.JADE_MAS_AgentContext;
import org.simulationsystems.csf.distsys.adapters.jade.api.JadeControllerMock;
import org.simulationsystems.csf.sim.core.api.distributedsystems.SimulationDistributedSystemManager;
import org.simulationsystems.csf.sim.engines.adapters.repastS.api.RepastS_AgentContext;

import repast.simphony.space.grid.GridPoint;

/*
 * Convenience class provided to the simulation and agent authors.  Unlike all other classes, this class has references to both the Repast Agent Context and JADE Agent Context. This allows all JZombies-specific code to be in one place.
 */
public class PrisonersDilemma_CSF {
	private Filter<Element> elementFilter = new org.jdom2.filter.ElementFilter();
	private RepastS_AgentContext repastS_AgentContext;

	// TODO: Get this from the configuration
	private String namespaceStr = "http://www.simulationsystems.org/csf/schemas/CsfMessageExchange/0.1.0";
	private Namespace namespace = Namespace.getNamespace("x", namespaceStr);
	private JADE_MAS_AgentContext jade_MAS_AgentContext;

	private AgentContext agentContext;

	public PrisonersDilemma_CSF(JADE_MAS_AgentContext jade_MAS_AgentContext) {
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

	public PrisonersDilemma_CSF(RepastS_AgentContext repastS_AgentContext) {
		this.repastS_AgentContext = repastS_AgentContext;
		agentContext = (AgentContext) repastS_AgentContext;
	}

	public Element populatePrisonersDilemmaDecisionAndRoundElements(FrameworkMessage msg,
			Element agentModelActor, int round, DECISION otherPlayerLastDecision,
			DECISION myDecision) {
		Element simulationDefinedEnvironmentChange = msg
				.getSimulationDefinedEnvironmentChangesElement(agentModelActor);

		Element roundElement = new Element("RoundNumber", namespace);
		roundElement.setText(String.valueOf(round));
		simulationDefinedEnvironmentChange.addContent(roundElement);

		if (otherPlayerLastDecision != null) {
			Element decisionElement = new Element("OtherPlayerDecision", namespace);
			decisionElement.setText(otherPlayerLastDecision.toString());
			simulationDefinedEnvironmentChange.addContent(decisionElement);

		}
		if (myDecision != null) {
			Element decisionElement = new Element("ThisPlayerDecision", namespace);
			decisionElement.setText(myDecision.toString());
			simulationDefinedEnvironmentChange.addContent(decisionElement);
		}

		return simulationDefinedEnvironmentChange;
	}

	public void sendMessageToDistributedAutonomousAgentModelFromSimulationAgent(
			String loggingPrefix, Object obj, int round,
			DECISION otherPlayerLastDecision, DECISION myDecision) {
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

		// Get the distributed autonomous agent and set the ID
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

		// Populate the Decision and Round Info
		populatePrisonersDilemmaFrameworkMessage(msg, agentModelActor, round,
				otherPlayerLastDecision, myDecision);

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

	public DECISION getThisPlayerDecision(Element distributedAutonomousAgentElement,
			FrameworkMessage msg) {
		// FIXME: Why does this have to be an element, and not Document?
		Element agentModelActor = msg.getNextAgentModelActor(
				distributedAutonomousAgentElement, null);
		List<Element> simulationDefinedEnvironmentChanges = (List<Element>) XMLUtilities
				.executeXPath(agentModelActor,
						"./x:EnvironmentChanges/x:SimulationDefinedEnvironmentChanges",
						namespaceStr, elementFilter);

		String decisionStr = simulationDefinedEnvironmentChanges.get(0)
				.getChild("ThisPlayerDecision", namespace).getText();

		return DECISION.valueOf(decisionStr);
	}

	public DECISION getOtherPlayerDecision(Element distributedAutonomousAgentElement,
			FrameworkMessage msg) {
		// FIXME: Why does this have to be an element, and not Document?
		Element agentModelActor = msg.getNextAgentModelActor(
				distributedAutonomousAgentElement, null);
		List<Element> simulationDefinedEnvironmentChanges = (List<Element>) XMLUtilities
				.executeXPath(agentModelActor,
						"./x:EnvironmentChanges/x:SimulationDefinedEnvironmentChanges",
						namespaceStr, elementFilter);

		Element otherPlayerDecisionElement = simulationDefinedEnvironmentChanges.get(0)
				.getChild("OtherPlayerDecision", namespace);
		String decisionStr = null;
		if (otherPlayerDecisionElement != null)
			decisionStr = otherPlayerDecisionElement.getText();
		else
			return null;

		return DECISION.valueOf(decisionStr);
	}

	public Integer getRoundNumber(Element distributedAutonomousAgentElement,
			FrameworkMessage msg) {
		// FIXME: Why does this have to be an element, and not Document?
		Element agentModelActor = msg.getNextAgentModelActor(
				distributedAutonomousAgentElement, null);
		List<Element> simulationDefinedEnvironmentChanges = (List<Element>) XMLUtilities
				.executeXPath(agentModelActor,
						"./x:EnvironmentChanges/x:SimulationDefinedEnvironmentChanges",
						namespaceStr, elementFilter);

		String roundNumberStr = simulationDefinedEnvironmentChanges.get(0)
				.getChild("RoundNumber", namespace).getText();

		return Integer.parseInt(roundNumberStr);
	}

	public FrameworkMessage populatePrisonersDilemmaFrameworkMessage(
			FrameworkMessage msg, Element agentModelActor, int round,
			DECISION otherPlayerLastDecision, DECISION myDecision) {

		populatePrisonersDilemmaDecisionAndRoundElements(msg, agentModelActor, round,
				otherPlayerLastDecision, myDecision);

		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
		String xmlString = outputter.outputString(msg.getDocument());
		System.out.println("Populated Prisoner's Dilemma Message: " + xmlString);

		return msg;
	}

}
