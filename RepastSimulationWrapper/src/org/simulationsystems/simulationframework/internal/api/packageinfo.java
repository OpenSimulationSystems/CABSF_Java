/**
 * This API is only for use by developers of adapters to connect simulation
 * tools (such as Repast) and agent-based systems (such as JADE) into the common
 * simulation framework. Simulation and Agent developers using such systems
 * should use the appropriate adapter(s).
 * 
 * Common Framework---> This API --> Adapters --> Simulation and Agent
 * Developers --> End Users of Simulation
 * 
 * Currently supported Adaptors (Users of this API): Repast - Via the
 * "Repast Simulation Wrapper" Application, which is both an Adapter into the
 * common simulation framework and its own application programmatically running
 * Repast.
 * 
 * JADE - Via the "Common Simulation Framework Adapter JADE Agent".
 * 
 * THIS PACKAGE WILL BE MOVED TO A SEPARATE JAR. TEMPORARILY HERE WIH THE REPAST
 * SIMULATION WRAPPER (RSW)
 * 
 * @author Jorge Calderon
 */
package org.simulationsystems.simulationframework.internal.api;