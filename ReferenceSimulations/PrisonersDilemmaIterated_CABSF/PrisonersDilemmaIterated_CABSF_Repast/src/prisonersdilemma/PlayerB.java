package prisonersdilemma;

import org.opensimulationsystems.cabsf.common.model.SIMULATION_TYPE;

/**
 * A temporary Player (subclass of Player) created in place of the parent Player object.
 * Once the pairing completes, this object instance is no longer used and is removed from
 * the Repast Context.The Class PlayerB.
 * 
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class PlayerB extends Player {

	/**
	 * Disabled constructor
	 */
	private PlayerB() {
		super(null);
	}

	/**
	 * Instantiates a new player B.
	 * 
	 * @param gameAdministrator
	 *            the game administrator
	 */
	public PlayerB(final GameAdministrator gameAdministrator) {
		super(gameAdministrator);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see prisonersdilemma.Player#decide()
	 */
	@Override
	public DECISION decide() {
		if (this.gameAdministrator.getSimulationType() == SIMULATION_TYPE.CABSF_SIMULATION) {
			return sendRoundInformationToAndGetDecisionFromDistributedAgent(
					this.gameAdministrator.getRound(),
					this.gameAdministrator.getLastPlayerADecision(), null);
		} else {
			return DECISION.DEFECT;
		}
	}
}
