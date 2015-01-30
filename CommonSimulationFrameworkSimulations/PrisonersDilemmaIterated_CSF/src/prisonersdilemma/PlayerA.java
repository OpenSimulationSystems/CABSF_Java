package prisonersdilemma;

import org.simulationsystems.csf.common.csfmodel.SIMULATION_TYPE;

public class PlayerA extends Player {

	private PlayerA() {
		super(null);
	}

	public PlayerA(GameAdministrator gameAdministrator) {
		super(gameAdministrator);
	}

	@Override
	public DECISION decide() {

		if (this.gameAdministrator.getSimulationType() == SIMULATION_TYPE.CSF_SIMULATION) {
			return sendRoundInformationToAndGetDecisionFromDistributedAgent(
					this.gameAdministrator.getRound(),
					this.gameAdministrator.getLastPlayer1Decision(), null);
		} else {
			//For purposes of testing with the GUI (non-CSF functionality)
			return DECISION.COOPERATE;
		}

	}
}
