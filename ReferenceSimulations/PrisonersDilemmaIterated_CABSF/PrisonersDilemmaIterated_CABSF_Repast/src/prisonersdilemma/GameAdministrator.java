package prisonersdilemma;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jdom2.JDOMException;
import org.opensimulationsystems.cabsf.common.model.SIMULATION_TYPE;
import org.opensimulationsystems.cabsf.common.model.cabsfexceptions.CabsfInitializationRuntimeException;
import org.opensimulationsystems.cabsf.sim.adapters.simengines.repastS.api.RepastS_AgentAdapterAPI;
import org.opensimulationsystems.cabsf.sim.adapters.simengines.repastS.api.RepastS_AgentContext;
import org.opensimulationsystems.cabsf.sim.adapters.simengines.repastS.api.RepastS_SimulationRunContext;
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
 * The GameAdministrator is the only class in this simulation with a scheduled RepastS
 * step function. It creates the initial set of Player pairings, calls each Player object
 * to make a COOPERATE or DEFECT decision, and keeps track of scores. Only one player
 * pairing is active at any given time.
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

	/** Not used in the simulation */
	int level = 0;

	/** The round. */
	private int round = 0;

	/** The simulation parameters in the simulation's .rs directory . */
	Parameters params = RunEnvironment.getInstance().getParameters();

	/** The total number of rounds per pairing. */
	int numberOfRounds = (Integer) params.getValue("number_of_rounds");

	/** The repast scontext. */
	private Context<Object> repastScontext;

	/** The player pairings. */
	private HashMap<Long, List<Player>> playerPairings;

	/** The player pairings iterator. */
	private Iterator<Entry<Long, List<Player>>> playerPairingsIterator;

	/** The last player0 decision. */
	private DECISION lastPlayerADecision;

	/** The last player1 decision. */
	private DECISION lastPlayerBDecision;

	/** The player A in the current pairing */
	private PlayerA playerA;

	/** The player G in the current pairing. */
	private PlayerB playerB;

	/** The RepastS_AgentContext. */
	private final RepastS_AgentContext repastS_AgentContext = RepastS_AgentAdapterAPI
			.getInstance().getAgentContext();

	/** The simulation type. */
	private SIMULATION_TYPE simulationType;

	/** The prisoners dilemma_ csf. */
	private PrisonersDilemma_CSF prisonersDilemma_CSF;

	/** The player set. */
	private HashSet<Player> playerSet;

	/** The lastplayer a. */
	private PlayerA lastplayerA;

	/** The lastplayer b. */
	private PlayerB lastplayerB;

	/**
	 * Creates the initial set of pairings between Player objects. These Player objects
	 * are not the PlayerA and PlayerB child class objects created later. The number of
	 * players is calculated by calculating the number of non-repeating combinations.
	 * 
	 * @param playersSet
	 *            the players set
	 * @return the hash map
	 */
	private HashMap<Long, List<Player>> createInitialPairings(final Set<Player> playersSet) {
		playerPairings = new HashMap<Long, List<Player>>();

		final Object[] playerArray = playersSet.toArray();
		// Create the initial vector
		final ICombinatoricsVector<Object> initialVector = Factory
				.createVector(playerArray);

		// Create a simple combination generator to generate 3-combinations of the initial
		// vector
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
	public DECISION getLastPlayerADecision() {
		return lastPlayerADecision;
	}

	/**
	 * Gets the last playerB decision.
	 * 
	 * @return the last playerB decision
	 */
	public DECISION getLastPlayerBDecision() {
		return lastPlayerBDecision;
	}

	/**
	 * Gets the PrisonersDilemma_CSF.
	 * 
	 * @return the PrisonersDilemma_CSF convenience class
	 */
	public PrisonersDilemma_CSF getPrisonersDilemma_CSF() {
		return prisonersDilemma_CSF;
	}

	/**
	 * Gets the RepastS_AgentContext.
	 * 
	 * @return the RepastS_AgentContext
	 */
	public RepastS_AgentContext getRepastS_AgentContext() {
		return repastS_AgentContext;
	}

	/**
	 * Gets the repast Context.
	 * 
	 * @return the repast Context
	 */
	public Context<Object> getRepastScontext() {
		return repastScontext;
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
	public SIMULATION_TYPE getSimulationType() {
		return simulationType;
	}

	/**
	 * Initialize overall tournament.
	 */
	public void initializeOverallTournament() {
		System.out.println("[Game Administrator] Initializing");

		// FIXME: Simplify the API
		try {
			if (prisonersDilemma_CSF == null) {
				repastScontext = RunState.getInstance().getMasterContext();
				prisonersDilemma_CSF = new PrisonersDilemma_CSF(repastS_AgentContext);

				final Iterable<Class> simulationAgentsClasses = RunState.getInstance()
						.getMasterContext().getAgentTypes();
				final Iterable<Object> csfRepastContextIterable = RunState.getInstance()
						.getMasterContext()
						.getAgentLayer(RepastS_SimulationRunContext.class);
				simulationType = repastS_AgentContext.initializeCabsfAgent(
						simulationAgentsClasses, csfRepastContextIterable);
			}
		} catch (final JDOMException e) {
			throw new CabsfInitializationRuntimeException(
					"Failed to initialize the Common Simulation Framework in the Repast simulation agent",
					e);
		} catch (final IOException e) {
			throw new CabsfInitializationRuntimeException(
					"Failed to initialize the Common Simulation Framework in the Repast simulation agent",
					e);
		}

		final Iterable<Player> players = RunState.getInstance().getMasterContext()
				.getAgentLayer(Player.class);
		assert (players != null);

		playerSet = new HashSet<Player>();
		while (players.iterator().hasNext()) {
			final Player player = players.iterator().next();
			playerSet.add(player);
		}

		playerPairings = createInitialPairings(playerSet);
		playerPairingsIterator = playerPairings.entrySet().iterator();

		gameInitialized = true;

	}

	/**
	 * Initialize the current pairi8ng of Players.
	 * 
	 * @return value not used
	 */
	private boolean initializePairing() {
		System.out.println("In initializePairing");
		final boolean thisLevelFinished = setupNewPairing();
		if (thisLevelFinished) {
			level += 1;
			// repastScontext.remove(playerA);
			// repastScontext.remove(playerB);
			playerA = null;
			playerB = null;
			lastPlayerADecision = null;
			lastPlayerBDecision = null;

			System.out.println("[Game Administrator] Finished this level");
			// System.exit(0); // Next: set up new levels
		}

		// FIXME: Remove this unneeded information from the simulation
		return thisLevelFinished;
	}

	/**
	 * Prints the final scores across all players and rounds.
	 */
	private void printFinalScores() {
		System.out.println("Printing Final Scores");
		for (final Player player : playerSet) {
			System.out.println("Player: " + String.valueOf(player.getPlayerNumber())
					+ " Score: " + player.getTotalScore());
		}

	}

	/*
	 * Return true for new pairing available. False for last pairing has executed.
	 */
	/**
	 * Setup new pairing.
	 * 
	 * @return true, if successful
	 */
	private boolean setupNewPairing() {
		System.out.println("In setupNewPairing");
		if (playerPairingsIterator.hasNext()) {
			final Map.Entry<Long, List<Player>> entry = playerPairingsIterator.next();
			System.out.println("[Game Administrator] Setting up new pairing. At level: "
					+ String.valueOf(level)
					+ " (rounds start at 1, levels at 0) Pairing number: "
					+ String.valueOf(pairingNumber));
			pairingNumber++;

			round = 0;

			lastPlayerADecision = null;
			lastPlayerBDecision = null;

			lastplayerA = playerA;
			lastplayerB = playerB;

			final Player playerAparent = entry.getValue().get(0);
			final Player playerBparent = entry.getValue().get(1);

			assert (playerAparent != null && playerBparent != null);

			/*
			 * playerA = (PlayerA) player0parent; playerB = (PlayerB) player1parent;
			 */

			playerA = new PlayerA(this);
			playerA.setPlayerNumber(playerAparent.getPlayerNumber());
			playerA.setPlayerParent(playerAparent);
			playerB = new PlayerB(this);
			playerB.setPlayerNumber(playerBparent.getPlayerNumber());
			playerB.setPlayerParent(playerBparent);

			playerA.updateRunningScore(0, "PlayerA");
			playerB.updateRunningScore(0, "PlayerB");

			// Use PlayerA and PlayerB classes instead of Player to better display the
			// results in the GUI (when not running with the CSF)
			repastScontext.add(playerA);
			repastScontext.add(playerB);

			playerA.intializeGame(playerB);
			playerB.intializeGame(playerA);

			return false;
		} else {
			return true;
		}
	}

	/**
	 * Step.
	 */
	@ScheduledMethod(start = 1, interval = 1)
	public void step() {
		if (playerPairings != null)
			System.out.println(String.valueOf(playerPairings.size()));

		System.out.println("[Game Administrator] Step");
		if (!gameInitialized) {
			initializeOverallTournament();
			initializePairing();
		}

		if (lastplayerA != null)
			repastScontext.remove(lastplayerA);
		if (lastplayerB != null)
			repastScontext.remove(lastplayerB);

		round += 1; // Start counting at round 1 to match the tick numbers
		System.out.println("[Game Administrator] Starting Round: "
				+ String.valueOf(round) + " of level: " + String.valueOf(level));

		final DECISION decision0 = playerA.decide();
		final DECISION decision1 = playerB.decide();

		lastPlayerADecision = decision0;
		lastPlayerBDecision = decision1;

		int player0payoffThisRound = 0;
		int player1payoffThisRound = 0;

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

		if (round == numberOfRounds) { // Finished the rounds for this one pairing
			playerA.getPlayerParent().updateTotalScore(playerA.getRunningScore(),
					"PlayerA");
			playerB.getPlayerParent().updateTotalScore(playerB.getRunningScore(),
					"PlayerB");
			System.out.println("Finished rounds for this pairing.");
			System.out.println("*********************************");

			final boolean finishedLevel = initializePairing();
			if (finishedLevel) {
				printFinalScores();
				return;
			}
		}

	}

}
