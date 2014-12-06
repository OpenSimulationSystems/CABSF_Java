/**
 * 
 */
package jzombies;

import java.util.List;

import org.simulationsystems.csf.sim.adapters.api.repastS.RepastS_SimulationRunContext;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunState;
import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.SimUtilities;

/**
 * @author nick
 *
 */
public class Human {
	
	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	private int energy, startingEnergy;

	public Human(ContinuousSpace<Object> space, Grid<Object> grid, int energy) {
		this.space = space;
		this.grid = grid;
		this.energy = startingEnergy = energy;
	}
	
	@Watch(watcheeClassName = "jzombies.Zombie", watcheeFieldNames = "moved", 
			query = "within_vn 1", whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void run() {
		@SuppressWarnings("unchecked")
		Context<Object> repastContext = RunState.getInstance()
				.getMasterContext();
		Iterable<Class> simulationAgentsClasses = repastContext.getAgentTypes();
		//for (Class clazz : simulationAgentsClasses )
		Iterable<Object> csfRepastContextIterable = RunState.getInstance()
		.getMasterContext().getAgentLayer(RepastS_SimulationRunContext.class);
		RepastS_SimulationRunContext repastS_SimulationRunContext = (RepastS_SimulationRunContext) csfRepastContextIterable.iterator().next();

		// get the grid location of this Human
		GridPoint pt = grid.getLocation(this);
		// use the GridCellNgh class to create GridCells for
		// the surrounding neighborhood.
		GridCellNgh<Zombie> nghCreator = new GridCellNgh<Zombie>(grid, pt,
				Zombie.class, 1, 1);
		List<GridCell<Zombie>> gridCells = nghCreator.getNeighborhood(true);
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform());

		GridPoint pointWithLeastZombies = null;
		int minCount = Integer.MAX_VALUE;
		for (GridCell<Zombie> cell : gridCells) {
			if (cell.size() < minCount) {
				pointWithLeastZombies = cell.getPoint();
				minCount = cell.size();
			}
		}
		//We now have the environment information needed to the distributed agent
		//pt
		//pointWithLeastZombies
		
		if (energy > 0) {
			moveTowards(pointWithLeastZombies);
		} else {
			energy = startingEnergy;
		}
	}
	
	public void moveTowards(GridPoint pt) {
		// only move if we are not already in this grid location
		if (!pt.equals(grid.getLocation(this))) {
			NdPoint myPoint = space.getLocation(this);
			NdPoint otherPoint = new NdPoint(pt.getX(), pt.getY());
			double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint);
			space.moveByVector(this, 2, angle, 0);
			myPoint = space.getLocation(this);
			grid.moveTo(this, (int)myPoint.getX(), (int)myPoint.getY());
			//energy--;
		}
	}

}
