package org.simulationsystems.simulationframework.internal.simulationadapter.api;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.simulationsystems.simulationframework.internal.api.distributedagents.AgentMapping;
import org.simulationsystems.simulationframework.internal.api.distributedagents.CommonFrameworkDistributedAgentManager;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunState;

public class SimulationAdapterInitializationHelper {
	private SimulationAdapterAPI simulationAdapterAPI;
	private Context ctx = RunState.getSafeMasterContext();
	
	// Disabled
	private SimulationAdapterInitializationHelper() {
	}

	public SimulationAdapterInitializationHelper(SimulationAdapterAPI simulationAdapterAPI) {
		this.simulationAdapterAPI = simulationAdapterAPI;
	}

	/*
	 * Initializes the API objects for the simulation adaptor client(s). Reads
	 * the properties from the configuration file, creates the mappings between
	 * the Simulation-side agents and distributed agents.
	 * 
	 * Throws exception for error reading the Common Simulation Framework
	 * configuration file.
	 */
	protected void initializeAPI(String frameworkConfigurationFileNameName, String simulationTool)
			throws IOException {

		processFrameworkConfigurationProperties(frameworkConfigurationFileNameName);		
	}

	/*
	 * 
	 */
	protected void initializeSimulationRun() {
		mapSimulationAgentsToDistributedAgents();
	}
	/*
	 * Reads the Common Simulation Framework Configuration File
	 */
	private void processFrameworkConfigurationProperties(
			String frameworkConfigurationFileNameName) throws IOException {
		/*
		 * FileInputStream fstream = new FileInputStream("textfile.txt"); // Get
		 * the object of DataInputStream DataInputStream in = new
		 * DataInputStream(fstream); BufferedReader br = new BufferedReader(new
		 * InputStreamReader(in)); String strLine; // Read File Line By Line
		 * while ((strLine = br.readLine()) != null) { // Print the content on
		 * the console System.out.println(strLine); }
		 */
	}

	private void mapSimulationAgentsToDistributedAgents() {
		CommonFrameworkDistributedAgentManager mgr = simulationAdapterAPI.getCommonFrameworkDistributedAgentManager();
		
		//Loop through the Agent mappings from the configuration file
		// TODO Auto-generated constructor stub
		//List<Object> simulationObjects = getSimulationAgents();
		
		// HARD CODED 200 HUMANS FOR NOW
		
		// Mock up some data for now. Takle jzombies Repast simulation
		//Distribute all Human agents
		import repast.simphony.engine.environment.RunState;
		AgentMapping am = mgr.createAgentMapping(fullyQualifiedSimulationAgent);
	}
	
	private List<Object> getSimulationAgents() {
		return new ArrayList();
	}
}
