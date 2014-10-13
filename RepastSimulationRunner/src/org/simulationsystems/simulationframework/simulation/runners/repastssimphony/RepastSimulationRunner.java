package org.simulationsystems.simulationframework.simulation.runners.repastssimphony;

import java.io.File;
import java.util.HashSet;

import org.simulationsystems.simulationframework.simulation.adapters.api.SimulationAdapterAPI;
import org.simulationsystems.simulationframework.simulation.adapters.simulationapps.api.RepastSimphonySimulationAdapterAPI;

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
import repast.simphony.parameter.Parameters;
import repast.simphony.parameter.ParametersCreator;
import repast.simphony.parameter.SweeperProducer;
import repast.simphony.util.collections.IterableAdaptor;
import simphony.util.messages.MessageCenter;

//The Repast AbstractRunner that programmatically runs a Repast Simulation.  This class is taken from the Repast FAQ and enhanced to allow incorporation of the common simulation framework. testtest test test test test test test
/*
 *  The Repast AbstractRunner that programmatically runs a Repast Simulation.  This class is taken from the Repast FAQ and enhanced to allow incorporation of the common simulation framework. testtest test test test test test test    
 *  
 *  Based on TestRunner_2.java from:
 * http://sourceforge.net/p/repast/repast-simphony-docs/ci/master/tree/docs/RepastFAQ/TestMain_2.java

 */
public class RepastSimulationRunner extends AbstractRunner {
	private RepastSimphonySimulationAdapterAPI repastSimphonySimulationAdapterAPI;

	public enum SIMULATION_RUNNER_RUN_TYPE {
		NON_FRAMEWORK_SIMULATION, COMMON_SIMULATION_FRAMEWORK
	}

	private SIMULATION_RUNNER_RUN_TYPE simulationRunnerType;

	private boolean isStopped;

	private static MessageCenter msgCenter = MessageCenter
			.getMessageCenter(RepastSimulationRunner.class);

	private RunEnvironmentBuilder runEnvironmentBuilder;
	protected Controller controller;
	protected boolean pause = false;
	protected Object monitor = new Object();
	protected SweeperProducer producer;
	private ISchedule schedule;

	public RepastSimulationRunner() {
		runEnvironmentBuilder = new DefaultRunEnvironmentBuilder(this, true);
		controller = new DefaultController(runEnvironmentBuilder);
		controller.setScheduleRunner(this);
	}

	public void load(File scenarioDir, String frameworkConfigurationFileName)
			throws Exception {
		if (scenarioDir.exists()) {
			BatchScenarioLoader loader = new BatchScenarioLoader(scenarioDir);
			ControllerRegistry registry = loader.load(runEnvironmentBuilder);
			controller.setControllerRegistry(registry);
		} else {
			msgCenter.error("Scenario not found", new IllegalArgumentException(
					"Invalid scenario " + scenarioDir.getAbsolutePath()));
			return;
		}

		controller.batchInitialize();

		// Set the Parameters across all simulation runs of this simulation
		// HARD CODED FOR NOW
		// TODO: Programmatically read the parameters
		DefaultParameters defaultParameters = new DefaultParameters();
		defaultParameters.addParameter("human_count", "Human Count", Number.class, 5,
				true);
		defaultParameters.addParameter("zombie_count", "Zombie Count", Number.class, 5,
				true);
		controller.runParameterSetters(defaultParameters);

		// If Common Framework configuration file is provided, initialize Common Framework
		// otherwise run the simulation as a regular Repast simulation (programmatically).
		if (frameworkConfigurationFileName != null) {
			// Call the concreate Adapter as this Adapter is only for Repast Simphony
			repastSimphonySimulationAdapterAPI = RepastSimphonySimulationAdapterAPI
					.getInstance();
			repastSimphonySimulationAdapterAPI
					.initializeAPI(frameworkConfigurationFileName);
			simulationRunnerType = RepastSimulationRunner.SIMULATION_RUNNER_RUN_TYPE.COMMON_SIMULATION_FRAMEWORK;
		} else
			simulationRunnerType = RepastSimulationRunner.SIMULATION_RUNNER_RUN_TYPE.NON_FRAMEWORK_SIMULATION;
	}

	public SIMULATION_RUNNER_RUN_TYPE getSimulationRunnerType() {
		return simulationRunnerType;
	}

	/*
	 * Initializes a single simulation run. Called after the simulation and (if
	 * configured) Common Simulation Framework are initialized.
	 */
	public void runInitialize() {
		// Set the Seed Parameter for this simulation run
		// HARD CODED FOR NOW
		// TODO: Programmatically read the parameters
		DefaultParameters defaultParameters = new DefaultParameters();
		defaultParameters.addParameter(ParameterConstants.DEFAULT_RANDOM_SEED_USAGE_NAME,
				ParameterConstants.DEFAULT_RANDOM_SEED_DISPLAY_NAME, Number.class, 1,
				true);

		controller.runInitialize(defaultParameters);
		schedule = RunState.getInstance().getScheduleRegistry().getModelSchedule();

		@SuppressWarnings("unchecked")
		Context<Object> repastContextForThisRun = RunState.getInstance().getMasterContext();

		if (simulationRunnerType == RepastSimulationRunner.SIMULATION_RUNNER_RUN_TYPE.COMMON_SIMULATION_FRAMEWORK)
			repastSimphonySimulationAdapterAPI.initializeSimulationRun(repastContextForThisRun);
	}

	public void cleanUpRun() {
		controller.runCleanup();
		isStopped = false;
	}

	public void cleanUpBatch() {
		controller.batchCleanup();
	}

	// returns the tick count of the next scheduled item
	public double getNextScheduledTime() {
		return ((Schedule) RunEnvironment.getInstance().getCurrentSchedule())
				.peekNextAction().getNextTime();
	}

	// returns the number of model actions on the schedule
	public int getModelActionCount() {
		return schedule.getModelActionCount();
	}

	// returns the number of non-model actions on the schedule
	public int getActionCount() {
		return schedule.getActionCount();
	}

	// Step the schedule
	public void step() {
		// System.out.println("Step");
		schedule.execute();
	}

	// stop the schedule
	public void stop() {
		if (schedule != null) {
			isStopped = true;
			schedule.executeEndActions();
		}
	}

	public void setFinishing(boolean fin) {
		schedule.setFinishing(fin);
	}

	public void execute(RunState toExecuteOn) {
		// required AbstractRunner stub. We will control the
		// schedule directly.
	}

	public boolean getIsStopped() {
		return isStopped;
	}
}
