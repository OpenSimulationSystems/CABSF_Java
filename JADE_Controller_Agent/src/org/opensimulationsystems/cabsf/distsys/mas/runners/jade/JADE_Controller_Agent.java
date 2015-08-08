package org.opensimulationsystems.cabsf.distsys.mas.runners.jade;

import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.opensimulationsystems.cabsf.common.model.SYSTEM_TYPE;
import org.opensimulationsystems.cabsf.common.model.cabsfexceptions.CabsfCheckedException;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FRAMEWORK_COMMAND;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FrameworkMessage;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FrameworkMessageImpl;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.JADE_MAS_AdapterAPI;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.JADE_MAS_RunContext;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.JADE_MAS_RunGroupContext;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.nativeagents.CabsfDistributedJADEagentWrapper;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.nativeagents.NativeDistributedAutonomousAgent;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.nativeagents.NativeJADEMockContext;

// TODO: Auto-generated Javadoc
/**
 * The JADE agent implementing the CABSF JADE MAS Adapter API to enable JADE agents to
 * communicate with ABMS systems such as Repast Simphony.
 *
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class JADE_Controller_Agent extends jade.core.Agent {

	/**
	 * The Behaviour class that specifies the specific actions that the JADE Controller
	 * Agent takes when receiving a new message from another agent. This functionality is
	 * described in the JADE documentation.
	 *
	 * @author Jorge Calderon
	 * @version 0.1
	 * @since 0.1
	 */
	private class JadeControllerAgentServer extends CyclicBehaviour {

		/*
		 * (non-Javadoc)
		 *
		 * @see jade.core.behaviours.Behaviour#action()
		 */
		@Override
		public void action() {
			final MessageTemplate mt = MessageTemplate
					.MatchPerformative(ACLMessage.INFORM);
			final ACLMessage aclMsg = myAgent.receive(mt);
			if (aclMsg != null) {
				// Message matching the template has come in.
				final String msgStr = aclMsg.getContent();

				FrameworkMessage message = null;
				try {
					message = new FrameworkMessageImpl(SYSTEM_TYPE.DISTRIBUTED_SYSTEM,
							SYSTEM_TYPE.SIMULATION_ENGINE, msgStr);
				} catch (final CabsfCheckedException e) {
					// FIXME: Remove all of the agents/shutdown the MAS?
					System.out
					.println("[JADE Controller Agent] Error converting message from a distributed JADE agent to the JADE Controller Agent: "
							+ msgStr);
					doDelete();
					e.printStackTrace();
				}

				System.out
				.println("[JADE Controller Agent] Received the distributed autonomous agent (model) decision. Forwarding to the simulation engine (agent)");

				jade_MAS_RunContext.sendMessageToSimulationEngine(message,
						jade_MAS_RunContext.getDistSysRunContext());

				final FRAMEWORK_COMMAND fc = getJade_MAS_RunContext()
						.waitForAndProcessSimulationEngineMessageAfterHandshake();
				if (fc == FRAMEWORK_COMMAND.STOP_SIMULATION) {
					System.out
					.println("[JADE Controller Agent] Received Stop Simulation Command from Simulation Engine.  Listening for new simulation run");
					setupNewSimulationRun();
				}

			} else {
				block(); // Save CPU cycles by not executing this thread until
				// new messages come in.
			}

		}
	} // End of inner class

	// All of these fields below are available to the inner class

	/** The setJade_MAS_RunContext group context. */
	private static JADE_MAS_RunGroupContext jade_MAS_RunGroupContext;

	/** The jade_ MAS_ adapter api. */
	static private JADE_MAS_AdapterAPI jade_MAS_AdapterAPI;

	/** The setJade_MAS_RunContext context. */
	private JADE_MAS_RunContext jade_MAS_RunContext;

	/** The framework configuration file name. */
	String frameworkConfigurationFileName = null;

	/** The new simulation run. */
	boolean newSimulationRun = true;

	/** The jade agents. */
	private final Set<NativeDistributedAutonomousAgent> mappedJadeAgents = new HashSet<NativeDistributedAutonomousAgent>();

	// TODO: Make this configurable
	/** The number of agents. */
	private int expectedNumOfAgents;

	/**
	 * Gets the jade_ MAS_ adapter api.
	 *
	 * @return the jade_ MAS_ adapter api
	 */
	public JADE_MAS_AdapterAPI getJade_MAS_AdapterAPI() {
		return jade_MAS_AdapterAPI;
	}

	/**
	 * Gets the setJade_MAS_RunContext context.
	 *
	 * @return the setJade_MAS_RunContext context
	 */
	public JADE_MAS_RunContext getJade_MAS_RunContext() {
		return jade_MAS_RunContext;
	}

	/**
	 * Sets the setJade_MAS_RunContext context.
	 *
	 * @param jade_MAS_RunContext
	 *            the new setJade_MAS_RunContext context
	 */
	public void setJade_MAS_RunContext(final JADE_MAS_RunContext jade_MAS_RunContext) {
		this.jade_MAS_RunContext = jade_MAS_RunContext;
	}

	/*
	 * Setup tasks run once
	 *
	 * @see jade.core.Agent#setup()
	 */
	/*
	 * (non-Javadoc)
	 *
	 * @see jade.core.Agent#setup()
	 */
	@Override
	protected void setup() {

		// Printout a welcome message
		System.out.println("JADE Controller Agent " + getAID().getName() + " is ready.");

		final Object[] args = getArguments();
		if (args != null && args.length > 0) {
			frameworkConfigurationFileName = (String) args[0];
			System.out
			.println("[JADE Controller Agent] CABSF JADE Controller Agent Configuration file "
					+ frameworkConfigurationFileName);

			jade_MAS_AdapterAPI = JADE_MAS_AdapterAPI.getInstance();
			jade_MAS_RunGroupContext = null;
			try {
				jade_MAS_RunGroupContext = jade_MAS_AdapterAPI
						.initializeAPI(frameworkConfigurationFileName);
			} catch (final IOException e) {
				// FIXME: Remove all of the agents/shutdown the MAS?
				System.out
				.println("[JADE Controller Agent] Error in initializing the JADE Controller Agent");
				doDelete();
				e.printStackTrace();
			}

			expectedNumOfAgents = jade_MAS_RunGroupContext.getDistSysRunGroupContext()
					.getRunGroupConfiguration().getNumberOfDistributedAutonomousAgents();

			// Register the JADE Controller agent in the yellow pages for the
			// distributed JADE agents to find
			final DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName(getAID());
			final ServiceDescription sd = new ServiceDescription();
			sd.setType("jade-CABSF-agents");
			sd.setName("jade-controller-agent");
			dfd.addServices(sd);
			try {
				DFService.register(this, dfd);
			} catch (final FIPAException fe) {
				// FIXME: Remove all of the agents/shutdown the MAS?
				System.out.println("[JADE Controller Agent]"
						+ " Error registering this JADE agent in the yellow pages");
				doDelete(); // Cleanup and remove this agent from the MAS.
				fe.printStackTrace();
			}

			setupNewSimulationRun();

			addBehaviour(new JadeControllerAgentServer());

		} else {
			// Make the agent terminate
			System.out
			.println("[JADE Controller Agent] The JADE Controller Agent configuration file must be provided as an argument");
			doDelete();
		}

	}

	/**
	 * Setup new simulation run.
	 */
	private void setupNewSimulationRun() {
		final DFAgentDescription template = new DFAgentDescription();
		final ServiceDescription sd = new ServiceDescription();
		DFAgentDescription[] result = null;
		sd.setType("jade-CABSF-agents");
		sd.setName("non-admin-agents");
		template.addServices(sd);

		System.out.println("[JADE Controller Agent] Looking for " + expectedNumOfAgents
				+ " total JADE agents.");

		// FIXME: Handle JADE agents that do not have representations in Repast, based on
		// the configuration.
		// Update the list of JADE agents
		while (expectedNumOfAgents != mappedJadeAgents.size()) {
			try {
				result = DFService.search(this, template);
				if (result.length > expectedNumOfAgents) {
					System.out
					.println("[JADE Controller Agent] Expected "
							+ String.valueOf(expectedNumOfAgents)
							+ " agents based on the configuration, but there were actually "
							+ String.valueOf(result.length)
							+ " JADE agents found. This functionality is not yet supported.  Terminating.");
					// FIXME: Remove all of the agents/shutdown the MAS?
					doDelete();
					System.exit(1);
				}

				// mappedJadeAgents = new HashSet<NativeDistributedAutonomousAgent>();
				System.out.println("[JADE Controller Agent] Found "
						+ String.valueOf(mappedJadeAgents.size())
						+ " total agents so far out of "
						+ String.valueOf(expectedNumOfAgents) + " expected.");
				if (expectedNumOfAgents != mappedJadeAgents.size()) {
					System.out
					.println("[JADE Controller Agent] Will retry looking for all expected agents.");
				}
				Thread.sleep(1000);
			} catch (final FIPAException fe) {
				// FIXME: Remove all of the agents/shutdown the MAS?
				System.out
				.println("[JADE Controller Agent] FIPA error in finding the distributed JADE agents.  Terminating.");
				doDelete();
				fe.printStackTrace();
			} catch (final InterruptedException e) {
				// FIXME: Remove all of the agents/shutdown the MAS?
				System.out
				.println("[JADE Controller Agent] Thread interrupted. Terminating.");
				doDelete();
				e.printStackTrace();
			}

			// Create a CabsfDistributedJADEagentWrapper concrete object for
			// each JADE agent. The purpose is to hold both the JADE AID and
			// CABSF-specific identifiers in a single object.
			// TODO: Dynamically handle naming
			// :DistributedSystemAutonomousAgent1,
			// DistributedSystemAutonomousAgent1MODEL
			// FIXME: Number of JADE agents exceed expected number.
			// FIXME: Agent names don't match
			final StringBuilder strb = new StringBuilder();
			strb.append("[JADE Controller Agent] All agents found: "
					+ String.valueOf(result.length)
					+ " agents.  Local name up to the the '@', complete name printed: "
					+ "\r\n");
			for (int i = 0; i < result.length; ++i) {
				String.valueOf(i);
				// TODO: Get Model Name from the configuration file?
				final CabsfDistributedJADEagentWrapper agent = new CabsfDistributedJADEagentWrapper(
						result[i].getName(), "DistSys1", result[i].getName()
						.getLocalName(), result[i].getName().getLocalName()
						+ "MODEL", "MODELNAME_PLACEHOLDER", this);
				mappedJadeAgents.add(agent);
				strb.append(result[i].getName().getName() + "\r\n");
			}
			System.out.println(strb);
		}

		final JADE_MAS_RunContext jade_MAS_RunContext = jade_MAS_AdapterAPI
				.initializeSimulationRun(new NativeJADEMockContext(),
						jade_MAS_RunGroupContext, null, mappedJadeAgents);
		setJade_MAS_RunContext(jade_MAS_RunContext);

		// Get the message from the simulation engine, send it on to the
		// appropriate distributed JADE agent.
		final FRAMEWORK_COMMAND fc = getJade_MAS_RunContext()
				.waitForAndProcessSimulationEngineMessageAfterHandshake();
		if (fc == FRAMEWORK_COMMAND.STOP_SIMULATION) {
			System.out
			.println("[JADE Controller Agent] Received Stop Simulation Command from Simulation Engine.  Listening for new simulation run");
			setupNewSimulationRun();
		}
	}
}
