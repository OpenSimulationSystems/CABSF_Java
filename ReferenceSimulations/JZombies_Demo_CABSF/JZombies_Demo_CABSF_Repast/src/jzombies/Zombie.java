/**
 *
 */
package jzombies;

import java.util.ArrayList;
import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.graph.Network;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.SimUtilities;

/**
 * @author nick
 *
 */
public class Zombie {

	private final ContinuousSpace<Object> space;
	private final Grid<Object> grid;
	private boolean moved;

	public Zombie(final ContinuousSpace<Object> space, final Grid<Object> grid) {
		this.space = space;
		this.grid = grid;
	}

	public void infect() {
		final GridPoint pt = grid.getLocation(this);
		final List<Object> humans = new ArrayList<Object>();
		for (final Object obj : grid.getObjectsAt(pt.getX(), pt.getY())) {
			if (obj instanceof Human) {
				humans.add(obj);
			}
		}

		if (humans.size() > 0) {
			final int index = RandomHelper.nextIntFromTo(0, humans.size() - 1);
			final Object obj = humans.get(index);
			final NdPoint spacePt = space.getLocation(obj);
			final Context<Object> context = ContextUtils.getContext(obj);
			context.remove(obj);
			final Zombie zombie = new Zombie(space, grid);
			context.add(zombie);
			space.moveTo(zombie, spacePt.getX(), spacePt.getY());
			grid.moveTo(zombie, pt.getX(), pt.getY());

			final Network<Object> net = (Network<Object>) context
					.getProjection("infection network");
			net.addEdge(this, zombie);
		}
	}

	public void moveTowards(final GridPoint pt) {
		// only move if we are not already in this grid location
		if (!pt.equals(grid.getLocation(this))) {
			NdPoint myPoint = space.getLocation(this);
			final NdPoint otherPoint = new NdPoint(pt.getX(), pt.getY());
			final double angle = SpatialMath.calcAngleFor2DMovement(space,
					myPoint, otherPoint);
			space.moveByVector(this, 1, angle, 0);
			myPoint = space.getLocation(this);
			grid.moveTo(this, (int) myPoint.getX(), (int) myPoint.getY());
			moved = true;
		}
	}

	@ScheduledMethod(start = 1, interval = 1)
	public void step() {
		// get the grid location of this Zombie
		final GridPoint pt = grid.getLocation(this);

		// use the GridCellNgh class to create GridCells for
		// the surrounding neighborhood.
		final GridCellNgh<Human> nghCreator = new GridCellNgh<Human>(grid, pt,
				Human.class, 1, 1);
		final List<GridCell<Human>> gridCells = nghCreator
				.getNeighborhood(true);
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
		GridPoint pointWithMostHumans = null;
		int maxCount = -1;
		for (final GridCell<Human> cell : gridCells) {
			if (cell.size() > maxCount) {
				pointWithMostHumans = cell.getPoint();
				maxCount = cell.size();
			}
		}
		// Search the global environment
		/*
		 * if (maxCount <=0) {
		 * System.out.println("No more Humans.  Ending Simulation");
		 * RunEnvironment.getInstance().endRun(); }
		 */

		moveTowards(pointWithMostHumans);
		infect();
	}
}
