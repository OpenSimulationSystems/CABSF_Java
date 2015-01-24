package prisonersdilemma;

import java.io.IOException;

import org.jdom2.JDOMException;
import org.simulationsystems.csf.common.csfmodel.SIMULATION_TYPE;
import org.simulationsystems.csf.common.csfmodel.csfexceptions.CsfInitializationRuntimeException;
import org.simulationsystems.csf.sim.engines.adapters.repastS.api.RepastS_AgentAdapterAPI;
import org.simulationsystems.csf.sim.engines.adapters.repastS.api.RepastS_AgentContext;
import org.simulationsystems.csf.sim.engines.adapters.repastS.api.RepastS_SimulationRunContext;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunState;
import repast.simphony.engine.schedule.ScheduledMethod;

public class GameAdministrator {
	private int round;
	boolean gameInitialized = false;
	private Iterable<Player> players;

	private Player player0;
	private Player player1;

	private DECISION lastPlayer0Decision;
	private DECISION lastPlayer1Decision;

	public DECISION getLastPlayer0Decision() {
		return lastPlayer0Decision;
	}

	public DECISION getLastPlayer1Decision() {
		return lastPlayer1Decision;
	}

	public int getRound() {
		return round;
	}

	// /////////////////
	// CSF-Specific
	private RepastS_AgentContext repastS_AgentContext = RepastS_AgentAdapterAPI
			.getInstance().getAgentContext();
	private SIMULATION_TYPE simulationType;
	private PrisonersDilemma_CSF prisonersDilemma_CSF;

	private Context<Object> repastScontext;

	public SIMULATION_TYPE getSimulationType() {
		return simulationType;
	}

	public Context<Object> getRepastScontext() {
		return repastScontext;
	}

	public RepastS_AgentContext getRepastS_AgentContext() {
		return repastS_AgentContext;
	}

	public PrisonersDilemma_CSF getPrisonersDilemma_CSF() {
		return prisonersDilemma_CSF;
	}

	// /////////////////

	public void initializeGame() {
		System.out.println("Initializing");

		players = RunState.getInstance().getMasterContext().getAgentLayer(Player.class);

		player0 = players.iterator().next();
		player1 = players.iterator().next();

		player0.intializeGame(player1);
		player1.intializeGame(player0);

		lastPlayer0Decision = null;
		lastPlayer1Decision = null;

		round = 0;

		///////////////////
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
		///////////////////

		gameInitialized = true;

	}

	@ScheduledMethod(start = 1, interval = 1)
	public void step() {
		System.out.println("[Game Administrator] Step");
		if (!gameInitialized) {
			initializeGame();
		}

		round += 1;
		System.out.println("Round: " + String.valueOf(round));

		DECISION decision0 = player0.decide();
		DECISION decision1 = player1.decide();

		lastPlayer0Decision = decision0;
		lastPlayer1Decision = decision1;

		int player0payoff = 0;
		int player1payoff = 0;

		if (decision0 == DECISION.COOPERATE && decision1 == DECISION.COOPERATE) {
			player0payoff = 3;
			player1payoff = 3;
		}

		if (decision0 == DECISION.DEFECT && decision1 == DECISION.DEFECT) {
			player0payoff = 1;
			player1payoff = 1;
		}

		if (decision0 == DECISION.COOPERATE && decision1 == DECISION.DEFECT) {
			player0payoff = 0;
			player1payoff = 5;
		}

		if (decision0 == DECISION.DEFECT && decision1 == DECISION.COOPERATE) {
			player0payoff = 5;
			player1payoff = 0;
		}

		player0.updateScore(player0payoff);
		player1.updateScore(player1payoff);

	}
}
