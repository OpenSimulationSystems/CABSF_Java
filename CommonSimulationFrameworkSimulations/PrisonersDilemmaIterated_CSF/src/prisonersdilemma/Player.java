package prisonersdilemma;

import org.apache.batik.xml.XMLUtilities;

public class Player {
	private Player currentCompetitor;
	private int runningScore;
	private int playerNumber;
	protected GameAdministrator gameAdministrator;
	private Player playerParent;
	private int totalScore;

	public Player(GameAdministrator gameAdministrator) {
		this.gameAdministrator = gameAdministrator;
	}

	/*
	 * Child class makes the decision
	 */
	public DECISION decide() {
		return null;
	}

	public int getPlayerNumber() {
		return playerNumber;
	}

	public Player getPlayerParent() {
		return playerParent;
	}

	public int getRunningScore() {
		return runningScore;
	}

	public int getTotalScore() {
		return totalScore;
	};

	public void intializeGame(Player player) {
		this.currentCompetitor = player;
	}

	public DECISION sendRoundInformationToAndGetDecisionFromDistributedAgent(
			int round, DECISION otherPlayerLastDecision, DECISION myDecision) {
		// //////////////////////////
		// CSF-specific
		// If it is part of a CSF simulation, move the decision-making to the
		// distributed
		// FIXME: Support multiple distributed system managers/provide method
		// for getting
		// the appropriate one.
		SimulationDistributedSystemManager dsm = gameAdministrator
				.getRepastS_AgentContext().getRepastS_SimulationRunContext()
				.getSimulationDistributedSystemManagers().iterator().next();
		AgentMapping am = dsm.getAgentMappingForObject(this.getPlayerParent());
		assert (am != null);

		String distributedSystemID = am.getDistributedSystemID();
		String distributedAutonomousAgentID = am
				.getDistributedAutonomousAgentID();
		String distributedAutonomousAgentModelID = am
				.getDistributedAutonomousAgentModelID();
		String loggingPrefix = "[Player " + String.valueOf(playerNumber) + " "
				+ distributedSystemID + " " + distributedAutonomousAgentID
				+ " " + distributedAutonomousAgentModelID + "] ";

		// Communicate the local environment information for this agent to the
		// distributed agent (agent model)
		// LOW: Add support for merging multiple messages bound for different
		// agents
		gameAdministrator
		.getPrisonersDilemma_CSF()
		.sendMessageToDistributedAutonomousAgentModelFromSimulationAgent(
				loggingPrefix, this.getPlayerParent(), round,
				otherPlayerLastDecision, myDecision);

		// FIXME: Move to simultaneous processing of these messages?
		FrameworkMessage msg = gameAdministrator.getRepastS_AgentContext()
				.getRepastS_SimulationRunContext()
				.readFrameworkMessageFromDistributedSystem();

		int distributedAgentsRoundNumber = gameAdministrator
				.getPrisonersDilemma_CSF().getRoundNumber(
						msg.getNextDistributedAutonomousAgent(
								msg.getDocument(), null), msg);
		assert (round == distributedAgentsRoundNumber);

		System.out.println(loggingPrefix
				+ "Received distributed decision: "
				+ XMLUtilities.convertDocumentToXMLString(msg.getDocument()
						.getRootElement(), true));

		DECISION thisPlayersDecision = gameAdministrator
				.getPrisonersDilemma_CSF().getThisPlayerDecision(
						msg.getNextDistributedAutonomousAgent(
								msg.getDocument(), null), msg);

		return thisPlayersDecision;
		// ////////////////////////////////////////////
	}

	public void setPlayerNumber(int playerNumber) {
		this.playerNumber = playerNumber;
	}

	public void setPlayerParent(Player playerParent) {
		this.playerParent = playerParent;
	}

	public void updateRunningScore(int newScore, String playerStr) {
		runningScore += newScore;
		System.out
		.println("[Game Administrator] Updating Running Scores.  Player "
				+ String.valueOf(playerNumber)
				+ " ("
				+ playerStr
				+ ")"
				+ " got new score: "
				+ String.valueOf(newScore)
				+ " New Running Score: " + String.valueOf(runningScore));
	}

	/*
	 * Total scores are kept by the player parent
	 */
	public void updateTotalScore(int newScore, String playerStr) {
		totalScore += newScore;
		System.out
		.println("[Game Administrator] Updating Total Scores.  Player "
				+ String.valueOf(playerNumber) + " (" + playerStr + ")"
				+ " got new score: " + String.valueOf(newScore)
				+ " New Total Score: " + String.valueOf(totalScore));
	}
}
