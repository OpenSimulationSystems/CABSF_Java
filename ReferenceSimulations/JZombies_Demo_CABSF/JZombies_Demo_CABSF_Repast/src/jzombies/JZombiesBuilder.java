package jzombies;

import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;

public class JZombiesBuilder implements ContextBuilder<Object> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see repast.simphony.dataLoader.ContextBuilder#build(repast.simphony.context
	 * .Context)
	 */
	@Override
	@SuppressWarnings("rawtypes")
	// @Override
	public Context build(final Context<Object> context) {
		final double MAX_ITERATIONS = 300;
		final RunEnvironment runEnvironment = RunEnvironment.getInstance();
		runEnvironment.endAt(MAX_ITERATIONS);
		context.setId("jzombies");

		final NetworkBuilder<Object> netBuilder = new NetworkBuilder<Object>(
				"infection network", context, true);
		netBuilder.buildNetwork();

		final ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder
				.createContinuousSpaceFactory(null);
		final ContinuousSpace<Object> space = spaceFactory.createContinuousSpace("space",
				context, new RandomCartesianAdder<Object>(),
				new repast.simphony.space.continuous.WrapAroundBorders(), 50, 50);

		final GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		final Grid<Object> grid = gridFactory.createGrid("grid", context,
				new GridBuilderParameters<Object>(new WrapAroundBorders(),
						new SimpleGridAdder<Object>(), true, 50, 50));

		final Parameters params = RunEnvironment.getInstance().getParameters();
		final int zombieCount = (Integer) params.getValue("zombie_count");
		for (int i = 0; i < zombieCount; i++) {
			context.add(new Zombie(space, grid));
		}

		final int humanCount = (Integer) params.getValue("human_count");
		for (int i = 0; i < humanCount; i++) {
			final int energy = RandomHelper.nextIntFromTo(4, 10);
			context.add(new Human(space, grid, energy));
		}

		for (final Object obj : context) {
			final NdPoint pt = space.getLocation(obj);
			grid.moveTo(obj, (int) pt.getX(), (int) pt.getY());
		}

		/*
		 * if (RunEnvironment.getInstance().isBatch()) {
		 * RunEnvironment.getInstance().endAt(20); }
		 */

		return context;
	}
}
