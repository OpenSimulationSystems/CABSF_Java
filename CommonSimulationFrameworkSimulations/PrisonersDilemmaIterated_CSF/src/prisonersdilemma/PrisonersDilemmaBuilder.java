package prisonersdilemma;

import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;

public class PrisonersDilemmaBuilder implements ContextBuilder<Object> {

	@SuppressWarnings("rawtypes")
	public Context build(Context<Object> context) {
		System.out.println("Running builder");
		context.setId("prisonersdilemma");

		GameAdministrator gameAdministrator = new GameAdministrator();
		context.add(gameAdministrator);

		// int playerCount = 2;

		Player player0 = new Player0(gameAdministrator);
		player0.setPlayerNumber(0);
		context.add(player0);

		Player player1 = new Player1(gameAdministrator);
		player1.setPlayerNumber(1);
		context.add(player1);

		/*
		 * if (RunEnvironment.getInstance().isBatch()) {
		 * RunEnvironment.getInstance().endAt(10); }
		 */

		RunEnvironment.getInstance().endAt(10);

		return context;
	}
}
