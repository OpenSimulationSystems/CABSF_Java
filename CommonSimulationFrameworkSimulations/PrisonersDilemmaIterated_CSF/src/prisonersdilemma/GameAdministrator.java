package prisonersdilemma;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jdom2.JDOMException;
import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;
import org.simulationsystems.csf.common.csfmodel.SIMULATION_TYPE;
import org.simulationsystems.csf.common.csfmodel.csfexceptions.CsfInitializationRuntimeException;
import org.simulationsystems.csf.sim.adapters.simengines.repastS.api.RepastS_AgentAdapterAPI;
import org.simulationsystems.csf.sim.adapters.simengines.repastS.api.RepastS_AgentContext;
import org.simulationsystems.csf.sim.adapters.simengines.repastS.api.RepastS_SimulationRunContext;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.environment.RunState;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;

public class GameAdministrator {
	int pairingNumber;
	boolean gameInitialized = false;
	int level = 0;
	private int round = 0;
	Parameters params = RunEnvironment.getInstance().getParameters();
	int numberOfRounds = (Integer) params.getValue("number_of_rounds");
	private Context<Object> repastScontext;
	private HashMap<Long, List<Player>> playerPairings;
	private Iterator<Entry<Long, List<Player>>> playerPairingsIterator;

	private DECISION lastPlayer0Decision;
	private DECISION lastPlayer1Decision;
	private PlayerA playerA;
	private PlayerB playerB;

	// /////////////////
	// CSF-Specific
	private RepastS_AgentContext repastS_AgentContext = RepastS_AgentAdapterAPI
			.getInstance().getAgentContext();
	private SIMULATION_TYPE simulationType;
	private PrisonersDilemma_CSF prisonersDilemma_CSF;
	private HashSet<Player> playerSet;
	private PlayerA lastplayerA;
	private PlayerB lastplayerB;

	public RepastS_AgentContext getRepastS_AgentContext() {
		return repastS_AgentContext;
	}

	public PrisonersDilemma_CSF getPrisonersDilemma_CSF() {
		return prisonersDilemma_CSF;
	}

	// /////////////////

	public SIMULATION_TYPE getSimulationType() {
		return simulationType;
	}

	public Context<Object> getRepastScontext() {
		return repastScontext;
	}

	public DECISION getLastPlayer0Decision() {
		return lastPlayer0Decision;
	}

	public DECISION getLastPlayer1Decision() {
		return lastPlayer1Decision;
	}

	public int getRound() {
		return round;
	}

	public void initializeOverallTournament() {
		System.out.println("[Game Administrator] Initializing");

		// /////////////////
		// CSF-Specific
		// FIXME: Make this transparent (do this from the Adapter so the agent doesn't
		// have to
		try {
			if (prisonersDilemma_CSF == null) {
				repastScontext = RunState.getInstance().getMasterContext();
				prisonersDilemma_CSF = new PrisonersDilemma_CSF(repastS_AgentContext);

				Iterable<Class> simulationAgentsClasses = RunState.getInstance()
						.getMasterContext().getAgentTypes();
				Iterable<Object> csfRepastContextIterable = RunState.getInstance()
						.getMasterContext()
						.getAgentLayer(RepastS_SimulationRunContext.class);
				simulationType = repastS_AgentContext.initializeCsfAgent(
						simulationAgentsClasses, csfRepastContextIterable);
			}
		} catch (JDOMException e) {
			throw new CsfInitializationRuntimeException(
					"Failed to initialize the Common Simulation Framework in the Repast simulation agent",
					e);
		} catch (IOException e) {
			throw new CsfInitializationRuntimeException(
					"Failed to initialize the Common Simulation Framework in the Repast simulation agent",
					e);
		}

		Iterable<Player> players = RunState.getInstance().getMasterContext()
				.getAgentLayer(Player.class);
		assert (players != null);

		playerSet = new HashSet<Player>();
		while (players.iterator().hasNext()) {
			Player player = players.iterator().next();
			playerSet.add(player);
		}
		
		playerPairings = createInitialPairings(playerSet);
		playerPairingsIterator = playerPairings.entrySet().iterator();

		gameInitialized = true;

	}

	private HashMap<Long, List<Player>> createInitialPairings(Set<Player> playersSet) {
		playerPairings = new HashMap<Long, List<Player>>();

		Object[] playerArray = (Object[]) playersSet.toArray();
		/*
		 * for (int pairingNumber = 0; pairingNumber < playerArray.length; pairingNumber += 2) { //playerPairings.put((Player)
		 * playerArray[pairingNumber], (Player) playerArray[pairingNumber + 1]); }
		 */
		// Create the initial vector
		ICombinatoricsVector<Object> initialVector = Factory.createVector(playerArray);

		// Create a simple combination generator to generate 3-combinations of the initial
		// vector
		Generator<Object> gen = Factory
				.createSimpleCombinationGenerator(initialVector, 2);

		// Print all possible combinations
		long i = 0;
		for (ICombinatoricsVector<Object> combination : gen) {
			System.out.println(combination);

			Player playeraTemp = null;
			Player playerbTemp = null;

			List<Object> cominationLst = combination.getVector();
			playeraTemp = (Player) cominationLst.get(0);
			playerbTemp = (Player) cominationLst.get(1);

			assert (playeraTemp != null && playerbTemp != null && playeraTemp != playerbTemp);

			List<Player> newList = new ArrayList<Player>();
			newList.add(playeraTemp);
			newList.add(playerbTemp);
			playerPairings.put(i, newList);
			i++;
		}

		return playerPairings;
	}

	/*
	 * Return true for new pairing available. False for last pairing has executed.
	 */
	private boolean setupNewPairing() {
		System.out.println("In setupNewPairing");
		if (playerPairingsIterator.hasNext()) {
			Map.Entry<Long, List<Player>> entry = playerPairingsIterator.next();
			System.out.println("[Game Administrator] Setting up new pairing. At level: "
					+ String.valueOf(level)
					+ " (rounds start at 1, levels at 0) Pairing number: "
					+ String.valueOf(pairingNumber));
			pairingNumber++;

			round = 0;

			lastPlayer0Decision = null;
			lastPlayer1Decision = null;

			lastplayerA = playerA;
			lastplayerB = playerB;

			Player playerAparent = entry.getValue().get(0);
			Player playerBparent = entry.getValue().get(1);

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

	private boolean initializePairing() {
		System.out.println("In initializePairing");
		boolean thisLevelFinished = setupNewPairing();
		if (thisLevelFinished) {
			level += 1;
			// repastScontext.remove(playerA);
			// repastScontext.remove(playerB);
			playerA = null;
			playerB = null;
			lastPlayer0Decision = null;
			lastPlayer1Decision = null;

			System.out.println("[Game Administrator] Finished this level");
			// System.exit(0); // Next: set up new levels
		}

		return thisLevelFinished;
	}

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

		DECISION decision0 = playerA.decide();
		DECISION decision1 = playerB.decide();

		lastPlayer0Decision = decision0;
		lastPlayer1Decision = decision1;

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

			boolean finishedLevel = initializePairing();
			if (finishedLevel) {
				printFinalScores();
				return;
			}
		}

	}

	private void printFinalScores() {
		System.out.println("Printing Final Scores");
		for (Player player : playerSet) {
			System.out.println("Player: " + String.valueOf(player.getPlayerNumber())
					+ " Score: " + player.getTotalScore());
		}

	}

}
