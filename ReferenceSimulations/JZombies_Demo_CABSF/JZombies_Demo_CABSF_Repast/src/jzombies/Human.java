/**
 *
 */
package jzombies;

import java.util.List;

import org.opensimulationsystems.cabsf.common.internal.messaging.xml.XMLUtilities;
import org.opensimulationsystems.cabsf.common.model.AgentMapping;
import org.opensimulationsystems.cabsf.common.model.CABSF_SIMULATION_DISTRIBUATION_TYPE;
import org.opensimulationsystems.cabsf.common.model.cabsfexceptions.CabsfInitializationRuntimeException;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FrameworkMessage;
import org.opensimulationsystems.cabsf.sim.adapters.simengines.repastS.api.CabsfRepastS_AgentContext;
import org.opensimulationsystems.cabsf.sim.adapters.simengines.repastS.api.RepastS_AgentAdapterAPI;
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

/**
 * The Human in the JZombies simulation from the RepastS tutorial. This Repast
 * Simphony simulation, including this agent, was modified to enable integration
 * with the Common Agent-Based Simulation Framework (CABSF).
 * <p>
 * </p>
 * When utilizing CABSF's distributed mode, these Human agents are distributed
 * outside of the RepastS process/runtime, such as to agents running in a JADE
 * multi-agent system. In such cases, this Human agent here in RepastS is still
 * used to communicate with the RepastS simulation runtime, but it becomes more
 * of a representation of the "actual" agent that exists outside of RepastS,
 * which is the one really making the decisions in the simulation (i.e., the
 * intelligent agent).
 *
 *
 * @author nick
 * @author Jorge Calderon (modified Human class for integrating with the CABSF
 *         and therefore JADE)
 */
public class Human {
    /** The space. */
    private final ContinuousSpace<Object> space;

    /** The grid. */
    private final Grid<Object> grid;

    /** The starting energy. */
    private int energy;

    private int startingEnergy;

    // /////////////////////////////
    // Section Added to the CABSF version of this simulation
    /**
     * The native Repast Simphony context. Different from CABSF's RepastS agent
     * context
     */
    private Context nativeRepastScontext;

    // CABSF's own RepastS agent context, different from the native RepastS
    // context.
    private CabsfRepastS_AgentContext cabsfRepastS_AgentContext = null;

    private CABSF_SIMULATION_DISTRIBUATION_TYPE cabsfSimulationType;

    /** The simulation-specific convenience class */
    private JZombies_CABSF_Helper jZombies_CABSF_Helper;

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
     * Gets the CabsfRepastS_AgentContext
     *
     * @return the CabsfRepastS_AgentContext
     */
    public CabsfRepastS_AgentContext getCabsfRepastS_AgentContext() {
        return cabsfRepastS_AgentContext;
    }

    /**
     * Returns this Human agent's decision for which GridPoint to move to,
     * called whenever the simulation is executed in either CABSF-disabled
     * (RepastS-only) or CABSF-enabled Non-Distributed (RepastS not connected to
     * JADE) modes. This method is not used when this simulation is run
     * CABSF-enabled Distributed mode (such as RepastS connected to JADE)
     * functionality turned on. In that case the Repast Simphony simulation
     * cannot talk to a corresponding distributed software agent, as none
     * exists, so this native RepastS agent must make its own decision for the
     * tick, just as happens in any RepastS-only simulation. By keeping this
     * non-distributed alternative in this CABSF-wired RepastS simulation, we
     * allow this simulation to be run in all modes.
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
            energy--;
        }
    }

    /**
     * The step method for the Human.
     */
    @Watch(watcheeClassName = "jzombies.Zombie", watcheeFieldNames = "moved", query = "within_vn 1", whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
    public void run() {
        // ////////////////////////////////
        // Section Added to the CABSF-wired version of the JZombies
        // simulation
        if (cabsfSimulationType == null) {
            cabsfRepastS_AgentContext = RepastS_AgentAdapterAPI.getInstance()
                    .getAgentContext();
            jZombies_CABSF_Helper = new JZombies_CABSF_Helper(cabsfRepastS_AgentContext);
            try {
                this.getClass().getClassLoader();
                // final RunState rs = RunState.getInstance();
                final Iterable<Class> simulationAgentsClasses = RunState.getInstance()
                        .getMasterContext().getAgentTypes();
                final Iterable<Object> cabsfRepastContextIterable = RunState
                        .getInstance().getMasterContext()
                        .getAgentLayer(RepastS_SimulationRunContext.class);

                cabsfSimulationType = cabsfRepastS_AgentContext.initializeCabsfAgent(
                        simulationAgentsClasses, cabsfRepastContextIterable, RunState
                        .getInstance().getMasterContext());

            } catch (final CabsfInitializationRuntimeException e) {
                throw new CabsfInitializationRuntimeException(
                        "Cabsf initialization error in agent: " + this.getClass()
                        + " hash: " + this.hashCode(), e);
            }
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

        GridPoint moveTowardsPoint = null;
        // /////////////////////////////////////////////

        // /////////////////////////////////////////////
        // Section Added to the CABSF-wired version of the JZombies
        // simulation
        if (cabsfSimulationType == CABSF_SIMULATION_DISTRIBUATION_TYPE.DISTRIBUTED) {
            final SimulationDistributedSystemManager dsm = cabsfRepastS_AgentContext
                    .getRepastS_SimulationRunContext()
                    .getSimulationDistributedSystemManager(this);
            final AgentMapping am = dsm.getAgentMappingForObject(this);

            final String distributedSystemID = am.getDistributedSystemID();
            final String softwareAgentID = am.getSoftwareAgentID();
            final String agentModelID = am.getAgentModelID();
            final String loggingPrefix = "[Human " + distributedSystemID + " "
                    + softwareAgentID + " " + agentModelID + "] ";

            // LOW: (for CABSF developers only) Add support for merging multiple
            // messages bound for different agents

            jZombies_CABSF_Helper.sendMsgFromSimAgentToDistributedAgentModel(
                    loggingPrefix, this, pt, pointWithLeastZombies);

            // LOW: (for CABSF developers only) Move to simultaneous processing
            // of these messages?
            final FrameworkMessage msgFromDistSys = cabsfRepastS_AgentContext
                    .readFrameworkMessageFromDistributedSystem();

            System.out.println(loggingPrefix
                    + "Received distributed decision: "
                    + XMLUtilities.convertDocumentToXMLString(msgFromDistSys
                            .getDocument().getRootElement(), true));

            final List<String> thisAgentPoint = msgFromDistSys
                    .getThisAgentLocationFromNextSoftwareAgentNextAgentModelActorInFrameworkMessage(msgFromDistSys);
            for (int i = 0; i < thisAgentPoint.size(); i++) {
                System.out.println(loggingPrefix
                        + "Distributed Agent Model Decided to Move Towards:"
                        + String.valueOf(i) + " : "
                        + String.valueOf(thisAgentPoint.get(i)) + "  ");
            }
            final int xValueToMoveTowards = Integer.parseInt(thisAgentPoint.get(0));
            final int yValueToMoveTowards = Integer.parseInt(thisAgentPoint.get(1));

            for (final GridCell<Zombie> cell : gridCells) {
                if (cell.getPoint().getX() == xValueToMoveTowards
                        && cell.getPoint().getY() == yValueToMoveTowards) {
                    moveTowardsPoint = cell.getPoint();
                }
            }
            assert (moveTowardsPoint != null);

        }
        // Non-Distributed. Make the decision here for where to move the Human
        // agent to without asking the JADE agent agent model.
        else {
            moveTowardsPoint = pointWithLeastZombies;
        }
        // /////////////////////////////////////////////

        // /////////////////////////////////////////////
        // Back to the original JZombies Code
        if (energy > 0) {
            moveTowards(moveTowardsPoint);
        } else {
            energy = startingEnergy;
        }
        // /////////////////////////////////////////////
    }
}
