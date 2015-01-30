package prisonersdilemma;

import org.simulationsystems.csf.common.csfmodel.SIMULATION_TYPE;

public class PlayerB extends Player {

	private PlayerB() {
		super(null);
	}

	public PlayerB(GameAdministrator gameAdministrator) {
		super(gameAdministrator);
	}

	@Override
	public DECISION decide() {
		if (this.gameAdministrator.getSimulationType() == SIMULATION_TYPE.CSF_SIMULATION) {
			return sendRoundInformationToAndGetDecisionFromDistributedAgent(
					this.gameAdministrator.getRound(),
					this.gameAdministrator.getLastPlayer0Decision(), null);
		} else {
			return DECISION.DEFECT;
		}
	}
}
