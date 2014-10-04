package org.simulationsystems.simulationframework.adapters.repastsimulationwrapper;

import java.io.File;
import repast.simphony.engine.environment.RunEnvironment;

/*
 * Based on TestMain_2.java from:
 * http://sourceforge.net/p/repast/repast-simphony-docs/ci/master/tree/docs/RepastFAQ/TestMain_2.java
 * 
 * 
 * @author Jorge Calderon
 * 
 */
public class RepastSimulationWrapper {

	public static void main(String[] args) {
		// The Repast scenario Directory
		File file = new File(args[0]);

		RepastSimulationRunner runner = new RepastSimulationRunner();

		try {
			runner.load(file); // Load the Repast Scenario
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Run the Sim a few times to check for cleanup and init issues.
		int simulation_runs = 2;
		for (int i = 0; i < simulation_runs; i++) {
			runner.runInitialize(); // initialize the run

			// double endTime = 1000.0; // some arbitrary end time
			// RunEnvironment.getInstance().endAt(endTime);
			int max_ticks = 30;
			double tick = 0;
			// loop until last action is left
			while (runner.getActionCount() > 0) {
				// MAX_TICKS-2: -1 since we check here on second to last tick
				// and -1 since we start counting at 0
				// Ticks actually jump from -1 to 1 in this loop, but in the
				// model output file it goes: 0, 1, 2, etc.
				if (runner.getModelActionCount() == 0 || (max_ticks - tick == 2) || runner.getIsStopped()) {
					runner.setFinishing(true);
				}
				tick = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
				System.out.println("Ticks: " + tick + " Model Action Count: " + runner.getModelActionCount() + " isStopped: "+runner.getIsStopped());
				runner.step(); // execute all scheduled actions at next tick
			}
			
			if (!runner.getIsStopped())
				runner.stop(); // Execute any actions scheduled at run at the end
			runner.cleanUpRun();
		}
		runner.cleanUpBatch(); // run after all runs complete
	}
}
