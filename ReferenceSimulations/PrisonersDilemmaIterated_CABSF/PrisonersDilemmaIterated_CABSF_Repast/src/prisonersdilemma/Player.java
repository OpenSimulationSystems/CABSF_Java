package prisonersdilemma;

import org.opensimulationsystems.cabsf.common.internal.messaging.xml.XMLUtilities;
import org.opensimulationsystems.cabsf.common.model.AgentMapping;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FrameworkMessage;
import org.opensimulationsystems.cabsf.sim.core.api.distributedsystems.SimulationDistributedSystemManager;

/**
 * The Player in the simulation. The Player pairings for the simulation are performed at
 * the simulation level by the GameAdministrator. There is also a PlayerA and PlayerB
 * subclasses which are initialized and added to the RepastS context at the start of any
 * given pairing play. Those temporary object instances have a reference to this parent
 * class. Those child classes are also Player objects themselves due to inheritance. This
 * complicated setup was required to allow the RepastS GUI to create a visualization as it
 * expects comparisons to be performed across different types of classes, hence the need
 * for PlayerA and PlayerB. Most of the code used by PlayerA and PlayerB is actually in
 * this parent class.
 * 
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class Player {

	/** The current competitor. */
	private Player currentCompetitor;

	/** The running score. */
	private int runningScore;

	/** This player number that is unique across all players in the simulation. */
	private int playerNumber;

	/** The game administrator. */
	protected GameAdministrator gameAdministrator;

	/** The player parent. This only applies to PlayerA and PlayerB instances */
	private Player playerParent;

	/** The total score. */
	private int totalScore;

	/**
	 * Instantiates a new Player.
	 * 
	 * @param gameAdministrator
	 *            the game administrator
	 */
	public Player(final GameAdministrator gameAdministrator) {
		this.gameAdministrator = gameAdministrator;
	}

	/**
	 * Only used by the PlayerA and PlayerB child classes. It is specified here to enable
	 * inheritance/polymorphism. Called by the GameAdministrator on a Player to make a
	 * COOPERATE or DEFECT decision.
	 * 
	 * @return the decision
	 */
	public DECISION decide() {
		return null;
	}

	/**
	 * Gets the player number.
	 * 
	 * @return the player number
	 */
	public int getPlayerNumber() {
		return playerNumber;
	}

	/**
	 * Gets the player parent. Only applies to the PlayerA and PlayerB child classes.
	 * 
	 * @return the player parent
	 */
	public Player getPlayerParent() {
		return playerParent;
	}

	/**
	 * Gets the running score for all rounds for this player in the current pairing.
	 * 
	 * @return the running score
	 */
	public int getRunningScore() {
		return runningScore;
	}

	/**
	 * Gets the current total score for the player across all rounds and pairings.
	 * 
	 * @return the total score
	 */
	public int getTotalScore() {
		return totalScore;
	};

	/**
	 * Initialize the game.
	 * 
	 * @param player
	 *            the player
	 */
	public void intializeGame(final Player player) {
		this.currentCompetitor = player;
	}

	/**
	 * Sends the current round number and last player's decision to this agent's
	 * corresponding distributed JADE agent. It also receives the distributed JADE agent's
	 * decision, which it returns to the caller of this method.
	 * 
	 * @param round
	 *            the round number
	 * @param otherPlayerLastDecision
	 *            the other player's last decision
	 * @param myDecision
	 *            not used. To be removed
	 * @return the decision
	 */
	// FIXME: Remove the myDecision
	public DECISION sendRoundInformationToAndGetDecisionFromDistributedAgent(
			final int round, final DECISION otherPlayerLastDecision,
			final DECISION myDecision) {
		// FIXME: Support multiple distributed system managers/provide method
		// for getting the appropriate one.
		final SimulationDistributedSystemManager dsm = gameAdministrator
				.getRepastS_AgentContext().getRepastS_SimulationRunContext()
				.getSimulationDistributedSystemManagers().iterator().next();
		final AgentMapping am = dsm.getAgentMappingForObject(this.getPlayerParent());
		assert (am != null);

		final String distributedSystemID = am.getDistributedSystemID();
		final String distributedAutonomousAgentID = am.getDistributedAutonomousAgentID();
		final String distributedAutonomousAgentModelID = am
				.getDistributedAutonomousAgentModelID();
		final String loggingPrefix = "[Player " + String.valueOf(playerNumber) + " "
				+ distributedSystemID + " " + distributedAutonomousAgentID + " "
				+ distributedAutonomousAgentModelID + "] ";

		// Communicate the local environment information for this agent to the
		// distributed agent (agent model)
		// LOW: Add support for merging multiple messages bound for different
		// agents
		gameAdministrator.getPrisonersDilemma_CSF()
				.sendMessageToDistributedAutonomousAgentModelFromSimulationAgent(
						loggingPrefix, this.getPlayerParent(), round,
						otherPlayerLastDecision, myDecision);

		// FIXME: Move to simultaneous processing of these messages?
		final FrameworkMessage msg = gameAdministrator.getRepastS_AgentContext()
				.getRepastS_SimulationRunContext()
				.readFrameworkMessageFromDistributedSystem();

		final int distributedAgentsRoundNumber = gameAdministrator
				.getPrisonersDilemma_CSF().getRoundNumber(
						msg.getNextDistributedAutonomousAgent(msg.getDocument(), null),
						msg);
		assert (round == distributedAgentsRoundNumber);

		System.out.println(loggingPrefix
				+ "Received distributed decision: "
				+ XMLUtilities.convertDocumentToXMLString(msg.getDocument()
						.getRootElement(), true));

		final DECISION thisPlayersDecision = gameAdministrator.getPrisonersDilemma_CSF()
				.getThisPlayerDecision(
						msg.getNextDistributedAutonomousAgent(msg.getDocument(), null),
						msg);

		return thisPlayersDecision;
	}

	/**
	 * Sets the player number.
	 * 
	 * @param playerNumber
	 *            the new player number
	 */
	public void setPlayerNumber(final int playerNumber) {
		this.playerNumber = playerNumber;
	}

	/**
	 * Sets the player parent.
	 * 
	 * @param playerParent
	 *            the new player parent
	 */
	public void setPlayerParent(final Player playerParent) {
		this.playerParent = playerParent;
	}

	/**
	 * Update running score.
	 * 
	 * @param newScore
	 *            the new score
	 * @param playerStr
	 *            the player string for logging purposes
	 */
	public void updateRunningScore(final int newScore, final String playerStr) {
		runningScore += newScore;
		System.out.println("[Game Administrator] Updating Running Scores.  Player "
				+ String.valueOf(playerNumber) + " (" + playerStr + ")"
				+ " got new score: " + String.valueOf(newScore) + " New Running Score: "
				+ String.valueOf(runningScore));
	}

	/*
	 * Total scores are kept by the player parent
	 */
	/**
	 * Update the total score.
	 * 
	 * @param newScore
	 *            the new score
	 * @param playerStr
	 *            the player string for logging purposes
	 */
	public void updateTotalScore(final int newScore, final String playerStr) {
		totalScore += newScore;
		System.out.println("[Game Administrator] Updating Total Scores.  Player "
				+ String.valueOf(playerNumber) + " (" + playerStr + ")"
				+ " got new score: " + String.valueOf(newScore) + " New Total Score: "
				+ String.valueOf(totalScore));
	}
}
