/*
 *
 */
package org.opensimulationsystems.cabsf.sim.engines.runners.repastS;

import java.io.File;

// TODO: Auto-generated Javadoc
/**
 * Contains most of the logic for the RepastS Simulation Runner.
 *
 * Based on TestRunner_2.java from:
 * http://sourceforge.net/p/repast/repast-simphony
 * -docs/ci/master/tree/docs/RepastFAQ/TestRunner_2.java
 *
 * @author Jorge Calderon
 * @version 0.1
 * @see RepastS_SimulationRunnerMain
 * @since 0.1
 */
public class RepastS_SimulationRunner extends AbstractRunner {

    // TODO: Add public getter to use native Repast logging from the runner main
    /** The msg center. */
    private static MessageCenter msgCenter = MessageCenter
            .getMessageCenter(RepastS_SimulationRunner.class);

    /** The repast s_ simulation adapter API. */
    private RepastS_SimulationAdapterAPI repastS_SimulationAdapterAPI;

    /** The repast s_ simulation run group context. */
    private RepastS_SimulationRunGroupContext_CABSF repastS_SimulationRunGroupContext_CABSF;

    /** The last repast s_ simulation run context. */
    private RepastS_SimulationRunContext_CABSF lastRepastS_SimulationRunContext;

    /** The simulation runner type. */
    private CABSF_SIMULATION_DISTRIBUATION_TYPE simulationRunnerType;

    /** The is stopped. */
    private boolean isStopped;

    /** The run environment builder. */
    private final RunEnvironmentBuilder runEnvironmentBuilder;

    /** The controller. */
    protected Controller controller;

    /** The pause. */
    protected boolean pause = false;

    /** The monitor. */
    protected Object monitor = new Object();

    /** The producer. */
    protected SweeperProducer producer;

    /** The schedule. */
    private ISchedule schedule; // level

    private File scenarioDir;

    private String secondProgramArgument;

    /**
     * Instantiates a new repast s_ simulation runner.
     */
    public RepastS_SimulationRunner() {
        runEnvironmentBuilder = new DefaultRunEnvironmentBuilder(this, true);
        controller = new DefaultController(runEnvironmentBuilder);
        controller.setScheduleRunner(this);
    }

    /**
     * Clean up batch.
     */
    public void cleanUpBatch() {
        controller.batchCleanup();
    }

    /**
     * Clean up run.
     */
    public void cleanUpRun() {
        controller.runCleanup();
        isStopped = false; // Clear this flag for the next simulation run
        if (lastRepastS_SimulationRunContext != null) { // if CABSF run
            lastRepastS_SimulationRunContext
            .closeInterface(lastRepastS_SimulationRunContext
                    .getSimulationRunContext());
            lastRepastS_SimulationRunContext.terminateSimulationRun();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see repast.simphony.engine.graph.Executor#execute(java.lang.Object)
     */
    @Override
    public void execute(final RunState toExecuteOn) {
        // required AbstractRunner stub. We will control the
        // schedule directly.
    }

    /**
     * Gets the action count (number of non-model actions in the schedule).
     *
     * @return the action count
     */
    public int getActionCount() {
        return schedule.getActionCount();
    }

    /**
     * Checks if the simulation is stopped. For example, if the RepastS
     * simulation's builder class sets the maximum number of ticks, this would
     * return true after all ticks have executed.
     *
     * @return the checks if is stopped
     */
    public boolean getIsStopped() {
        return isStopped;
    }

    /**
     * Gets the model action count.
     *
     * @return the model action count
     */
    public int getModelActionCount() {
        return schedule.getModelActionCount();
    }

    /**
     * Gets the next scheduled time.
     *
     * @return the next scheduled time
     */
    public double getNextScheduledTime() {
        return ((Schedule) RunEnvironment.getInstance().getCurrentSchedule())
                .peekNextAction().getNextTime();
    }

    /**
     * Gets the repast s_ run group context.
     *
     * @return the repast s_ run group context
     */
    public RepastS_SimulationRunGroupContext_CABSF getRepastS_SimulationRunGroupContext() {
        return repastS_SimulationRunGroupContext_CABSF;
    }

    /**
     * Gets the simulation runner type.
     *
     * @return the simulation runner type
     */
    public CABSF_SIMULATION_DISTRIBUATION_TYPE getSimulationRunnerType() {
        return simulationRunnerType;
    }

    /**
     * Loads the Repast Simphony simulation and, optionally, the Common
     * Agent-Based Simulation Framework depending on whether or not the CABSF
     * configuration file is supplied as the second argument. The second
     * argument could also contains flags in place of a configuration file. In
     * that case, the simulation is run in CABSF-disabled mode, however the
     * flags may be used to correct issues with Repast Simphony.
     *
     * @param scenarioDir
     *            the RepastS scenario directory
     * @param secondProgramArgument
     *            the first program argument
     * @throws Exception
     *             the exception
     */
    public void load(final File scenarioDir, final String secondProgramArgument)
            throws Exception {
        this.scenarioDir = scenarioDir;
        this.secondProgramArgument = secondProgramArgument;

        if (scenarioDir.exists()) {
            final BatchScenarioLoader loader = new BatchScenarioLoader(scenarioDir);
            final ControllerRegistry registry = loader.load(runEnvironmentBuilder);
            controller.setControllerRegistry(registry);
        } else {
            msgCenter.error("Scenario not found", new IllegalArgumentException(
                    "Invalid scenario " + scenarioDir.getAbsolutePath()));
            throw new CabsfInitializationRuntimeException(
                    "Unable to initialize the simulation run.  Are you pointing to the right simulation configuration directory?  In Repast Simphony, it ends in .rs.  Tried using:  "
                            + scenarioDir);
        }
        controller.batchInitialize();

        // TODO: Add validation of this number against the actual number of
        // distributed agent models

        // Call the concrete Adapter as this Adapter is only for Repast
        // Simphony
        repastS_SimulationAdapterAPI = RepastS_SimulationAdapterAPI.getInstance();

        // FIXME: Use Nick's fix. Check other location for calling this method.
        // FIXME: Also check if the current code changes when taking out one of the calls in the random number generation (compare the results before and after).
        // Temporary Fix to set the Parameters in the simulation
        repastS_SimulationAdapterAPI.applyRssrParametersFix(controller, scenarioDir,
                secondProgramArgument);
        final String cabsfConfigurationFileName = repastS_SimulationAdapterAPI
                .convertFirstProgramArgumentToConfigurationFileName(secondProgramArgument);

        // If Common Framework configuration file is provided, initialize Common
        // Framework
        // otherwise run the simulation as a regular Repast Simphony simulation
        // (programmatically).
        if (cabsfConfigurationFileName != null) {
            repastS_SimulationRunGroupContext_CABSF = repastS_SimulationAdapterAPI
                    .initializeAPI(cabsfConfigurationFileName);
            simulationRunnerType = CABSF_SIMULATION_DISTRIBUATION_TYPE.DISTRIBUTED;
        } else {
            simulationRunnerType = CABSF_SIMULATION_DISTRIBUATION_TYPE.NON_DISTRIBUTED;
        }

    }

    /**
     * Initializes a single simulation run. Called after the simulation and (if
     * configured) Common Agent-Based Simulation Framework are initialized.
     *
     * @return the repast s_ simulation run context
     */
    public RepastS_SimulationRunContext_CABSF runInitialize(final boolean executeHandshake)
            throws Exception {
        controller.runInitialize(repastS_SimulationAdapterAPI.applyRssrParametersFix(
                controller, scenarioDir, secondProgramArgument));
        schedule = RunState.getInstance().getScheduleRegistry().getModelSchedule();

        @SuppressWarnings("unchecked")
        final Context<Object> repastContextForThisRun = RunState.getInstance()
        .getMasterContext();
        assert (repastContextForThisRun != null);
        RepastS_SimulationRunContext_CABSF repastS_SimulationRunContext_CABSF = null;
        if (simulationRunnerType == CABSF_SIMULATION_DISTRIBUATION_TYPE.DISTRIBUTED) {
            repastS_SimulationRunContext_CABSF = repastS_SimulationAdapterAPI
                    .initializeSimulationRun(repastContextForThisRun,
                            repastS_SimulationRunGroupContext_CABSF, executeHandshake);
            // Fix after expanding to support multiple distributed systems.
            System.out.println(repastS_SimulationRunContext_CABSF
                    .getSimulationDistributedSystemManagers().iterator().next()
                    .logHelper());

            // TODO: Better handling for future multithreading. Set the
            // interface at the run group level, close after all runs have
            // executed.

            lastRepastS_SimulationRunContext = repastS_SimulationRunContext_CABSF;

            // Now we're ready to start sending the tick information
        } else {
            // Apply random seed context add fix only if not a CABSF-enabled
            // run.
            repastS_SimulationAdapterAPI.applyRssrRandomSeedContextAddFix(controller,
                    scenarioDir, secondProgramArgument);
        }

        return repastS_SimulationRunContext_CABSF;
    }

    /**
     * Sets the finishing variable. Called after the last step in the simulation
     * run executes.
     *
     * @param fin
     *            the new finishing
     */
    public void setFinishing(final boolean fin) {
        schedule.setFinishing(fin);
    }

    /*
     * Performs the step in the simulation
     *
     * (non-Javadoc)
     *
     * @see repast.simphony.engine.environment.AbstractRunner#step()
     */
    @Override
    public void step() {
        // System.out.println("Step");
        schedule.execute();
    }

    //
    /*
     * Call the end actions on the scheduler
     *
     * (non-Javadoc)
     *
     * @see repast.simphony.engine.environment.AbstractRunner#stop()
     */
    @Override
    public void stop() {
        if (schedule != null) {
            isStopped = true;
            schedule.executeEndActions();
        }
    }

}
