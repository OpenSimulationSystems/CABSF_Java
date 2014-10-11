package org.simulationsystems.simulationframework.simulation.runners.repastssimphony;

import java.io.File;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.environment.RunState;

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
		if (args.length >=2)
			frameworkConfigurationFileName = args[1];

		RepastSimulationRunner repastSimulationRunner = new RepastSimulationRunner();

		try {
			repastSimulationRunner.load(file, frameworkConfigurationFileName); // Load the Repast Scenario
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Run the Sim a few times to check for cleanup and init issues.
		int simulation_runs = 2;
		for (int i = 0; i < simulation_runs; i++) {
			repastSimulationRunner.runInitialize(); // initialize the run
			
			// double endTime = 1000.0; // some arbitrary end time
			// RunEnvironment.getInstance().endAt(endTime);
			int max_ticks = 3;
			double tick = 0;
			// loop until last action is left
			while (repastSimulationRunner.getActionCount() > 0) {
				// max_ticks-2: -1 since this check is done on the second to
				// last tick and another -1 since we start counting at 0
				// Ticks actually jump from -1 to 1,2,3,etc. in this code, but
				// in the model output file it goes: 0, 1, 2, etc.
				if (repastSimulationRunner.getModelActionCount() == 0 || (max_ticks - tick == 2) || repastSimulationRunner.getIsStopped()) {
					repastSimulationRunner.setFinishing(true);
				}
				tick = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
				System.out.println("Ticks: " + tick + " Model Action Count: " + repastSimulationRunner.getModelActionCount()
						+ " isStopped: " + repastSimulationRunner.getIsStopped());
				repastSimulationRunner.step(); // execute all scheduled actions at next tick
			}

			if (!repastSimulationRunner.getIsStopped())
				repastSimulationRunner.stop(); // Execute any actions scheduled at run at the
								// end
			repastSimulationRunner.cleanUpRun();
		}
		repastSimulationRunner.cleanUpBatch(); // run after all runs complete
	}
}
