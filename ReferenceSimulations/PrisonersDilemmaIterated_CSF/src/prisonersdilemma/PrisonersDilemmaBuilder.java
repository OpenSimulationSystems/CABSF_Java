package prisonersdilemma;

import java.math.BigInteger;

import org.codehaus.groovy.runtime.typehandling.LongMath;
import org.simulationsystems.csf.common.csfmodel.csfexceptions.CsfRuntimeException;

import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;

import com.google.common.math.BigIntegerMath;

// TODO: Auto-generated Javadoc
/**
 * The builder class for the RepastS simulation
 * 
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class PrisonersDilemmaBuilder implements ContextBuilder<Object> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * repast.simphony.dataLoader.ContextBuilder#build(repast.simphony.context.Context)
	 */
	@SuppressWarnings("rawtypes")
	public Context build(final Context<Object> context) {
		System.out.println("Running builder");
		context.setId("prisonersdilemma");

		final GameAdministrator gameAdministrator = new GameAdministrator();
		context.add(gameAdministrator);

		final Parameters params = RunEnvironment.getInstance().getParameters();
		final int playerCount = (Integer) params.getValue("player_count");
		if (playerCount % 2 != 0)
			throw new CsfRuntimeException(
					"The CSF only supports an even number of players for now.");
		final int numberOfRounds = (Integer) params.getValue("number_of_rounds");

		assert (playerCount % 2 == 0); // Only even numbers supported

		for (int i = 0; i < playerCount; i++) {
			final Player player = new Player(gameAdministrator);
			player.setPlayerNumber(i);
			context.add(player);
		}

		// Calculate the number of combinations
		final BigInteger nFact = BigIntegerMath.factorial(playerCount);
		final BigInteger nMinusRfact = BigIntegerMath.factorial(playerCount - 2);
		final BigInteger rFact = BigIntegerMath.factorial(2);
		final Number denominator = LongMath.multiply(nMinusRfact, rFact);
		final Number numCombinations = LongMath.divide(nFact, denominator);
		final int numbCombinationsInt = numCombinations.intValue();

		RunEnvironment.getInstance().endAt(numbCombinationsInt * numberOfRounds);

		return context;
	}
}
