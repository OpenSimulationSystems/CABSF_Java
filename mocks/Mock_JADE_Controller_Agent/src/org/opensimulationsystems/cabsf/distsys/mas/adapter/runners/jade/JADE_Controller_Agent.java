package org.opensimulationsystems.cabsf.distsys.mas.adapter.runners.jade;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.jdom2.Element;
import org.opensimulationsystems.cabsf.common.csfmodel.SYSTEM_TYPE;
import org.opensimulationsystems.cabsf.common.csfmodel.csfexceptions.CsfInitializationRuntimeException;
import org.opensimulationsystems.cabsf.common.csfmodel.messaging.messages.FRAMEWORK_COMMAND;
import org.opensimulationsystems.cabsf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.opensimulationsystems.cabsf.common.csfmodel.messaging.messages.FrameworkMessageImpl;
import org.opensimulationsystems.cabsf.common.csfmodel.messaging.messages.STATUS;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.JADE_MAS_AdapterAPI;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.JADE_MAS_RunContext;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.JADE_MAS_RunGroupContext;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.JadeControllerMock;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.nativeagents.NativeDistributedAutonomousAgent;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.nativeagents.NativeJADEMockContext;
import org.opensimulationsystems.cabsf.distsys.core.api.distributedautonomousagents.DistributedAgentModel;
import org.opensimulationsystems.cabsf.distsys.core.api.distributedautonomousagents.DistributedAgentsManager;
import org.opensimulationsystems.cabsf.distsys.core.api.distributedautonomousagents.DistributedAutonomousAgent;
import org.opensimulationsystems.cabsf.distsys.mas.mocks.MockHumanJADE_Agent;

public class JADE_Controller_Agent implements JadeControllerMock {
	private static JADE_MAS_RunGroupContext jade_MAS_RunGroupContext;

	public JADE_MAS_AdapterAPI getJade_MAS_AdapterAPI() {
		return jade_MAS_AdapterAPI;
	}


	static private JADE_MAS_AdapterAPI jade_MAS_AdapterAPI;
	private JADE_MAS_RunContext jade_MAS_RunContext;

	public static void main(String[] args) {
		String frameworkConfigurationFileName = null;
		if (args.length >= 1)
			frameworkConfigurationFileName = args[0];
		// TODO: Add Validation of CSF configuration file
		if (frameworkConfigurationFileName == null)
			throw new CsfInitializationRuntimeException(
					"The configuration directory must be provided");

		jade_MAS_AdapterAPI = JADE_MAS_AdapterAPI.getInstance();
		jade_MAS_RunGroupContext = null;

		try {
			jade_MAS_RunGroupContext = jade_MAS_AdapterAPI
					.initializeAPI(frameworkConfigurationFileName);
		} catch (IOException e) {
			throw new CsfInitializationRuntimeException(
					"Error in initializing the JADE Controller Agent", e);
		}

		// Initialize simulation run
		// TODO: Fix the native JADE context
		JADE_Controller_Agent jade_Controller_Agent = new JADE_Controller_Agent();
		Set<NativeDistributedAutonomousAgent> st = getInitialSetOfNativeJADEagents();

		jade_Controller_Agent.listenLoop(st);

	}

	private void listenLoop(Set<NativeDistributedAutonomousAgent> st) {
		while (true) {
			try {
				JADE_MAS_RunContext jade_MAS_RunContext = jade_MAS_AdapterAPI
						.initializeSimulationRun(new NativeJADEMockContext(),
								jade_MAS_RunGroupContext, this, st);
				setJade_MAS_RunContext(jade_MAS_RunContext);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			FRAMEWORK_COMMAND fc = getJade_MAS_RunContext()
					.waitForAndProcessSimulationEngineMessageAfterHandshake();
			if (fc == FRAMEWORK_COMMAND.STOP_SIMULATION)
				System.out
						.println("[JADE Controller Agent] Simulation Run Ended. Listening for new simulation run");
		}
	}

	private static Set<NativeDistributedAutonomousAgent> getInitialSetOfNativeJADEagents() {
		Set<NativeDistributedAutonomousAgent> st = new HashSet<NativeDistributedAutonomousAgent>();

		NativeDistributedAutonomousAgent nativeDistributedAutonomousAgent = new MockHumanJADE_Agent(
				"DistSys1", "DistributedSystemAutonomousAgent1",
				"DistributedSystemAutonomousAgent1MODEL", "Human");
		st.add(nativeDistributedAutonomousAgent);
		nativeDistributedAutonomousAgent = new MockHumanJADE_Agent("DistSys1",
				"DistributedSystemAutonomousAgent2",
				"DistributedSystemAutonomousAgent2MODEL", "Human");
		st.add(nativeDistributedAutonomousAgent);
		nativeDistributedAutonomousAgent = new MockHumanJADE_Agent("DistSys1",
				"DistributedSystemAutonomousAgent3",
				"DistributedSystemAutonomousAgent3MODEL", "Human");
		st.add(nativeDistributedAutonomousAgent);
		nativeDistributedAutonomousAgent = new MockHumanJADE_Agent("DistSys1",
				"DistributedSystemAutonomousAgent4",
				"DistributedSystemAutonomousAgent4MODEL", "Human");
		st.add(nativeDistributedAutonomousAgent);
		nativeDistributedAutonomousAgent = new MockHumanJADE_Agent("DistSys1",
				"DistributedSystemAutonomousAgent5",
				"DistributedSystemAutonomousAgent5MODEL", "Human");
		st.add(nativeDistributedAutonomousAgent);
		nativeDistributedAutonomousAgent = new MockHumanJADE_Agent("DistSys1",
				"DistributedSystemAutonomousAgent6",
				"DistributedSystemAutonomousAgent6MODEL", "Human");
		st.add(nativeDistributedAutonomousAgent);
		return st;
	}

	public JADE_MAS_RunContext getJade_MAS_RunContext() {
		return jade_MAS_RunContext;
	}

	public void setJade_MAS_RunContext(JADE_MAS_RunContext jade_MAS_RunContext) {
		this.jade_MAS_RunContext = jade_MAS_RunContext;
	}

	public void receiveMessage(FrameworkMessage message, String messageID,
			String inReplyToMessageID) {
		List<String> location = message.getSelfLocationFromFirstAgentModel(
				message.getNextDistributedAutonomousAgent(message.getDocument(), null),
				message);
		assert (location.size() == 2);

		System.out
				.println("[JADE Controller Agent] Received the distributed autonomous agent (model) decision. Forwarding to the simulation engine (agent)");

		jade_MAS_RunContext.messageSimulationEngine(message,
				jade_MAS_RunContext.getDistSysRunContext());

		FRAMEWORK_COMMAND fc = getJade_MAS_RunContext()
				.waitForAndProcessSimulationEngineMessageAfterHandshake();
		if (fc == FRAMEWORK_COMMAND.STOP_SIMULATION) {
			System.out
					.println("[JADE Controller Agent] Simulation Run Ended. Listening for new simulation run");
			Set<NativeDistributedAutonomousAgent> st = getInitialSetOfNativeJADEagents();
			listenLoop(st);
		}
	}

}
