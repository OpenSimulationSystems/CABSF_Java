package org.opensimulationsystems.cabsf.common.csfmodel;

// TODO: Auto-generated Javadoc
// NON_CABSF_SIMULATION Run RepastS Programmatically without the Common
// Simulation Framework
// CABSF_SIMULATION Run RepastS programmatically with the Common Simulation
// Framework
/**
 * The type of simulation. This is used to identify whether a simulation will be run with
 * the full CSF functionality or as a simple ABMS system only simulation (such as an
 * RepastS-only simulation).
 *
 * @author Jorge Calderon
 * @version 0.2
 * @since 0.1
 */
public enum SIMULATION_TYPE {

	/** The non CABSF simulation. */
	NON_CABSF_SIMULATION,
	/** The CABSF simulation. */
	CABSF_SIMULATION
}