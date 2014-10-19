/**
 * This API is only for use by developers of adapters to connect simulation
 * tools (such as Repast) and agent-based systems (such as JADE) into the common
 * simulation framework. Simulation and Agent developers using such systems
 * should use the appropriate adapter(s). The following highlights the where in
 * the overall system this code sits:<br/>
 * <br/>
 * 
 * Simulation-side:<br/>
 * 1-Common Framework---> 2-***COMMON FRAMEWORK API*** --> 3-Simulation
 * Adapters (Common Framework Repast RepastS_SimulationAdapterAPI)- API --> 4-Simulation Tool (Either programmatic runner such as the 
 * Repast Simulation RepastS_SimulationRunnerMain or, for future work, GUIs such as Repast Simphony GUI) --> 5-End users of Simulation<br/><br/>
 * 
 * Distributed Agent side:<br/>
 * 1-Common Framework---> 2-***COMMON FRAMEWORK API*** --> 3-Distributed Agent
 * Adapters (Common Framework RepastS_SimulationAdapterAPI)- API --> 4-Simulation Tool (Either programmatic runner such as the 
 * Rep
 * Common Framework---> ***COMMON FRAMEWORK API*** --> Simulation and Agent
 * RepastS_SimulationAdapterAPI(s) --> Simulations and Agents (Such as Repast simulations and JADE
 * agents) --> End Users of Simulation<br/><br/>
 * 
 * Currently supported Adaptors (Implementors of this API):<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Repast - Via the "Repast Simulation RepastS_SimulationRunnerMain"
 * Application, which is both an RepastS_SimulationAdapterAPI into the common simulation framework
 * and its own application programmatically running Repast as a library.<br/>
 * <br/>
 * 
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;JADE - Via the
 * "Common Simulation Framework RepastS_SimulationAdapterAPI JADE Agent".<br/><br/>
 * 
 * THIS PACKAGE WILL BE MOVED TO A SEPARATE JAR. TEMPORARILY HERE WIH THE REPAST
 * SIMULATION WRAPPER (RSW)<br/>
 * 
 * @author Jorge Calderon
 */
package org.simulationsystems.simulationframework.simulation.adapters.api;