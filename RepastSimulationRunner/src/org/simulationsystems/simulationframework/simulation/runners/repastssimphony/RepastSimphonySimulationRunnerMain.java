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
			//Read the directory to the Common Simulation Framework configuration file (second argument)
			frameworkConfigurationFileName = args[1];

		RepastSimulationRunner repastSimulationRunner = new RepastSimulationRunner();

		try {
			repastSimulationRunner.load(file, frameworkConfigurationFileName); // Load the Repast Scenario
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Run the simulation a few times to check for cleanup and init issues.
		int simulation_runs = 2;
		for (int i = 0; i < simulation_runs; i++) {
			repastSimulationRunner.runInitialize(); // initialize the run
			
			//Hard Coded for now
			double max_ticks = 3;
			double tick = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
			System.out.println("Tick: " + tick + " Model Action Count: " + repastSimulationRunner.getModelActionCount()
					+ " isStopped: " + repastSimulationRunner.getIsStopped());
			
			// loop until last action is left
			while (repastSimulationRunner.getActionCount() > 0 ) {
				System.out.println("Enter Loop. tick:"+tick);

				if (repastSimulationRunner.getModelActionCount() == 0 || repastSimulationRunner.getIsStopped()) {
					repastSimulationRunner.setFinishing(true);
					System.out.println("setting finishing");
				}
				System.out.println("stepping");
				repastSimulationRunner.step(); // execute all scheduled actions at next tick
				tick = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
				if (max_ticks - tick == 0.0 && !repastSimulationRunner.getIsStopped()) { //just executed the last step
					repastSimulationRunner.stop();
					System.out.println("Tick="+tick+" Stopping");
				}
				
				//Prints out one or two extra times depending on where the simulation is ended from,
				//either in the model: print twice,  or through the runner: print three times
				System.out.println("Executed step. Tick: " + tick + " Model Action Count: " + repastSimulationRunner.getModelActionCount()
						+ " isStopped: " + repastSimulationRunner.getIsStopped());
			}

			if (!repastSimulationRunner.getIsStopped()) //Don't stop twice
				repastSimulationRunner.stop(); // Execute any actions scheduled at run at the
								// end
			repastSimulationRunner.cleanUpRun();
		}
		repastSimulationRunner.cleanUpBatch(); // run after all runs complete
	}
}
