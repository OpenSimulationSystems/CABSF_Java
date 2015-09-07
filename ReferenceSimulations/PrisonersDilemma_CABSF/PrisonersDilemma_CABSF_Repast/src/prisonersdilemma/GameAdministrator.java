package prisonersdilemma;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.opensimulationsystems.cabsf.common.model.CABSF_SIMULATION_DISTRIBUATION_TYPE;
import org.opensimulationsystems.cabsf.common.model.cabsfexceptions.CabsfInitializationRuntimeException;
import org.opensimulationsystems.cabsf.sim.adapters.simengines.repastS.api.RepastS_AgentAdapterAPI;
import org.opensimulationsystems.cabsf.sim.adapters.simengines.repastS.api.RepastS_AgentContext_Cabsf;
import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.environment.RunState;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;

// TODO: Auto-generated Javadoc
/**
 * The object that manages the the simulation game. GameAdministrator is the
 * only class in this simulation with a scheduled RepastS step function. It
 * creates the initial set of Player pairings, calls each Player object to make
 * a COOPERATE or DEFECT decision, and keeps track of scores. Only one player
 * pairing is active at any given time.
 *
 * When utilizing CABSF's distributed mode, these Player human agents are
 * distributed to software agents running outside of the RepastS
 * process/runtime, such as to agents running in a JADE multi-agent system. In
 * such cases, this Player agent here in RepastS is still used to communicate
 * with the RepastS simulation runtime, but it becomes more of a representation
 * of the "actual" agent that exists outside of RepastS, which is the one really
 * making the decisions in the simulation (i.e., the intelligent agent).
 *
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class GameAdministrator {

    /** The current pairing number. */
    int pairingNumber;

    /** The game initialized. */
    boolean gameInitialized = false;

    /** The round. */
    private int round = 0;

    /** The simulation parameters in the simulation's .rs directory . */
    Parameters params = RunEnvironment.getInstance().getParameters();

    /** The total number of rounds per pairing. */
    int numberOfRounds = (Integer) params.getValue("number_of_rounds");

    /**
     * The native Repast Simphony context. Different from CABSF's RepastS agent
     * context
     */
    private Context nativeRepastScontext;

    /**
     * The player pairings. Mapping unique player numbers to a List/Paring of 2
     * Players
     */
    private HashMap<Long, List<Player>> playerPairings;

    /** The player pairings iterator. */
    private Iterator<Entry<Long, List<Player>>> playerPairingsIterator;

    /**
     * The player0 decision in the previous round for the current Player
     * pairing. The player only learns of his/her opponent's DECISION after a
     * round.
     */
    private DECISION previousRoundPlayerADecision;

    /**
     * The player1 decision in the previous round for the current Player
     * pairing. The player only learns of his/her opponent's DECISION after a
     * round.
     */
    private DECISION previousRoundPlayerBDecision;

    /** The player A in the current pairing. */
    private PlayerA playerA;

    /** The player G in the current pairing. */
    private PlayerB playerB;

    /** The RepastS_AgentContext_Cabsf. */
    private RepastS_AgentContext_Cabsf repastS_AgentContext_Cabsf = null;

    /** The simulation type. */
    private CABSF_SIMULATION_DISTRIBUATION_TYPE cabsfSimulationType;

    /** The prisoners dilemma_ CABSF convenience class. */
    private PrisonersDilemma_CABSF_Helper prisonersDilemma_CABSF_Helper;

    /** The set of standalone parent Player objects (not PlayerA or PlayerB). */
    private HashSet<Player> standaloneParentPlayerSet;

    /** The player a in the previous agent pairing. */
    private PlayerA previousPairingPlayerA;

    /** The player b in the previous agent pairing. */
    private PlayerB previousPairingPlayerB;

    /**
     * Creates the initial set of pairings between Player objects. Each of these
     * Player objects are later mapped to either a separate PlayerA and PlayerB
     * object created for a specific round. Those objects are themselves child
     * classes of Player. The number of pairings is determined by the number of
     * non-repeating combinations of players.
     *
     * @param playersSet
     *            the players set
     * @return the hash map
     */
    private HashMap<Long, List<Player>> createInitialPlayerPairings(
            final Set<Player> playersSet) {
        playerPairings = new HashMap<Long, List<Player>>();

        final Object[] playerArray = playersSet.toArray();
        // Create the initial vector
        final ICombinatoricsVector<Object> initialVector = Factory
                .createVector(playerArray);

        // Create a simple combination generator to generate
        // 2-player-combinations of
        // the initial vector
        final Generator<Object> gen = Factory.createSimpleCombinationGenerator(
                initialVector, 2);

        // Print all possible combinations
        long i = 0;
        for (final ICombinatoricsVector<Object> combination : gen) {
            System.out.println(combination);

            Player playeraTemp = null;
            Player playerbTemp = null;

            final List<Object> cominationLst = combination.getVector();
            playeraTemp = (Player) cominationLst.get(0);
            playerbTemp = (Player) cominationLst.get(1);

            assert (playeraTemp != null && playerbTemp != null && playeraTemp != playerbTemp);

            final List<Player> newList = new ArrayList<Player>();
            newList.add(playeraTemp);
            newList.add(playerbTemp);
            playerPairings.put(i, newList);
            i++;
        }

        return playerPairings;
    }

    /**
     * Gets the last playerA decision.
     *
     * @return the last playerB decision
     */
    public DECISION getPreviousRoundPlayerADecision() {
        return previousRoundPlayerADecision;
    }

    /**
     * Gets the last playerB decision.
     *
     * @return the last playerB decision
     */
    public DECISION getPreviousRoundPlayerBDecision() {
        return previousRoundPlayerBDecision;
    }

    /**
     * Gets the PrisonersDilemma_CABSF_Helper convenience class
     *
     * @return the PrisonersDilemma_CABSF_Helper convenience class
     */
    public PrisonersDilemma_CABSF_Helper getPrisonersDilemma_CABSF() {
        return prisonersDilemma_CABSF_Helper;
    }

    /**
     * Gets the RepastS_AgentContext_Cabsf.
     *
     * @return the RepastS_AgentContext_Cabsf
     */
    public RepastS_AgentContext_Cabsf getRepastS_AgentContext() {
        return repastS_AgentContext_Cabsf;
    }

    /**
     * Gets the native repast Context.
     *
     * @return the repast Context
     */
    public Context<Object> getRepastScontext() {
        return nativeRepastScontext;
    }

    /**
     * Gets the round.
     *
     * @return the round
     */
    public int getRound() {
        return round;
    }

    /**
     * Gets the simulation type.
     *
     * @return the simulation type
     */
    public CABSF_SIMULATION_DISTRIBUATION_TYPE getSimulationType() {
        return cabsfSimulationType;
    }

    /**
     * Initialize overall tournament.
     */
    public void initializeOverallTournament() {
        // ////////////////////////////////
        // Section Added to the CABSF-wired version of the JZombies
        // simulation
        System.out.println("[Game Administrator] Initializing Game");

        if (cabsfSimulationType == null) {
            nativeRepastScontext = RunState.getInstance().getMasterContext();
            repastS_AgentContext_Cabsf = RepastS_AgentAdapterAPI.getInstance()
                    .getAgentContext();
            prisonersDilemma_CABSF_Helper = new PrisonersDilemma_CABSF_Helper(
                    repastS_AgentContext_Cabsf);

            try {
                cabsfSimulationType = repastS_AgentContext_Cabsf
                        .initializeRepastSagentForCabsf(nativeRepastScontext);
            } catch (final CabsfInitializationRuntimeException e) {
                throw new CabsfInitializationRuntimeException(
                        "Cabsf initialization error in agent: " + this.getClass()
                        + " hash: " + this.hashCode(), e);
            }
        }
        // ////////////////////////////////

        // ////////////////////////////////

        final Iterable<Player> players = RunState.getInstance().getMasterContext()
                .getAgentLayer(Player.class);
        assert (players != null);

        standaloneParentPlayerSet = new HashSet<Player>();
        while (players.iterator().hasNext()) {
            final Player player = players.iterator().next();
            standaloneParentPlayerSet.add(player);
        }

        playerPairings = createInitialPlayerPairings(standaloneParentPlayerSet);
        playerPairingsIterator = playerPairings.entrySet().iterator();

        gameInitialized = true;

        System.out.println("[Game Administrator] Number of Player pairings: "
                + String.valueOf(playerPairings.size()));

    }

    /**
     * Initialize the current pairing of Players.
     *
     * @return value not used
     */
    private boolean initializePairing() {
        System.out.println("In initializePairing");
        final boolean newPairingAvailable = setupNewPairing();
        if (!newPairingAvailable) {
            playerA = null;
            playerB = null;
            previousRoundPlayerADecision = null;
            previousRoundPlayerBDecision = null;

            System.out.println("[Game Administrator] No more agent pairings to process ");
        } else {
            System.out.println("[Game Administrator] Finished agent pairing");
        }

        return newPairingAvailable;
    }

    /**
     * Prints the final scores across all players and rounds.
     */
    private void printFinalScores() {
        System.out.println("[Game Administrator] Printing Final Scores");
        for (final Player player : standaloneParentPlayerSet) {
            System.out.println("Player: " + String.valueOf(player.getPlayerNumber())
                    + " Score: " + player.getTotalScore());
        }

    }

    /**
     * Setup new pairing.
     *
     * @return true, if successful, false otherwise (all pairings have already
     *         executed ).
     */
    private boolean setupNewPairing() {
        System.out.println("In setupNewPairing");
        if (playerPairingsIterator.hasNext()) {
            final Map.Entry<Long, List<Player>> entry = playerPairingsIterator.next();
            System.out.println("[Game Administrator] Setting up new pairing.  "
                    + " (rounds start at 1, levels at 0) Pairing number: "
                    + String.valueOf(pairingNumber));
            pairingNumber++;

            round = 0;

            previousRoundPlayerADecision = null;
            previousRoundPlayerBDecision = null;

            previousPairingPlayerA = playerA;
            previousPairingPlayerB = playerB;

            final Player playerAparent = entry.getValue().get(0);
            final Player playerBparent = entry.getValue().get(1);

            assert (playerAparent != null && playerBparent != null);

            playerA = new PlayerA(this);
            playerA.setPlayerNumber(playerAparent.getPlayerNumber());
            playerA.setPlayerParent(playerAparent);
            playerB = new PlayerB(this);
            playerB.setPlayerNumber(playerBparent.getPlayerNumber());
            playerB.setPlayerParent(playerBparent);

            playerA.updateRunningScore(0, "PlayerA");
            playerB.updateRunningScore(0, "PlayerB");

            // Use PlayerA and PlayerB classes instead of Player to better
            // display the
            // results in the GUI (when not running with the CABSF)
            nativeRepastScontext.add(playerA);
            nativeRepastScontext.add(playerB);

            playerA.intializeGame(playerB);
            playerB.intializeGame(playerA);

            return true;
        } else {
            return false;
        }
    }

    /**
     * Step.
     */
    @ScheduledMethod(start = 1, interval = 1)
    public void step() {
        System.out.println("[Game Administrator] Step");

        if (!gameInitialized) {
            initializeOverallTournament();
            initializePairing();

            assert (playerPairings != null);
        }

        if (previousPairingPlayerA != null) {
            nativeRepastScontext.remove(previousPairingPlayerA);
        }
        if (previousPairingPlayerB != null) {
            nativeRepastScontext.remove(previousPairingPlayerB);
        }

        round += 1; // Start counting at round 1
        System.out.println("[Game Administrator] Starting Round: "
                + String.valueOf(round) + " Player A - Player "
                + String.valueOf(playerA.getPlayerNumber()) + " Player - Player "
                + String.valueOf(playerB.getPlayerNumber()));

        final DECISION decision0 = playerA.decide();
        final DECISION decision1 = playerB.decide();

        previousRoundPlayerADecision = decision0;
        previousRoundPlayerBDecision = decision1;

        int player0payoffThisRound = 0;
        int player1payoffThisRound = 0;

        // TODO: Could make the payoffs configurable.
        if (decision0 == DECISION.COOPERATE && decision1 == DECISION.COOPERATE) {
            player0payoffThisRound = 3;
            player1payoffThisRound = 3;
        }

        if (decision0 == DECISION.DEFECT && decision1 == DECISION.DEFECT) {
            player0payoffThisRound = 1;
            player1payoffThisRound = 1;
        }

        if (decision0 == DECISION.COOPERATE && decision1 == DECISION.DEFECT) {
            player0payoffThisRound = 0;
            player1payoffThisRound = 5;
        }

        if (decision0 == DECISION.DEFECT && decision1 == DECISION.COOPERATE) {
            player0payoffThisRound = 5;
            player1payoffThisRound = 0;
        }

        playerA.updateRunningScore(player0payoffThisRound, "Player A");
        playerB.updateRunningScore(player1payoffThisRound, "Player B");

        if (round == numberOfRounds) { // Finished the rounds for this one
            // pairing
            playerA.getPlayerParent().updateTotalScore(playerA.getRunningScore(),
                    "PlayerA");
            playerB.getPlayerParent().updateTotalScore(playerB.getRunningScore(),
                    "PlayerB");
            System.out.println("Finished rounds for this pairing.");
            System.out.println("*********************************");

            final boolean morePairingsAvailable = initializePairing();
            if (!morePairingsAvailable) {
                printFinalScores();
                return;
            }
        }

    }
}
