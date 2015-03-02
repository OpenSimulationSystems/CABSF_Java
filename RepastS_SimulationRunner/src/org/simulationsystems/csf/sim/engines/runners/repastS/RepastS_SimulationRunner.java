/*
 * 
 */
package org.simulationsystems.csf.sim.engines.runners.repastS;

import java.io.File;

import org.simulationsystems.csf.common.csfmodel.SIMULATION_TYPE;
import org.simulationsystems.csf.common.csfmodel.csfexceptions.CsfInitializationRuntimeException;
import org.simulationsystems.csf.sim.adapters.simengines.repastS.api.RepastS_SimulationAdapterAPI;
import org.simulationsystems.csf.sim.adapters.simengines.repastS.api.RepastS_SimulationRunContext;
import org.simulationsystems.csf.sim.adapters.simengines.repastS.api.RepastS_SimulationRunGroupContext;

import repast.simphony.batch.BatchScenarioLoader;
import repast.simphony.context.Context;
import repast.simphony.engine.controller.Controller;
import repast.simphony.engine.controller.DefaultController;
import repast.simphony.engine.environment.AbstractRunner;
import repast.simphony.engine.environment.ControllerRegistry;
import repast.simphony.engine.environment.DefaultRunEnvironmentBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.environment.RunEnvironmentBuilder;
import repast.simphony.engine.environment.RunState;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.Schedule;
import repast.simphony.parameter.DefaultParameters;
import repast.simphony.parameter.ParameterConstants;
import repast.simphony.parameter.SweeperProducer;
import simphony.util.messages.MessageCenter;

/**
 * Contains most of the logic for the RepastS Simulation Runner.
 * 
 * Based on TestRunner_2.java from:
 * http://sourceforge.net/p/repast/repast-simphony
 * -docs/ci/master/tree/docs/RepastFAQ/TestRunner_2.java
 * 
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 * @see RepastS_SimulationRunnerMain
 */
public class RepastS_SimulationRunner extends AbstractRunner {

	/** The repast s_ simulation adapter API. */
	private RepastS_SimulationAdapterAPI repastS_SimulationAdapterAPI;

	/** The repast s_ simulation run group context. */
	private RepastS_SimulationRunGroupContext repastS_SimulationRunGroupContext;

	/** The last repast s_ simulation run context. */
	private RepastS_SimulationRunContext lastRepastS_SimulationRunContext;

	/** The simulation runner type. */
	private SIMULATION_TYPE simulationRunnerType;

	/** The is stopped. */
	private boolean isStopped;

	// TODO: Add public getter to use native Repast logging from the runner main
	/** The msg center. */
	private static MessageCenter msgCenter = MessageCenter
			.getMessageCenter(RepastS_SimulationRunner.class);

	/** The run environment builder. */
	private RunEnvironmentBuilder runEnvironmentBuilder;

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
		if (lastRepastS_SimulationRunContext != null) { // if CSF run
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
	public void execute(RunState toExecuteOn) {
		// required AbstractRunner stub. We will control the
		// schedule directly.
	}

	/**
	 * Gets the action count (number of non-model actions in the schedule)
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
	 * Gets the repast s_ simulation run group context.
	 * 
	 * @return the repast s_ simulation run group context
	 */
	public RepastS_SimulationRunGroupContext getRepastS_SimulationRunGroupContext() {
		return repastS_SimulationRunGroupContext;
	}

	/**
	 * Gets the simulation runner type.
	 * 
	 * @return the simulation runner type
	 */
	public SIMULATION_TYPE getSimulationRunnerType() {
		return simulationRunnerType;
	}

	/**
	 * Loads the Repast Simphony simulation and, optionally, the Common
	 * Simulation Framework depending on whether or not the
	 * csfConfigurationFileName is supplied.
	 * 
	 * @param scenarioDir
	 *            the RepastS scenario directory
	 * @param csfConfigurationFileName
	 *            the CSF configuration file name
	 * @throws Exception
	 *             the exception
	 */
	public void load(File scenarioDir, String csfConfigurationFileName)
			throws Exception {
		if (scenarioDir.exists()) {
			BatchScenarioLoader loader = new BatchScenarioLoader(scenarioDir);
			ControllerRegistry registry = loader.load(runEnvironmentBuilder);
			controller.setControllerRegistry(registry);
		} else {
			msgCenter.error("Scenario not found", new IllegalArgumentException(
					"Invalid scenario " + scenarioDir.getAbsolutePath()));
			throw new CsfInitializationRuntimeException(
					"Unable to initialize the simulation run.  Are you pointed to the right simulation configuration directory?  In Repast Simphony, it ends in .rs.  Tried using:  "
							+ scenarioDir);
		}
		controller.batchInitialize();

		// Set the Parameters across all simulation runs of this simulation
		// HARD CODED FOR NOW
		// TODO: Programmatically read the parameters from the RunState?
		// TODO: Add validation of this number against the actual number of
		// distributed agent models
		DefaultParameters defaultParameters = new DefaultParameters();
		// JZombies
		if (csfConfigurationFileName
				.equals("PLACEHOLDER_FOR_CSF_CONFIGURATION_FILE")) {
			defaultParameters.addParameter("human_count", "Human Count",
					Number.class, 6, true);
			defaultParameters.addParameter("zombie_count", "Zombie Count",
					Number.class, 6, true);
		}
		// Prisoner's Dilemma
		else {
			defaultParameters.addParameter("player_count", "Player Count",
					Number.class, 8, true);
			defaultParameters.addParameter("number_of_rounds",
					"Number of Rounds", Number.class, 10, true);

		}

		controller.runParameterSetters(defaultParameters);

		// If Common Framework configuration file is provided, initialize Common
		// Framework
		// otherwise run the simulation as a regular Repast Simphony simulation
		// (programmatically).
		if (csfConfigurationFileName != null) {
			// Call the concrete Adapter as this Adapter is only for Repast
			// Simphony
			repastS_SimulationAdapterAPI = RepastS_SimulationAdapterAPI
					.getInstance();
			repastS_SimulationRunGroupContext = repastS_SimulationAdapterAPI
					.initializeAPI(csfConfigurationFileName);
			simulationRunnerType = SIMULATION_TYPE.CSF_SIMULATION;
		} else
			simulationRunnerType = SIMULATION_TYPE.NON_CSF_SIMULATION;
	}

	/**
	 * Initializes a single simulation run. Called after the simulation and (if
	 * configured) Common Simulation Framework are initialized.
	 * 
	 * @return the repast s_ simulation run context
	 */
	public RepastS_SimulationRunContext runInitialize() {
		// Set the Seed Parameter for this simulation run
		// HARD CODED FOR NOW
		// TODO: Programmatically read the parameters
		DefaultParameters defaultParameters = new DefaultParameters();
		defaultParameters.addParameter(
				ParameterConstants.DEFAULT_RANDOM_SEED_USAGE_NAME,
				ParameterConstants.DEFAULT_RANDOM_SEED_DISPLAY_NAME,
				Number.class, 1, true);

		controller.runInitialize(defaultParameters);
		schedule = RunState.getInstance().getScheduleRegistry()
				.getModelSchedule();

		@SuppressWarnings("unchecked")
		Context<Object> repastContextForThisRun = RunState.getInstance()
		.getMasterContext();

		RepastS_SimulationRunContext repastS_SimulationRunContext = null;
		if (simulationRunnerType == SIMULATION_TYPE.CSF_SIMULATION) {
			repastS_SimulationRunContext = repastS_SimulationAdapterAPI
					.initializeSimulationRun(repastContextForThisRun,
							repastS_SimulationRunGroupContext);
			// Fix after expanding to support multiple distributed systems.
			System.out.println(repastS_SimulationRunContext
					.getSimulationDistributedSystemManagers().iterator().next()
					.logHelper());

			// TODO: Better handling for future multithreading. Set the
			// interface at the run group level, close after all runs have
			// executed.

			lastRepastS_SimulationRunContext = repastS_SimulationRunContext;

			// Now we're ready to start sending the tick information
		}

		return repastS_SimulationRunContext;
	}

	/**
	 * Sets the finishing variable. Called after the last step in the simulation
	 * run executes.
	 * 
	 * @param fin
	 *            the new finishing
	 */
	public void setFinishing(boolean fin) {
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
