package org.simulationsystems.csf.sim.engines.runners.repastS;

import java.io.File;

import repast.simphony.engine.environment.RunEnvironment;

// TODO: Auto-generated Javadoc
/**
 * The Repast Simphony (RepastS) Simulation Runner programmatically runs a
 * RepastS Simulation. This class is taken from the Repast FAQ for running a
 * program from another program. It is enhanced to enable the incorporation of
 * the Common Simulation Framework.
 * 
 * Currently, this is the only way to run a RepastS simulation using the Common
 * Simulation Framework functionality.
 *
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class RepastS_SimulationRunnerMain {

	/*
	 * TODO: Switch to sl4j
	 */
	/**
	 * Log helper.
	 *
	 * @param tick
	 *            the tick
	 * @param modelActionCount
	 *            the model action count
	 * @param isStopped
	 *            the is stopped
	 */
	static private void logHelper(double tick, int modelActionCount,
			boolean isStopped) {
		System.out.println("Tick: " + tick + " Model Action Count: "
				+ modelActionCount + " isStopped: " + isStopped);
	}

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 * @throws Exception
	 *             the exception
	 */
	public static void main(String[] args) throws Exception {
		String csfConfigurationFileName = null;
		if (args.length >= 2)
			// Read the directory to the Common Simulation Framework
			// configuration file (second
			// argument)
			csfConfigurationFileName = args[1];
		// TODO: Add Validation of CSF configuration file
		// if (frameworkConfigurationFileName == null)
		// throw new
		// CsfInitializationRuntimeException("The Repast scenario directory must be provided");

		// The Repast scenario Directory
		File file = new File(args[0]);
		RepastS_SimulationRunner repastS_SimulationRunner = new RepastS_SimulationRunner();

		repastS_SimulationRunner.load(file, csfConfigurationFileName); // Load

		// Run the simulation a few times to check for cleanup and init issues.
		// TODO: Tie in the number of simulation runs from the configuration
		int simulation_runs = 2;
		for (int i = 0; i < simulation_runs; i++) {
			repastS_SimulationRunner.runInitialize();

			// Hard Coded for now
			// TODO: Tie in the maximum ticks in this simulation run from the
			// configuration
			Double max_ticks = null; // = 300d;
			double tick = RunEnvironment.getInstance().getCurrentSchedule()
					.getTickCount();

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

				// execute all scheduled actions at next tick
				repastS_SimulationRunner.step();

				tick = RunEnvironment.getInstance().getCurrentSchedule()
						.getTickCount();
				System.out.println("Finished Stepping.  New Tick: " + tick);

				// Call stop() after the last step. One "end" action is left, so
				// two extra iterations of this loop will occur instead of one
				// extra one when the end is specified from within the Repast
				// model.
				if (max_ticks != null && max_ticks - tick == 0.0
						&& !repastS_SimulationRunner.getIsStopped()) {
					repastS_SimulationRunner.stop();
					System.out.println("Stopping");
				}

				// Prints out one or two extra times depending on where the
				// simulation is ended from, either in the model: print twice,
				// or through the runner: print three times
				logHelper(tick, repastS_SimulationRunner.getModelActionCount(),
						repastS_SimulationRunner.getIsStopped());
			}

			if (!repastS_SimulationRunner.getIsStopped()) // Don't stop twice
				repastS_SimulationRunner.stop(); // Execute any actions
			// scheduled at run at the end
			repastS_SimulationRunner.cleanUpRun();
		}
		repastS_SimulationRunner.cleanUpBatch(); // run after all runs complete

	}
}
