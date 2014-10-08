package org.simulationsystems.simulationframework.runners.repastssimphonysimulationwrapper;

import java.io.File;

import org.simulationsystems.simulationframework.internal.simulationadapter.api.SimulationAdapterAPI;

import repast.simphony.batch.BatchScenarioLoader;
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
import simphony.util.messages.MessageCenter;

/*
 *  Based on TestRunner_2.java from:
 * http://sourceforge.net/p/repast/repast-simphony-docs/ci/master/tree/docs/RepastFAQ/TestMain_2.java

 */
public class Runner extends AbstractRunner {
	private RepastSimphonySimulationAdapterAPI repastSimphonySimulationAdapterAPI;
	
	private boolean isStopped;

	private static MessageCenter msgCenter = MessageCenter.getMessageCenter(Runner.class);

	private RunEnvironmentBuilder runEnvironmentBuilder;
	protected Controller controller;
	protected boolean pause = false;
	protected Object monitor = new Object();
	protected SweeperProducer producer;
	private ISchedule schedule;

	public Runner() {
		runEnvironmentBuilder = new DefaultRunEnvironmentBuilder(this, true);
		controller = new DefaultController(runEnvironmentBuilder);
		controller.setScheduleRunner(this);
	}

	public void load(File scenarioDir, String frameworkConfigurationFileName) throws Exception {
		if (scenarioDir.exists()) {
			BatchScenarioLoader loader = new BatchScenarioLoader(scenarioDir);
			ControllerRegistry registry = loader.load(runEnvironmentBuilder);
			controller.setControllerRegistry(registry);
		} else {
			msgCenter.error("Scenario not found",
					new IllegalArgumentException("Invalid scenario " + scenarioDir.getAbsolutePath()));
			return;
		}

		controller.batchInitialize();

		// Set the Parameters across all simulation runs of this simulation
		// HARD CODED FOR NOW
		// TODO: Programmatically read the parameters
		DefaultParameters defaultParameters = new DefaultParameters();
		defaultParameters.addParameter("human_count", "Human Count", Number.class, 200, true);
		defaultParameters.addParameter("zombie_count", "Zombie Count", Number.class, 5, true);
		controller.runParameterSetters(defaultParameters);

		// If Common Framework configuration file is provided, initialize Common
		// Framework
		// otherwise run the simulation as a regular Repast simulation
		// (programmatically).		
		if (frameworkConfigurationFileName != null) {
			simulationAdapterAPI = SimulationAdapterAPI.getInstance();
			simulationAdapterAPI.initializeAPI(frameworkConfigurationFileName);
		}
		//RunEnvironment.getInstance();
	}

	public void runInitialize() {
		// Set the Seed Parameter for this simulation run
		// HARD CODED FOR NOW
		// TODO: Programmatically read the parameters
		DefaultParameters defaultParameters = new DefaultParameters();
		defaultParameters.addParameter(ParameterConstants.DEFAULT_RANDOM_SEED_USAGE_NAME,
				ParameterConstants.DEFAULT_RANDOM_SEED_DISPLAY_NAME, Number.class, 1, true);

		controller.runInitialize(defaultParameters);
		schedule = RunState.getInstance().getScheduleRegistry().getModelSchedule();
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
		return ((Schedule) RunEnvironment.getInstance().getCurrentSchedule()).peekNextAction().getNextTime();
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
