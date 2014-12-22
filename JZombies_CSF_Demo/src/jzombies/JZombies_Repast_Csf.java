package jzombies;

import org.jdom2.Element;
import org.jdom2.Namespace;
import org.simulationsystems.csf.common.csfmodel.SYSTEM_TYPE;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessageImpl;
import org.simulationsystems.csf.common.internal.systems.AgentMapping;
import org.simulationsystems.csf.sim.core.api.distributedsystems.SimulationDistributedSystemManager;
import org.simulationsystems.csf.sim.engines.adapters.repastS.api.RepastS_AgentContext;

import repast.simphony.space.grid.GridPoint;

public class JZombies_Repast_Csf {

	private RepastS_AgentContext repastS_AgentContext;
	// TODO: Get this from the configuration
	private String namespaceStr = "http://www.simulationsystems.org/csf/schemas/CsfMessageExchange/0.1.0";
	private Namespace namespace = Namespace.getNamespace("x", namespaceStr);

	@SuppressWarnings("unused")
	private JZombies_Repast_Csf() {
		super();
	}

	public JZombies_Repast_Csf(RepastS_AgentContext repastS_AgentContext) {
		this.repastS_AgentContext = repastS_AgentContext;
	}

	public Element populatePointWithLeastZombies(FrameworkMessage msg,
			Element agentModelActor, String GridPointX, String GridPointY,
			Element cachedLocationTemplate) {
		Element location = msg.getNextNonSelfLocationForActor(agentModelActor,
				cachedLocationTemplate);
		location.getChild("GridPointX", namespace).setText(GridPointX);
		location.getChild("GridPointY", namespace).setText(GridPointY);
		location.setAttribute("category", "neighborhood");
		location.setAttribute("includecenter", "true");
		location.setAttribute("entitytype", "Zombie");
		return location;
	}

	public void sendCorrespondingDistributedAgentModelThisAgentLocationAndZombieLocations(
			Object obj, GridPoint pt, GridPoint pointWithLeastZombies) {
		// TODO: Add support for multiple distributed systems
		SimulationDistributedSystemManager dsm = repastS_AgentContext
				.getRepastS_SimulationRunContext()
				.getSimulationDistributedSystemManagers().iterator().next();
		AgentMapping am = dsm.getAgentMappingForObject(obj);
		// TODO: Add validation here
		assert (am != null);
		
		//Construct FrameworkMessage to send to the distributed agent
		FrameworkMessage msg = new FrameworkMessageImpl(SYSTEM_TYPE.SIMULATION_ENGINE,
				SYSTEM_TYPE.DISTRIBUTED_SYSTEM, repastS_AgentContext
						.getRepastS_SimulationRunContext()
						.getCachedMessageExchangeTemplate());
		assert (repastS_AgentContext.getRepastS_SimulationRunContext()
				.getCachedDistributedAutonomousAgentTemplate() != null);
		Element distributedAutonomousAgent = msg.getNextDistributedAutonomousAgent(msg
				.getDocument(), repastS_AgentContext.getRepastS_SimulationRunContext()
				.getCachedDistributedAutonomousAgentTemplate());

		Element agentModelActor = msg.getNextAgentModelActor(distributedAutonomousAgent,
				repastS_AgentContext.getRepastS_SimulationRunContext()
						.getCachedAgentModelActorTemplate());
		// TODO: First get the distributed system manager section.
		// TODO: Add validation here
		assert (am.getDistributedAgentModelID() != null);
		msg.populateThisActorLocationInAgentModel(agentModelActor,
				am.getDistributedAgentModelID(), String.valueOf(pt.getX()),
				String.valueOf(pt.getY()));
		
		msg.populateDistributedAutonomousAgent(distributedAutonomousAgent, am.getDistributedAutonomousAgentID());
		populatePointWithLeastZombies(msg, agentModelActor,
				String.valueOf(pointWithLeastZombies.getX()),
				String.valueOf(pointWithLeastZombies.getY()), repastS_AgentContext
						.getRepastS_SimulationRunContext().getCachedLocationTemplate());

		// The message has been constructed, now send it over the wire
		repastS_AgentContext.getRepastS_SimulationRunContext().messageDistributedSystems(
				msg,
				repastS_AgentContext.getRepastS_SimulationRunContext()
						.getSimulationRunContext());

	}
}