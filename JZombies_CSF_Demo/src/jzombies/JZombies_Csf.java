package jzombies;

import org.jdom2.Element;
import org.simulationsystems.csf.common.csfmodel.SYSTEM_TYPE;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FRAMEWORK_COMMAND;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessageImpl;
import org.simulationsystems.csf.common.internal.systems.AgentMapping;
import org.simulationsystems.csf.sim.core.api.distributedsystems.SimulationDistributedSystemManager;
import org.simulationsystems.csf.sim.engines.adapters.repastS.api.RepastS_AgentContext;

import repast.simphony.space.grid.GridPoint;

public class JZombies_Csf {

	private RepastS_AgentContext repastS_AgentContext;

	@SuppressWarnings("unused")
	private JZombies_Csf() {
		super();
	}

	public JZombies_Csf(RepastS_AgentContext repastS_AgentContext) {
		this.repastS_AgentContext = repastS_AgentContext;
	}

	public Element populatePointWithLeastZombies(Element agentModelActor,
			String GridPointX, String GridPointY, Element cachedLocationTemplate) {
		Element location = getNextNonSelfLocationForActor(agentModelActor,
				cachedLocationTemplate);
		location.getChild("GridPointX", namespace).setText(GridPointX);
		location.getChild("GridPointY", namespace).setText(GridPointY);
		location.setAttribute("category", "neighborhood");
		location.setAttribute("includecenter", "true");
		location.setAttribute("entitytype", "Zombie");
		return location;
	}

	public void sendDistributedAgentThisAgentLocationAndZombieLocations(Object obj,
			GridPoint pt, GridPoint pointWithLeastZombies) {
		FrameworkMessage msg = new FrameworkMessageImpl(SYSTEM_TYPE.SIMULATION_ENGINE,
				SYSTEM_TYPE.DISTRIBUTED_SYSTEM, repastS_AgentContext
						.getRepastS_SimulationRunContext()
						.getCachedMessageExchangeTemplate());
		Element agentModelActor = msg.getNextAgentModelActor(msg.getDocument(),
				repastS_AgentContext.getRepastS_SimulationRunContext()
						.getCachedAgentModelActorTemplate());
		// TODO: Add support for multiple distributed systems
		SimulationDistributedSystemManager dsm = repastS_AgentContext
				.getRepastS_SimulationRunContext()
				.getSimulationDistributedSystemManagers().iterator().next();
		AgentMapping am = dsm.getAgentMappingForObject(obj);
		// TODO: Add validation here
		assert (am != null);

		msg.processActorForAgentModel(agentModelActor, "teststring1",
				String.valueOf(pt.getX()), String.valueOf(pt.getY()));

	}
}
