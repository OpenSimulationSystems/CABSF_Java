/**
 * 
 */
package jzombies;

import java.io.IOException;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.simulationsystems.csf.common.csfmodel.SIMULATION_TYPE;
import org.simulationsystems.csf.common.csfmodel.SYSTEM_TYPE;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FRAMEWORK_COMMAND;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.internal.messaging.xml.XMLUtilities;
import org.simulationsystems.csf.sim.engines.adapters.repastS.api.RepastS_AgentAdapterAPI;
import org.simulationsystems.csf.sim.engines.adapters.repastS.api.RepastS_AgentContext;
import org.simulationsystems.csf.sim.engines.adapters.repastS.api.RepastS_SimulationRunContext;

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

	// /////////////////
	// CSF-Specific
	private RepastS_AgentContext repastS_AgentContext = RepastS_AgentAdapterAPI
			.getInstance().getAgentContext();
	private SIMULATION_TYPE simulationType;
	private JZombies_Csf jZombies_Csf;

	// /////////////////

	public Human(ContinuousSpace<Object> space, Grid<Object> grid, int energy) {
		this.space = space;
		this.grid = grid;
		this.energy = startingEnergy = energy;
	}

	@Watch(watcheeClassName = "jzombies.Zombie", watcheeFieldNames = "moved", query = "within_vn 1", whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void run() {
		// /////////////////
		// CSF-Specific
		// FIXME: Make this transparent (do this from the Adapter so the agent doesn't
		// have to
		try {
			if (jZombies_Csf == null)
				jZombies_Csf = new JZombies_Csf(repastS_AgentContext);
			
			simulationType = repastS_AgentContext.initializeCsfAgent();
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// /////////////////

		// ////////////////////////////////
		// Common Repast Code for all all agent instances
		// get the local environment inforamtion (this agent location and nearby Zombies
		GridPoint pt = grid.getLocation(this);
		// use the GridCellNgh class to create GridCells for
		// the surrounding neighborhood.
		GridCellNgh<Zombie> nghCreator = new GridCellNgh<Zombie>(grid, pt, Zombie.class,
				1, 1);
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
		// /////////////////////////////////////////////

		// //////////////////////////
		// CSF-specific
		// If it is part of a CSF simulation, move the decision-making to the distributed
		// agent/model
		if (simulationType == SIMULATION_TYPE.CSF_SIMULATION) {
			// Communicate the local environment information for this agent to the
			// distributed agent (agent model)
			// LOW: Add support for merging multiple messages bound for different agents
			jZombies_Csf.sendMessageToDistributedAutonomousAgentModelFromSimulationAgent(
					this, pt, pointWithLeastZombies);
			// FIXME: Move to simultaneous processing of these messages?
			FrameworkMessage msg = repastS_AgentContext.getRepastS_SimulationRunContext()
					.readFrameworkMessageFromDistributedSystem();

			System.out.println("[Human "
					+ repastS_AgentContext.hashCode()
					+ "] Received distributed decision: "
					+ XMLUtilities.convertDocumentToXMLString(msg.getDocument()
							.getRootElement(), true));

			List<String> selfPoint = msg.getSelfLocation(msg);
			for (int i = 0; i < selfPoint.size(); i++) {
				System.out.println("[Human " + repastS_AgentContext.hashCode()
						+ "] Self Location:" + String.valueOf(i) + " : "
						+ String.valueOf(selfPoint.get(i)));
			}

			System.exit(0);
		}
		// ////////////////////////////////////////////

		// /////////////////////////////////////////////
		// Back to common code for Repast
		if (energy > 0) {
			moveTowards(pointWithLeastZombies);
		} else {
			energy = startingEnergy;
		}
		// /////////////////////////////////////////////
	}

	public void moveTowards(GridPoint pt) {
		// only move if we are not already in this grid location
		if (!pt.equals(grid.getLocation(this))) {
			NdPoint myPoint = space.getLocation(this);
			NdPoint otherPoint = new NdPoint(pt.getX(), pt.getY());
			double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint);
			space.moveByVector(this, 2, angle, 0);
			myPoint = space.getLocation(this);
			grid.moveTo(this, (int) myPoint.getX(), (int) myPoint.getY());
			// energy--;
		}
	}

}
