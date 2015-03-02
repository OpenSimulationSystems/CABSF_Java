package org.simulationsystems.csf.common.csfmodel;

// TODO: Auto-generated Javadoc
// NON_CSF_SIMULATION Run RepastS Programmatically without the Common
// Simulation Framework
// CSF_SIMULATION Run RepastS programmatically with the Common Simulation
// Framework
/**
 * The type of simulation. This is used to identify whether a simulation will be run with
 * the full CSF functionality or as a simple ABMS system only simulation (such as an
 * RepastS-only simulation).
 * 
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public enum SIMULATION_TYPE {

	/** The non csf simulation. */
	NON_CSF_SIMULATION,
	/** The csf simulation. */
	CSF_SIMULATION
}