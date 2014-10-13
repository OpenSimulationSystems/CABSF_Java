package org.simulationsystems.simulationframework.simulation.runners.repastssimphony;

import java.io.File;
import repast.simphony.engine.environment.RunEnvironment;

/*
 * The Repast Simulation RepastSimphonySimulationRunnerMain is an application that programmatically runs a Repast simulation
 * (See http://repast.sourceforge.net/docs/RepastSimphonyFAQ.pdf )
 * It also has an embedded RepastSimphonySimulationAdapterAPI into the Common Simulation Framework.  For basic functionality
 * users should not have to modify this application.
 * 
 * The following shows where this code fits in to the common framework: 
 * 
 * Common Framework---> Common Framework API --> ***SIMULATION AND AGENT ADAPTER(S)***
 * --> Simulation and Agent Developers --> End Users of Simulation
 * 
 * Based on TestMain_2.java from Repast FAQ:
 * http://sourceforge.net/p/repast/repast-simphony-docs/ci/master/tree/docs/RepastFAQ/TestMain_2.java
 * 
 * @author Jorge Calderon
 * 
 */
public class RepastSimphonySimulationRunnerMain {

	public static void main(String[] args) {
		// The Repast scenario Directory
		File file = new File(args[0]);

		String frameworkConfigurationFileName = null;
		if (args.length >= 2)
			// Read the directory to the Common Simulation Framework configuration file (second
			// argument)
			frameworkConfigurationFileName = args[1];
		// TODO: Add Validation of CSF configuration file
		if (frameworkConfigurationFileName != null)
			;
		RepastSimulationRunner repastSimulationRunner = new RepastSimulationRunner();

		try {
			repastSimulationRunner.load(file, frameworkConfigurationFileName); // Load the Repast
																				// Scenario
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Run the simulation a few times to check for cleanup and init issues.
		// TODO: Tie in the number of simulation runs from the configuration
		int simulation_runs = 2;
		for (int i = 0; i < simulation_runs; i++) {
			repastSimulationRunner.runInitialize(); // initialize the run

			// Hard Coded for now
			// TODO: Tie in the maximum ticks in this simulation run from the configuration
			double max_ticks = 3;
			double tick = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
			
			System.out
			.println("Starting simulation run.  -1 means initial state.  Corresponding values in model out files use 0 instead of -1");
			logHelper(tick, repastSimulationRunner.getModelActionCount(),
					repastSimulationRunner.getIsStopped());
			// loop until last action is left
			while (repastSimulationRunner.getActionCount() > 0) {
				System.out.println("Enter Loop. tick:" + tick);

				if (repastSimulationRunner.getModelActionCount() == 0
						|| repastSimulationRunner.getIsStopped()) {
					repastSimulationRunner.setFinishing(true);
					System.out.println("Setting Finishing");
				}
				System.out.println("Stepping");
				repastSimulationRunner.step(); // execute all scheduled actions at next tick
				tick = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
				System.out.println("Finished Stepping.  New Tick: " + tick);

				// Call stop() after the last step. One "end" action is left, so two extra
				// iterations of this loop will occur instead of one extra one when the end is
				// specified from within the Repast model.

				if (max_ticks - tick == 0.0 && !repastSimulationRunner.getIsStopped()) {
					repastSimulationRunner.stop();
					System.out.println("Stopping");
				}

				// Prints out one or two extra times depending on where the simulation is ended
				// from, either in the model: print twice, or through the runner: print three times
				logHelper(tick, repastSimulationRunner.getModelActionCount(),
						repastSimulationRunner.getIsStopped());
			}

			if (!repastSimulationRunner.getIsStopped()) // Don't stop twice
				repastSimulationRunner.stop(); // Execute any actions scheduled at run at the end
			repastSimulationRunner.cleanUpRun();
		}
		repastSimulationRunner.cleanUpBatch(); // run after all runs complete
	}

	/*
	 * 
	 */
	/*
	 * TODO: Switch to sl4j
	 */
	static private void logHelper(double tick, int modelActionCount, boolean isStopped) {
				System.out.println("Tick: " + tick + " Model Action Count: " + modelActionCount
				+ " isStopped: " + isStopped);
	}
}
