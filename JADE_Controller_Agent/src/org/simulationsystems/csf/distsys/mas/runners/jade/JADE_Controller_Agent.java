package org.simulationsystems.csf.distsys.mas.runners.jade;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.jdom2.Element;
import org.simulationsystems.csf.common.csfmodel.SYSTEM_TYPE;
import org.simulationsystems.csf.common.csfmodel.csfexceptions.CsfCheckedException;
import org.simulationsystems.csf.common.csfmodel.csfexceptions.CsfInitializationRuntimeException;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FRAMEWORK_COMMAND;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessageImpl;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.STATUS;
import org.simulationsystems.csf.distsys.adapters.jade.api.JADE_MAS_AdapterAPI;
import org.simulationsystems.csf.distsys.adapters.jade.api.JADE_MAS_RunContext;
import org.simulationsystems.csf.distsys.adapters.jade.api.JADE_MAS_RunGroupContext;
import org.simulationsystems.csf.distsys.adapters.jade.api.nativeagents.CsfDistributedJADEagentWrapper;
import org.simulationsystems.csf.distsys.adapters.jade.api.nativeagents.NativeDistributedAutonomousAgent;
import org.simulationsystems.csf.distsys.adapters.jade.api.nativeagents.NativeJADEMockContext;
import org.simulationsystems.csf.distsys.core.api.distributedautonomousagents.DistributedAgentModel;
import org.simulationsystems.csf.distsys.core.api.distributedautonomousagents.DistributedAgentsManager;
import org.simulationsystems.csf.distsys.core.api.distributedautonomousagents.DistributedAutonomousAgent;

public class JADE_Controller_Agent extends jade.core.Agent {
	private static JADE_MAS_RunGroupContext jade_MAS_RunGroupContext;
	static private JADE_MAS_AdapterAPI jade_MAS_AdapterAPI;
	private JADE_MAS_RunContext jade_MAS_RunContext;
	String frameworkConfigurationFileName = null;

	boolean newSimulationRun = true;

	private Set<NativeDistributedAutonomousAgent> jadeAgents = new HashSet<NativeDistributedAutonomousAgent>();

	// TODO: Make this configurable
	private int numberOfAgents;

	public JADE_MAS_AdapterAPI getJade_MAS_AdapterAPI() {
		return jade_MAS_AdapterAPI;
	}

	// Put agent initializations here
	protected void setup() {

		// Printout a welcome message
		System.out.println("JADE Controller Agent " + getAID().getName() + " is ready.");

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
				numberOfAgents = 2;

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

			// Register the JADE Controller agent in the yellow pages for the distributed
			// JADE agents to find
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

	/*
	 * private static Set<NativeDistributedAutonomousAgent>
	 * getInitialSetOfNativeJADEagents() { Set<NativeDistributedAutonomousAgent> st = new
	 * HashSet<NativeDistributedAutonomousAgent>();
	 * 
	 * NativeDistributedAutonomousAgent nativeDistributedAutonomousAgent = new
	 * MockHumanJADE_Agent( "DistSys1", "DistributedSystemAutonomousAgent1",
	 * "DistributedSystemAutonomousAgent1MODEL", "Human");
	 * st.add(nativeDistributedAutonomousAgent); nativeDistributedAutonomousAgent = new
	 * MockHumanJADE_Agent("DistSys1", "DistributedSystemAutonomousAgent2",
	 * "DistributedSystemAutonomousAgent2MODEL", "Human");
	 * st.add(nativeDistributedAutonomousAgent); nativeDistributedAutonomousAgent = new
	 * MockHumanJADE_Agent("DistSys1", "DistributedSystemAutonomousAgent3",
	 * "DistributedSystemAutonomousAgent3MODEL", "Human");
	 * st.add(nativeDistributedAutonomousAgent); nativeDistributedAutonomousAgent = new
	 * MockHumanJADE_Agent("DistSys1", "DistributedSystemAutonomousAgent4",
	 * "DistributedSystemAutonomousAgent4MODEL", "Human");
	 * st.add(nativeDistributedAutonomousAgent); nativeDistributedAutonomousAgent = new
	 * MockHumanJADE_Agent("DistSys1", "DistributedSystemAutonomousAgent5",
	 * "DistributedSystemAutonomousAgent5MODEL", "Human");
	 * st.add(nativeDistributedAutonomousAgent); nativeDistributedAutonomousAgent = new
	 * MockHumanJADE_Agent("DistSys1", "DistributedSystemAutonomousAgent6",
	 * "DistributedSystemAutonomousAgent6MODEL", "Human");
	 * st.add(nativeDistributedAutonomousAgent); return st; }
	 */
	public JADE_MAS_RunContext getJade_MAS_RunContext() {
		return jade_MAS_RunContext;
	}

	public void setJade_MAS_RunContext(JADE_MAS_RunContext jade_MAS_RunContext) {
		this.jade_MAS_RunContext = jade_MAS_RunContext;
	}

	private void setupNewSimulationRun() {
		// Update the list of JADE agents
		while (numberOfAgents != jadeAgents.size()) {
			System.out.println("[JADE Controller Agent] Looking for JADE agents: "
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

				// Create a CsfDistributedJADEagentWrapper concrete object for each JADE
				// agent
				// THe purpose is to hold both the JADE AID and CSF-specific identifiers
				// in a single object.
				// DistributedSystemAutonomousAgent1,
				// DistributedSystemAutonomousAgent1MODEL
				for (int i = 0; i < result.length; ++i) {
					String num = String.valueOf(i);
					CsfDistributedJADEagentWrapper agent = new CsfDistributedJADEagentWrapper(
							result[i].getName(), "DistSys1", result[i].getName()
									.getLocalName(), result[i].getName().getLocalName()
									+ "MODEL", "Human", this);
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

		// Get the message from the simulation engine, send it on to the appropriate
		// distributed JADE agent.
		FRAMEWORK_COMMAND fc = getJade_MAS_RunContext()
				.waitForAndProcessSimulationEngineMessageAfterHandshake();
		if (fc == FRAMEWORK_COMMAND.STOP_SIMULATION) {
			System.out
					.println("[JADE Controller Agent] Received Stop Simulation Command from Simulation Engine.  Listening for new simulation run");
			setupNewSimulationRun();
		}
	}

	private class JadeControllerAgentServer extends CyclicBehaviour {
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage aclMsg = myAgent.receive(mt);
			if (aclMsg != null) {
				// Message matching the template has come in.
				String msgStr = aclMsg.getContent();

				FrameworkMessage message = null;
				try {
					message = new FrameworkMessageImpl(SYSTEM_TYPE.DISTRIBUTED_SYSTEM,
							SYSTEM_TYPE.SIMULATION_ENGINE, msgStr);
				} catch (CsfCheckedException e) {
					// FIXME: Remove all of the agents/shutdown the MAS?
					System.out
							.println("[JADE Controller Agent] Error converting message from a distributed JADE agent to the JADE Controller Agent: "
									+ msgStr);
					doDelete();
					e.printStackTrace();
				}
/*				List<String> location = message.getSelfLocation(message
						.getNextDistributedAutonomousAgent(message.getDocument(), null),
						message);*/

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
				block();
			}

		}
	} // End of inner class
}
