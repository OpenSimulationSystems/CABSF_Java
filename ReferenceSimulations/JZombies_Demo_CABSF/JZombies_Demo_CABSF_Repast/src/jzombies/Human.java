/**
 *
 */
package jzombies;

import java.io.IOException;
import java.util.List;

import org.jdom2.JDOMException;
import org.opensimulationsystems.cabsf.common.internal.messaging.xml.XMLUtilities;
import org.opensimulationsystems.cabsf.common.model.AgentMapping;
import org.opensimulationsystems.cabsf.common.model.SIMULATION_TYPE;
import org.opensimulationsystems.cabsf.common.model.cabsfexceptions.CabsfInitializationRuntimeException;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FrameworkMessage;
import org.opensimulationsystems.cabsf.sim.adapters.simengines.repastS.api.RepastS_AgentAdapterAPI;
import org.opensimulationsystems.cabsf.sim.adapters.simengines.repastS.api.RepastS_AgentContext;
import org.opensimulationsystems.cabsf.sim.adapters.simengines.repastS.api.RepastS_SimulationRunContext;
import org.opensimulationsystems.cabsf.sim.core.api.distributedsystems.SimulationDistributedSystemManager;

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

// TODO: Auto-generated Javadoc
/**
 * The Human in the JZombies simulation. We modified this Repast Simphony simulation from
 * the tutorial to integrated with the Common Simulation Framework (CSF). The humans are
 * distributed to a JADE MAS. This Human RepastS class is the representational agent
 * within RepastS of the distributed JADE Human agent.
 *
 * @author nick
 * @author Jorge Calderon (modified Human class for integrating with the CSF)
 */
public class Human {

	/** The space. */
	private final ContinuousSpace<Object> space;

	/** The grid. */
	private final Grid<Object> grid;

	/** The starting energy. */
	private int energy, startingEnergy;

	// /////////////////////////////
	// Section Added to the CSF version of this simulation
	/** The repast s_ agent context. */
	private final RepastS_AgentContext repastS_AgentContext = RepastS_AgentAdapterAPI
			.getInstance().getAgentContext();

	/** The simulation type. */
	private SIMULATION_TYPE simulationType;

	/** The simulation-specific convenience class */
	private JZombies_CABSF_Helper jZombies_CABSF_Helper;

	/** The repast context. */
	private Context<Object> repastContext;

	// /////////////////////////////

	/**
	 * Instantiates a new Human.
	 *
	 * @param space
	 *            the space
	 * @param grid
	 *            the grid
	 * @param energy
	 *            the energy
	 */
	public Human(final ContinuousSpace<Object> space, final Grid<Object> grid,
			final int energy) {
		this.space = space;
		this.grid = grid;
		this.energy = startingEnergy = energy;
	}

	/**
	 * Gets the RepastS_AgentContext context.
	 *
	 * @return the repast s_ agent context
	 */
	public RepastS_AgentContext getRepastS_AgentContext() {
		return repastS_AgentContext;
	}

	/**
	 * This class is only used when this simulation is run with the CSF functionality
	 * turned off. It moves the agent to a particular Gridpoint. By using this method when
	 * CSF is turned off, we can "plugin" in the move decision of this agent into the
	 * simulation so that this agent does not have to rely on a corresponding distributed
	 * JADE agent.
	 *
	 * @param pt
	 *            the GridPoint
	 */
	public void moveTowards(final GridPoint pt) {
		// only move if we are not already in this grid location
		if (!pt.equals(grid.getLocation(this))) {
			NdPoint myPoint = space.getLocation(this);
			final NdPoint otherPoint = new NdPoint(pt.getX(), pt.getY());
			final double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint,
					otherPoint);
			space.moveByVector(this, 2, angle, 0);
			myPoint = space.getLocation(this);
			grid.moveTo(this, (int) myPoint.getX(), (int) myPoint.getY());
			// energy--;
		}
	}

	/**
	 * The step method for the Human.
	 */
	@Watch(watcheeClassName = "jzombies.Zombie", watcheeFieldNames = "moved", query = "within_vn 1", whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void run() {
		// ////////////////////////////////
		// Section Added to the CSF version of this simulation
		// FIXME: Simply the API
		try {
			if (jZombies_CABSF_Helper == null) {
				repastContext = RunState.getInstance().getMasterContext();
				jZombies_CABSF_Helper = new JZombies_CABSF_Helper(repastS_AgentContext);

				final Iterable<Class> simulationAgentsClasses = RunState.getInstance()
						.getMasterContext().getAgentTypes();
				final Iterable<Object> csfRepastContextIterable = RunState.getInstance()
						.getMasterContext()
						.getAgentLayer(RepastS_SimulationRunContext.class);
				simulationType = repastS_AgentContext.initializeCabsfAgent(
						simulationAgentsClasses, csfRepastContextIterable);
			}
		} catch (final JDOMException e) {
			throw new CabsfInitializationRuntimeException(
					"Failed to initialize the Common Simulation Framework in the Repast simulation agent",
					e);
		} catch (final IOException e) {
			throw new CabsfInitializationRuntimeException(
					"Failed to initialize the Common Simulation Framework in the Repast simulation agent",
					e);
		}
		// ////////////////////////////////

		// ////////////////////////////////
		// Back to the original JZombies code
		final GridPoint pt = grid.getLocation(this);
		// use the GridCellNgh class to create GridCells for
		// the surrounding neighborhood.
		final GridCellNgh<Zombie> nghCreator = new GridCellNgh<Zombie>(grid, pt,
				Zombie.class, 1, 1);
		final List<GridCell<Zombie>> gridCells = nghCreator.getNeighborhood(true);
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform());

		GridPoint pointWithLeastZombies = null;
		int minCount = Integer.MAX_VALUE;
		for (final GridCell<Zombie> cell : gridCells) {
			if (cell.size() < minCount) {
				pointWithLeastZombies = cell.getPoint();
				minCount = cell.size();
			}
		}

		GridPoint moveToPoint = null;
		// /////////////////////////////////////////////

		// /////////////////////////////////////////////
		// Section Added to the CSF version of this simulation
		// If it is part of a CSF simulation, move the decision-making to the
		// distributed
		// FIXME: Support multiple distributed system managers/provide method
		// for getting
		// the appropriate one.
		if (simulationType == SIMULATION_TYPE.CABSF_SIMULATION) {
			final SimulationDistributedSystemManager dsm = repastS_AgentContext
					.getRepastS_SimulationRunContext()
					.getSimulationDistributedSystemManagers().iterator().next();
			final AgentMapping am = dsm.getAgentMappingForObject(this);

			final String distributedSystemID = am.getDistributedSystemID();
			final String distributedAutonomousAgentID = am
					.getDistributedAutonomousAgentID();
			final String distributedAutonomousAgentModelID = am
					.getDistributedAutonomousAgentModelID();
			final String loggingPrefix = "[Human " + distributedSystemID + " "
					+ distributedAutonomousAgentID + " "
					+ distributedAutonomousAgentModelID + "] ";

			// Communicate the local environment information for this agent to
			// the
			// distributed agent (agent model)
			// LOW: Add support for merging multiple messages bound for
			// different agents
			jZombies_CABSF_Helper.sendMessageToDistributedAutonomousAgentModelFromSimulationAgent(
					loggingPrefix, this, pt, pointWithLeastZombies);
			// FIXME: Move to simultaneous processing of these messages?
			final FrameworkMessage msg = repastS_AgentContext
					.getRepastS_SimulationRunContext()
					.readFrameworkMessageFromDistributedSystem();

			System.out.println(loggingPrefix
					+ "Received distributed decision: "
					+ XMLUtilities.convertDocumentToXMLString(msg.getDocument()
							.getRootElement(), true));

			final List<String> selfPoint = msg
					.getSelfLocationFromNextDistributedAutonomousAgentNextAgentModelActor(msg);
			for (int i = 0; i < selfPoint.size(); i++) {
				System.out.println(loggingPrefix + "Move Towards Location:"
						+ String.valueOf(i) + " : " + String.valueOf(selfPoint.get(i)));
			}
			final int xValue = Integer.parseInt(selfPoint.get(0));
			final int yValue = Integer.parseInt(selfPoint.get(1));

			for (final GridCell<Zombie> cell : gridCells) {
				if (cell.getPoint().getX() == xValue && cell.getPoint().getY() == yValue) {
					moveToPoint = cell.getPoint();
				}
			}
			assert (moveToPoint != null);

		} else
			moveToPoint = pointWithLeastZombies;
		// /////////////////////////////////////////////

		// /////////////////////////////////////////////
		// Back to the original JZombies Code
		if (energy > 0) {
			moveTowards(moveToPoint);
		} else {
			energy = startingEnergy;
		}
		// /////////////////////////////////////////////
	}

}
