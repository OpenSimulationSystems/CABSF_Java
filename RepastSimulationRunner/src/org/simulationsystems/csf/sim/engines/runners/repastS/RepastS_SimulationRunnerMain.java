package org.simulationsystems.csf.sim.engines.runners.repastS;
import java.io.File;

import org.simulationsystems.csf.common.csfmodel.csfexceptions.CsfInitializationRuntimeException;
import org.simulationsystems.csf.sim.engines.adapters.repastS.api.RepastS_SimulationRunContext;

import repast.simphony.engine.environment.RunEnvironment;

/*
 * The Repast Simulation RepastS_SimulationRunnerMain is an application that programmatically runs a
 * Repast simulation (See http://repast.sourceforge.net/docs/RepastSimphonyFAQ.pdf ) It also has an
 * embedded JADE_DistributedSystemAdapterAPI into the Common Simulation Framework. For basic
 * functionality users should not have to modify this application.
 * 
 * The following shows where this code fits in to the common framework:
 * 
 * Common Framework---> Common Framework API --> ***SIMULATION AND AGENT ADAPTER(S)*** -->
 * Simulation and Agent Developers --> End Users of Simulation
 * 
 * Based on TestMain_2.java from Repast FAQ:
 * http://sourceforge.net/p/repast/repast-simphony-docs/ci/
 * master/tree/docs/RepastFAQ/TestMain_2.java
 * 
 * @author Jorge Calderon
 */
public class RepastS_SimulationRunnerMain {

	public static void main(String[] args) throws Exception {
		String frameworkConfigurationFileName = null;
		if (args.length >= 2)
			// Read the directory to the Common Simulation Framework configuration file (second
			// argument)
			frameworkConfigurationFileName = args[1];
		// TODO: Add Validation of CSF configuration file
		//if (frameworkConfigurationFileName == null)
		//	throw new CsfInitializationRuntimeException("The Repast scenario directory must be provided");
		
		// The Repast scenario Directory
		File file = new File(args[0]);
		RepastS_SimulationRunner repastS_SimulationRunner = new RepastS_SimulationRunner();

		// try {
		repastS_SimulationRunner.load(file, frameworkConfigurationFileName); // Load the Repast
																				// Scenario
		/*
		 * } catch (Exception e) { e.printStackTrace(); }
		 */

		// Run the simulation a few times to check for cleanup and init issues.
		// TODO: Tie in the number of simulation runs from the configuration
		int simulation_runs = 1;
		for (int i = 0; i < simulation_runs; i++) {
			RepastS_SimulationRunContext repastS_SimulationRunContext = repastS_SimulationRunner
					.runInitialize(); // initialize the run

			// Hard Coded for now
			// TODO: Tie in the maximum ticks in this simulation run from the configuration
			Double max_ticks = 300d;
			double tick = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();

			System.out
					.println("Starting simulation run.  -1 means initial state.  Corresponding values in model out files use 0 instead of -1");
			logHelper(tick, repastS_SimulationRunner.getModelActionCount(),
					repastS_SimulationRunner.getIsStopped());
			// loop until last action is left
			while (repastS_SimulationRunner.getActionCount() > 0) {
				System.out.println("Enter Loop. tick:" + tick);

				if (repastS_SimulationRunner.getModelActionCount() == 0
						|| repastS_SimulationRunner.getIsStopped()) {
					repastS_SimulationRunner.setFinishing(true);
					System.out.println("Setting Finishing");
				}
				System.out.println("Stepping");
				
				//repastS_SimulationRunner.csfPreStep(repastS_SimulationRunContext);
				repastS_SimulationRunner.step(); // execute all scheduled actions at next tick

				tick = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
				System.out.println("Finished Stepping.  New Tick: " + tick);

				// Call stop() after the last step. One "end" action is left, so two extra
				// iterations of this loop will occur instead of one extra one when the end is
				// specified from within the Repast model.
				if (max_ticks != null && max_ticks - tick == 0.0 && !repastS_SimulationRunner.getIsStopped()) {
					repastS_SimulationRunner.stop();
					System.out.println("Stopping");
				}

				// Prints out one or two extra times depending on where the simulation is ended
				// from, either in the model: print twice, or through the runner: print three times
				logHelper(tick, repastS_SimulationRunner.getModelActionCount(),
						repastS_SimulationRunner.getIsStopped());
			}

			if (!repastS_SimulationRunner.getIsStopped()) // Don't stop twice
				repastS_SimulationRunner.stop(); // Execute any actions scheduled at run at the end
			repastS_SimulationRunner.cleanUpRun();
		}
		repastS_SimulationRunner.cleanUpBatch(); // run after all runs complete

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
