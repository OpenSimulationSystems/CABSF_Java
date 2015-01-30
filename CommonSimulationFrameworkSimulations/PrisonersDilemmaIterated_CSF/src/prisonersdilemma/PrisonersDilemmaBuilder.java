package prisonersdilemma;

import java.math.BigInteger;

import org.codehaus.groovy.runtime.typehandling.LongMath;
import org.simulationsystems.csf.common.csfmodel.csfexceptions.CsfRuntimeException;

import com.google.common.math.BigIntegerMath;

import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;

public class PrisonersDilemmaBuilder implements ContextBuilder<Object> {

	@SuppressWarnings("rawtypes")
	public Context build(Context<Object> context) {
		System.out.println("Running builder");
		context.setId("prisonersdilemma");

		GameAdministrator gameAdministrator = new GameAdministrator();
		context.add(gameAdministrator);

		// int playerCount = 2;

		Parameters params = RunEnvironment.getInstance().getParameters();
		int playerCount = (Integer) params.getValue("player_count");
		if (playerCount % 2 != 0)
			throw new CsfRuntimeException(
					"The CSF only supports an even number of players for now.");
		int numberOfRounds = (Integer) params.getValue("number_of_rounds");
		
		assert (playerCount % 2 == 0); // Only even number supported
		
		for (int i = 0; i < playerCount; i++) {
			/*
			 * Player player0 = new PlayerA(gameAdministrator);
			 * player0.setPlayerNumber(0); context.add(player0);
			 * 
			 * Player player1 = new PlayerB(gameAdministrator);
			 * player1.setPlayerNumber(1); context.add(player1);
			 */

			Player player = new Player(gameAdministrator);
			player.setPlayerNumber(i);
			context.add(player);
		}

		/*
		 * if (RunEnvironment.getInstance().isBatch()) {
		 * RunEnvironment.getInstance().endAt(10); }
		 */
		
		//Calculate the number of combinations
		BigInteger nFact = BigIntegerMath.factorial(playerCount);
		BigInteger nMinusRfact = BigIntegerMath.factorial(playerCount-2);
		BigInteger rFact = BigIntegerMath.factorial(2);
		Number denominator = LongMath.multiply(nMinusRfact, rFact);
		Number numCombinations = LongMath.divide(nFact, denominator);
		int numbCombinationsInt = numCombinations.intValue();
		
		RunEnvironment.getInstance().endAt(numbCombinationsInt*numberOfRounds);

		return context;
	}
}
