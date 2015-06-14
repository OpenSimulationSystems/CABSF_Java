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

import org.opensimulationsystems.cabsf.common.csfmodel.SYSTEM_TYPE;
import org.opensimulationsystems.cabsf.common.csfmodel.csfexceptions.CsfCheckedException;
import org.opensimulationsystems.cabsf.common.csfmodel.messaging.messages.FRAMEWORK_COMMAND;
import org.opensimulationsystems.cabsf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.opensimulationsystems.cabsf.common.csfmodel.messaging.messages.FrameworkMessageImpl;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.JADE_MAS_AdapterAPI;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.JADE_MAS_RunContext;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.JADE_MAS_RunGroupContext;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.nativeagents.CsfDistributedJADEagentWrapper;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.nativeagents.NativeDistributedAutonomousAgent;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.nativeagents.NativeJADEMockContext;

/**
 * The JADE agent implementing the CSF JADE MAS Adapter API to enable JADE
 * agents to communicate with ABMS systems such as Repast Simphony.
 *
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class JADE_Controller_Agent extends jade.core.Agent {

	/**
	 * The Behaviour class that specifies the specific actions that the JADE
	 * Controller Agent takes when receiving a new message from another agent.
	 * This functionality is described in the JADE documentation.
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
			MessageTemplate mt = MessageTemplate
					.MatchPerformative(ACLMessage.INFORM);
			ACLMessage aclMsg = myAgent.receive(mt);
			if (aclMsg != null) {
				// Message matching the template has come in.
				String msgStr = aclMsg.getContent();

				FrameworkMessage message = null;
				try {
					message = new FrameworkMessageImpl(
							SYSTEM_TYPE.DISTRIBUTED_SYSTEM,
							SYSTEM_TYPE.SIMULATION_ENGINE, msgStr);
				} catch (CsfCheckedException e) {
					// FIXME: Remove all of the agents/shutdown the MAS?
					System.out
					.println("[JADE Controller Agent] Error converting message from a distributed JADE agent to the JADE Controller Agent: "
							+ msgStr);
					doDelete();
					e.printStackTrace();
				}

				System.out
				.println("[JADE Controller Agent] Received the distributed autonomous agent (model) decision. Forwarding to the simulation engine (agent)");

				jade_MAS_RunContext.messageSimulationEngine(message,
						jade_MAS_RunContext.getDistSysRunContext());

				FRAMEWORK_COMMAND fc = getJade_MAS_RunContext()
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
	private Set<NativeDistributedAutonomousAgent> jadeAgents = new HashSet<NativeDistributedAutonomousAgent>();

	// TODO: Make this configurable
	/** The number of agents. */
	private int numberOfAgents;

	/**
	 * Gets the jade_ MAS_ adapter api.
	 *
	 * @return the jade_ MAS_ adapter api
	 */
	public JADE_MAS_AdapterAPI getJade_MAS_AdapterAPI() {
		return jade_MAS_AdapterAPI;
	}

	// This code was used when testing using a mock in place of JADE agents.
	// This is being left for now commented out as the mock code can be migrated
	// to a generic Java distributed system adapter implementation.
	/*
	 * private static Set<NativeDistributedAutonomousAgent>
	 * getInitialSetOfNativeJADEagents() { Set<NativeDistributedAutonomousAgent>
	 * st = new HashSet<NativeDistributedAutonomousAgent>();
	 * 
	 * NativeDistributedAutonomousAgent nativeDistributedAutonomousAgent = new
	 * MockHumanJADE_Agent( "DistSys1", "DistributedSystemAutonomousAgent1",
	 * "DistributedSystemAutonomousAgent1MODEL", "Human");
	 * st.add(nativeDistributedAutonomousAgent);
	 * nativeDistributedAutonomousAgent = new MockHumanJADE_Agent("DistSys1",
	 * "DistributedSystemAutonomousAgent2",
	 * "DistributedSystemAutonomousAgent2MODEL", "Human");
	 * st.add(nativeDistributedAutonomousAgent);
	 * nativeDistributedAutonomousAgent = new MockHumanJADE_Agent("DistSys1",
	 * "DistributedSystemAutonomousAgent3",
	 * "DistributedSystemAutonomousAgent3MODEL", "Human");
	 * st.add(nativeDistributedAutonomousAgent);
	 * nativeDistributedAutonomousAgent = new MockHumanJADE_Agent("DistSys1",
	 * "DistributedSystemAutonomousAgent4",
	 * "DistributedSystemAutonomousAgent4MODEL", "Human");
	 * st.add(nativeDistributedAutonomousAgent);
	 * nativeDistributedAutonomousAgent = new MockHumanJADE_Agent("DistSys1",
	 * "DistributedSystemAutonomousAgent5",
	 * "DistributedSystemAutonomousAgent5MODEL", "Human");
	 * st.add(nativeDistributedAutonomousAgent);
	 * nativeDistributedAutonomousAgent = new MockHumanJADE_Agent("DistSys1",
	 * "DistributedSystemAutonomousAgent6",
	 * "DistributedSystemAutonomousAgent6MODEL", "Human");
	 * st.add(nativeDistributedAutonomousAgent); return st; }
	 */
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
	public void setJade_MAS_RunContext(JADE_MAS_RunContext jade_MAS_RunContext) {
		this.jade_MAS_RunContext = jade_MAS_RunContext;
	}

	// Put agent initializations here
	/*
	 * (non-Javadoc)
	 * 
	 * @see jade.core.Agent#setup()
	 */
	@Override
	protected void setup() {

		// Printout a welcome message
		System.out.println("JADE Controller Agent " + getAID().getName()
				+ " is ready.");

		// TODO: Add Validation of CSF configuration file
		// One-time setup
		Object[] args = getArguments();
		if (args != null && args.length > 0) {
			frameworkConfigurationFileName = (String) args[0];
			System.out
			.println("[JADE Controller Agent] JADE Controller Agent Configuration file "
					+ frameworkConfigurationFileName);

			// JZombies or Prisoner's Dilemma
			if (frameworkConfigurationFileName.equals("PLACEHOLDERCONFIGFILE"))
				numberOfAgents = 6;
			else
				numberOfAgents = 8;

			jade_MAS_AdapterAPI = JADE_MAS_AdapterAPI.getInstance();
			jade_MAS_RunGroupContext = null;

			try {
				jade_MAS_RunGroupContext = jade_MAS_AdapterAPI
						.initializeAPI(frameworkConfigurationFileName);
			} catch (IOException e) {
				// FIXME: Remove all of the agents/shutdown the MAS?
				System.out
				.println("[JADE Controller Agent] Error in initializing the JADE Controller Agent");
				doDelete();
				e.printStackTrace();
			}

			// Register the JADE Controller agent in the yellow pages for the
			// distributed JADE agents to find
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName(getAID());
			ServiceDescription sd = new ServiceDescription();
			sd.setType("jade-csf-agents");
			sd.setName("jade-controller-agent");
			dfd.addServices(sd);
			try {
				DFService.register(this, dfd);
			} catch (FIPAException fe) {
				// FIXME: Remove all of the agents/shutdown the MAS?
				System.out
				.println("[JADE Controller Agent]"
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
		// Update the list of JADE agents
		while (numberOfAgents != jadeAgents.size()) {
			System.out
			.println("[JADE Controller Agent] Looking for JADE agents: "
					+ numberOfAgents);
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("jade-csf-agents");
			sd.setName("non-admin-agents");
			template.addServices(sd);
			try {
				DFAgentDescription[] result = DFService.search(this, template);
				System.out
				.println("[JADE Controller Agent] Found the following JADE agents ("
						+ String.valueOf(result.length) + "):");
				jadeAgents = new HashSet<NativeDistributedAutonomousAgent>();

				// Create a CsfDistributedJADEagentWrapper concrete object for
				// each JADE agent. The purpose is to hold both the JADE AID and
				// CSF-specific identifiers in a single object.
				// TODO: Dynamically handle naming
				// :DistributedSystemAutonomousAgent1,
				// DistributedSystemAutonomousAgent1MODEL
				for (int i = 0; i < result.length; ++i) {
					String.valueOf(i);
					CsfDistributedJADEagentWrapper agent = new CsfDistributedJADEagentWrapper(
							result[i].getName(), "DistSys1", result[i]
									.getName().getLocalName(), result[i]
											.getName().getLocalName() + "MODEL",
											"Human", this);
					jadeAgents.add(agent);
					System.out.println("   " + result[i].getName().getName());
				}
			} catch (FIPAException fe) {
				// FIXME: Remove all of the agents/shutdown the MAS?
				System.out
				.println("[JADE Controller Agent] FIPA error in finding the distributed JADE agents.  Terminating.");
				doDelete();
				fe.printStackTrace();
			}
		}

		JADE_MAS_RunContext jade_MAS_RunContext = jade_MAS_AdapterAPI
				.initializeSimulationRun(new NativeJADEMockContext(),
						jade_MAS_RunGroupContext, null, jadeAgents);
		setJade_MAS_RunContext(jade_MAS_RunContext);

		// Get the message from the simulation engine, send it on to the
		// appropriate distributed JADE agent.
		FRAMEWORK_COMMAND fc = getJade_MAS_RunContext()
				.waitForAndProcessSimulationEngineMessageAfterHandshake();
		if (fc == FRAMEWORK_COMMAND.STOP_SIMULATION) {
			System.out
			.println("[JADE Controller Agent] Received Stop Simulation Command from Simulation Engine.  Listening for new simulation run");
			setupNewSimulationRun();
		}
	}
}
