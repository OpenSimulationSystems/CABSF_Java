package prisonersdilemma;

import java.util.List;
import org.simulationsystems.csf.common.csfmodel.SIMULATION_TYPE;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.internal.messaging.xml.XMLUtilities;
import org.simulationsystems.csf.common.internal.systems.AgentMapping;
import org.simulationsystems.csf.sim.core.api.distributedsystems.SimulationDistributedSystemManager;
import org.simulationsystems.csf.sim.engines.adapters.repastS.api.RepastS_AgentAdapterAPI;
import org.simulationsystems.csf.sim.engines.adapters.repastS.api.RepastS_AgentContext;

import repast.simphony.context.Context;
import repast.simphony.query.space.grid.GridCell;

public abstract class Player {
	private Player currentCompetitor;
	private int totalScore;
	private int playerNumber;
	protected GameAdministrator gameAdministrator;

	public Player(GameAdministrator gameAdministrator) {
		this.gameAdministrator = gameAdministrator;
	}

	public int getPlayerNumber() {
		return playerNumber;
	}

	public void setPlayerNumber(int playerNumber) {
		this.playerNumber = playerNumber;
	}

	public void intializeGame(Player player) {
		this.currentCompetitor = player;
	}

	abstract public DECISION decide();

	public void updateScore(int newScore) {
		totalScore += newScore;
		System.out.println("[Game Administrator] Player " + String.valueOf(playerNumber)
				+ " got new score: " + String.valueOf(newScore) + " New Total Score: "
				+ String.valueOf(totalScore));
	}

	public int getTotalScore() {
		return totalScore;
	}

	public DECISION sendRoundInformationToAndGetDecisionFromDistributedAgent(int round,
			DECISION otherPlayerLastDecision, DECISION myDecision) {
		// //////////////////////////
		// CSF-specific
		// If it is part of a CSF simulation, move the decision-making to the distributed
		// FIXME: Support multiple distributed system managers/provide method for getting
		// the appropriate one.
		SimulationDistributedSystemManager dsm = gameAdministrator
				.getRepastS_AgentContext().getRepastS_SimulationRunContext()
				.getSimulationDistributedSystemManagers().iterator().next();
		AgentMapping am = dsm.getAgentMappingForObject(this);

		String distributedSystemID = am.getDistributedSystemID();
		String distributedAutonomousAgentID = am.getDistributedAutonomousAgentID();
		String distributedAutonomousAgentModelID = am
				.getDistributedAutonomousAgentModelID();
		String loggingPrefix = "[Player " + String.valueOf(playerNumber) + " "
				+ distributedSystemID + " " + distributedAutonomousAgentID + " "
				+ distributedAutonomousAgentModelID + "] ";

		// Communicate the local environment information for this agent to the
		// distributed agent (agent model)
		// LOW: Add support for merging multiple messages bound for different agents
		gameAdministrator.getPrisonersDilemma_CSF()
				.sendMessageToDistributedAutonomousAgentModelFromSimulationAgent(loggingPrefix, this,
						round, otherPlayerLastDecision, myDecision);

		// FIXME: Move to simultaneous processing of these messages?
		FrameworkMessage msg = gameAdministrator.getRepastS_AgentContext()
				.getRepastS_SimulationRunContext()
				.readFrameworkMessageFromDistributedSystem();

		System.out.println(loggingPrefix
				+ "Received distributed decision: "
				+ XMLUtilities.convertDocumentToXMLString(msg.getDocument()
						.getRootElement(), true));

		DECISION thisPlayersDecision = gameAdministrator.getPrisonersDilemma_CSF().getThisPlayerDecision(
				msg.getNextDistributedAutonomousAgent(msg.getDocument(), null), msg);
		
		return thisPlayersDecision;
		//////////////////////////////////////////////
	}
}
