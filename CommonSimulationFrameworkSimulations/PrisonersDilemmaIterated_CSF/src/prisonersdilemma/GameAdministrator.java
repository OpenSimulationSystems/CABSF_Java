package prisonersdilemma;

import repast.simphony.engine.environment.RunState;
import repast.simphony.engine.schedule.ScheduledMethod;

public class GameAdministrator {
	boolean gameInitialized = false;
	Iterable<Player> players;

	Player player0;
	Player player1;

	public void initializeGame() {
		System.out.println("Initializing");

		players = RunState.getInstance().getMasterContext().getAgentLayer(Player.class);

		player0 = players.iterator().next();
		player1 = players.iterator().next();

		player0.intializeGame(player1);
		player1.intializeGame(player0);
		
		gameInitialized= true;

	}

	@ScheduledMethod(start = 1, interval = 1)
	public void step() {
		System.out.println("[Game Administrator] Step");
		if (!gameInitialized) {
			initializeGame();
		}

		DECISION decision0 = player0.decide();
		DECISION decision1 = player1.decide();

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
