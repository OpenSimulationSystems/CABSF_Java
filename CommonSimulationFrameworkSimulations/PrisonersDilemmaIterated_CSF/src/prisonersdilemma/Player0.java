package prisonersdilemma;

import org.simulationsystems.csf.common.csfmodel.SIMULATION_TYPE;

public class Player0 extends Player {

	private Player0() {
		super(null);
	}

	public Player0(GameAdministrator gameAdministrator) {
		super(gameAdministrator);
	}

	@Override
	public DECISION decide() {

		if (this.gameAdministrator.getSimulationType() == SIMULATION_TYPE.CSF_SIMULATION) {
			return sendRoundInformationToAndGetDecisionFromDistributedAgent(
					this.gameAdministrator.getRound(),
					this.gameAdministrator.getLastPlayer1Decision(), null);
		} else {
			return DECISION.COOPERATE;
		}

	}
}
