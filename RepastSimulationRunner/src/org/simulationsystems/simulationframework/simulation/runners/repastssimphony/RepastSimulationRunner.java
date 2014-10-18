package org.simulationsystems.simulationframework.simulation.runners.repastssimphony;

import java.io.File;

import org.simulationsystems.simulationframework.simulation.adapters.api.SimulationFrameworkContext;
import org.simulationsystems.simulationframework.simulation.adapters.simulationapps.api.RepastSimphonySimulationAdapterAPI;
import org.simulationsystems.simulationframework.simulation.adapters.simulationapps.api.RepastSimphonySimulationFrameworkContext;

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

/*
 *  The Repast AbstractRunner that programmatically runs a Repast Simulation.  This class is taken from the Repast FAQ and enhanced to allow incorporation of the common simulation framework.   
 *  
 *  Based on TestRunner_2.java from:
 * http://sourceforge.net/p/repast/repast-simphony-docs/ci/master/tree/docs/RepastFAQ/TestMain_2.java
 * 
 * @author 

 */
public class RepastSimulationRunner extends AbstractRunner {
	private RepastSimphonySimulationAdapterAPI repastSimphonySimulationAdapterAPI;
	
	//NON_CSF_SIMULATION Run RepastS Programmatically without the Common Simulation Framework
	//CSW_SIMULATION Run RepastS programmatically with the Common Simulation Framework
	public enum SIMULATION_RUNNER_RUN_TYPE {
		NON_CSF_SIMULATION, CSW_SIMULATION
	}
	private SIMULATION_RUNNER_RUN_TYPE simulationRunnerType;

	private boolean isStopped;
	
	//TODO: Add public getter to use native Repast logging from the runner main
	private static MessageCenter msgCenter = MessageCenter
			.getMessageCenter(RepastSimulationRunner.class);

	private RunEnvironmentBuilder runEnvironmentBuilder;
	protected Controller controller;
	protected boolean pause = false;
	protected Object monitor = new Object();
	protected SweeperProducer producer;
	private ISchedule schedule;
	private RepastSimphonySimulationFrameworkContext repastSimphonySimulationFrameworkContext; //Simulation-run-group level

	public RepastSimulationRunner() {
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
			msgCenter.error("Scenario not found", new IllegalArgumentException("Invalid scenario "
					+ scenarioDir.getAbsolutePath()));
			return;
		}

		controller.batchInitialize();

		// Set the Parameters across all simulation runs of this simulation
		// HARD CODED FOR NOW
		// TODO: Programmatically read the parameters from the RunState?
		DefaultParameters defaultParameters = new DefaultParameters();
		defaultParameters.addParameter("human_count", "Human Count", Number.class, 5, true);
		defaultParameters.addParameter("zombie_count", "Zombie Count", Number.class, 5, true);
		controller.runParameterSetters(defaultParameters);

		// If Common Framework configuration file is provided, initialize Common Framework
		// otherwise run the simulation as a regular Repast simulation (programmatically).
		if (frameworkConfigurationFileName != null) {
			// Call the concrete Adapter as this Adapter is only for Repast Simphony
			repastSimphonySimulationAdapterAPI = RepastSimphonySimulationAdapterAPI.getInstance();
			repastSimphonySimulationFrameworkContext = repastSimphonySimulationAdapterAPI.initializeAPI(frameworkConfigurationFileName);
			simulationRunnerType = RepastSimulationRunner.SIMULATION_RUNNER_RUN_TYPE.CSW_SIMULATION;
		} else
			simulationRunnerType = RepastSimulationRunner.SIMULATION_RUNNER_RUN_TYPE.NON_CSF_SIMULATION;
	}

	public SIMULATION_RUNNER_RUN_TYPE getSimulationRunnerType() {
		return simulationRunnerType;
	}

	/*
	 * Initializes a single simulation run. Called after the simulation and (if configured) Common
	 * Simulation Framework are initialized.
	 */
	public void runInitialize() {
		// Set the Seed Parameter for this simulation run
		// HARD CODED FOR NOW
		// TODO: Programmatically read the parameters
		DefaultParameters defaultParameters = new DefaultParameters();
		defaultParameters.addParameter(ParameterConstants.DEFAULT_RANDOM_SEED_USAGE_NAME,
				ParameterConstants.DEFAULT_RANDOM_SEED_DISPLAY_NAME, Number.class, 1, true);

		controller.runInitialize(defaultParameters);
		schedule = RunState.getInstance().getScheduleRegistry().getModelSchedule();

		@SuppressWarnings("unchecked")
		Context<Object> repastContextForThisRun = RunState.getInstance().getMasterContext();

		if (simulationRunnerType == RepastSimulationRunner.SIMULATION_RUNNER_RUN_TYPE.CSW_SIMULATION)
			repastSimphonySimulationAdapterAPI.initializeSimulationRun(repastContextForThisRun, repastSimphonySimulationFrameworkContext);
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
		return ((Schedule) RunEnvironment.getInstance().getCurrentSchedule()).peekNextAction()
				.getNextTime();
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
	
	//Called after the last step in the simulation run executes.
	public void setFinishing(boolean fin) {
		schedule.setFinishing(fin);
	}

	//Comment is from the Repast development team
	public void execute(RunState toExecuteOn) {
		// required AbstractRunner stub. We will control the
		// schedule directly.
	}

	public boolean getIsStopped() {
		return isStopped;
	}
}
