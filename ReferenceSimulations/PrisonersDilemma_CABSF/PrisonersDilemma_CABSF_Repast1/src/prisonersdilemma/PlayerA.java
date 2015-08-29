package prisonersdilemma;

import org.opensimulationsystems.cabsf.common.model.CABSF_SIMULATION_DISTRIBUATION_TYPE;

/**
 * A temporary Player (subclass of Player) created in place of the parent Player
 * object. Once the pairing completes, this object instance is no longer used
 * and is removed from the Repast Context.
 *
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class PlayerA extends Player {

    /**
     * This constructor is disabled.
     */
    private PlayerA() {
        super(null);
    }

    /**
     * Instantiates a new player A.
     *
     * @param gameAdministrator
     *            the game administrator
     */
    public PlayerA(final GameAdministrator gameAdministrator) {
        super(gameAdministrator);
    }

    /*
     * (non-Javadoc)
     * 
     * @see prisonersdilemma.Player#decide()
     */
    @Override
    public DECISION decide() {

        if (this.gameAdministrator.getSimulationType() == CABSF_SIMULATION_DISTRIBUATION_TYPE.DISTRIBUTED) {
            return sendRoundInformationToAndGetDecisionFromDistributedAgent(
                    this.gameAdministrator.getRound(),
                    this.gameAdministrator.getPreviousRoundPlayerBDecision(), null);
        } else {
            // For purposes of testing with the GUI (non-CABSF functionality)
            return DECISION.COOPERATE;
        }

    }
}
