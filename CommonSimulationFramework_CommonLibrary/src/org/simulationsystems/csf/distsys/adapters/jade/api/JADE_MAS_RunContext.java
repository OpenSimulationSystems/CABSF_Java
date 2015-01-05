package org.simulationsystems.csf.distsys.adapters.jade.api;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.jdom2.Document;
import org.jdom2.Element;
import org.simulationsystems.csf.common.csfmodel.SYSTEM_TYPE;
import org.simulationsystems.csf.common.csfmodel.SimulationRunGroup;
import org.simulationsystems.csf.common.csfmodel.csfexceptions.CsfInitializationRuntimeException;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FRAMEWORK_COMMAND;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.internal.messaging.xml.XMLUtilities;
import org.simulationsystems.csf.distsys.adapters.jade.api.nativeagents.NativeDistributedAutonomousAgent;
import org.simulationsystems.csf.distsys.core.api.DistSysRunContext;
import org.simulationsystems.csf.distsys.core.api.configuration.DistSysRunGroupConfiguration;
import org.simulationsystems.csf.distsys.core.api.distributedautonomousagents.DistributedAgentModel;
import org.simulationsystems.csf.distsys.core.api.distributedautonomousagents.DistributedAgentsManager;
import org.simulationsystems.csf.distsys.core.api.distributedautonomousagents.DistributedAutonomousAgent;
import org.simulationsystems.csf.sim.core.api.SimulationRunContext;
import org.simulationsystems.csf.sim.core.api.distributedsystems.SimulationDistributedSystemManager;

import repast.simphony.context.Context;

/*
 * Provides the context for the Common Simulation Framework. This Simulation-Toolkit-specific
 * context mirrors the generic DistSysRunContext provided by the Common Framework API. It enables
 * API users to get native Simulation-Toolkit objects instead of generic "Object"s. This aids the
 * API client at compile time. The simulation context is created for the entire simulation run
 * group, unlike in Repast where the simulation context exists per simulation run. Adapter
 * developers should first instantiate DistSysRunContext, before instantiating a
 * Simulation-Toolkit-specific Context such as this class.
 */
public class JADE_MAS_RunContext {
	private DistSysRunContext distSysRunContext;
	Object jade_ContextForThisRun;
	private DistributedAgentsManager dam;
	private JadeController jadeControllerAgent;

	public void setDam(DistributedAgentsManager dam) {
		this.dam = dam;
	}

	public DistSysRunContext getDistSysRunContext() {
		return distSysRunContext;
	}

	public Element getCachedAgentModelActorTemplate() {
		return this.getDistSysRunContext().getDistSysRunGroupContext()
				.getCachedAgentModelActorTemplate();
	}

	public Element getCachedLocationTemplate() {
		return this.getDistSysRunContext().getDistSysRunGroupContext()
				.getCachedLocationTemplate();
	}

	/*
	 * Use the other constructor
	 */
	@SuppressWarnings("unused")
	private JADE_MAS_RunContext() {

	}

	/*
	 * Uses the decorator pattern to set up a custom JADE MAS RunContext using the general
	 * DistSystRunContext object.
	 */
	public JADE_MAS_RunContext(DistSysRunContext distSysRunContext) {
		this.distSysRunContext = distSysRunContext;

		// TODO: Make initialized based on configuration. For now, hard code one
		// distributed system.
	}

	public DistSysRunGroupConfiguration getDistSysRunGroupConfiguration() {
		return distSysRunContext.getDistributedSystemRunGroupConfiguration();
	}

	public SimulationRunGroup getSimulationRunGroup() {
		return distSysRunContext.getSimulationRunGroup();
	}

	/*
	 * 
	 */
	/*
	 * LOW: Add the ability to support many simultaneous "Context"s
	 */
	public void setJadeContextForThisRun(Object jade_ContextForThisRun) {
		this.jade_ContextForThisRun = jade_ContextForThisRun;

	}

	public Object getCurrentJADE_Context() {
		return jade_ContextForThisRun;
	}

	/*
	 * Convenience method to get the cached messgae exchange template
	 */
	public Document getCachedMessageExchangeTemplate() {
		return this.getDistSysRunContext().getDistSysRunGroupContext()
				.getBlankCachedMessageExchangeTemplate();
	}

	public void messageSimulationEngine(FrameworkMessage frameworkMessage,
			DistSysRunContext distSysRunContext) {
		distSysRunContext.messageSimulationEngine(frameworkMessage);
	}

	public FrameworkMessage listenForMessageFromSimulationEngine() {
		return getDistSysRunContext().listenForMessageFromSimulationEngine();

	}

	public void closeInterface(DistSysRunContext distSysRunContext) {
		distSysRunContext.closeInterface();
	}

	// FIXME: Need this?
	public FrameworkMessage requestEnvironmentInformation() {
		getDistSysRunContext().requestEnvironmentInformation();
		return null;

	}

	public void initializeAgentMappings() {
		// TODO Auto-generated method stub

	}

	public FRAMEWORK_COMMAND waitForAndProcessSimulationEngineMessageAfterHandshake() {
		// Now listen for the messages from the simulation engine
		FrameworkMessage fm = listenForMessageFromSimulationEngine();
		List<Element> distributedAutonomousAgentElements = fm
				.getDistributedAutonomousAgentElements(fm.getDocument());

		// check whether to terminate the simulation
		// Listen for START_SIMULATION command from the simulation engine
		FRAMEWORK_COMMAND fc = fm.getFrameworkToDistributedSystemCommand();
		// TODO: Better error handling. Send a message back to the simulation engine that
		// this distributed system is terminating

		if (fc != null && fc.equals(FRAMEWORK_COMMAND.STOP_SIMULATION)) {
			return FRAMEWORK_COMMAND.STOP_SIMULATION;
		}

		// TODO: better validation
		assert (distributedAutonomousAgentElements.size() != 0);
		// dam.getDistributedAutonomousAgentIDStoDistributedAutonomousAgents().keySet().toString();
		for (Element distributedAutonomousAgentElement : distributedAutonomousAgentElements) {
			String distributedAutonomousAgentID = fm
					.getDistributedAutonomousAgentID(distributedAutonomousAgentElement);
			DistributedAutonomousAgent distAutAgent = dam
					.getDistributedAutonomousAgent(distributedAutonomousAgentID);
			// TODO: better validation
			assert (distAutAgent != null);
			// XMLUtilities.convertDocumentToXMLString(distributedAutonomousAgentElement,
			// true);
			List<Element> agentModelElements = fm
					.getAgentModels(distributedAutonomousAgentElement); // TODO: better
																		// validation
			assert (agentModelElements.size() != 0);

			System.out
					.println("[JADE Controller Agent] Received message from the simulation engine to start the simulation: "
							+ fm.transformToCommonMessagingXMLString(true));

			// Agent Model Assertions. We don't actually message the modesl from here,
			// only the distributed autonomous agents
			for (Element agentModelElement : agentModelElements) {
				String agentModelIDfromMessage = fm
						.getFirstAgentModelActorAgentModelID(agentModelElement);
				DistributedAgentModel distAgentModel = distAutAgent
						.getDistributedAgentModelIDStoAgentModels().get(
								agentModelIDfromMessage);
				// TODO: add better validation
				assert (distAgentModel != null && agentModelIDfromMessage
						.equals(distAgentModel.getDistributedAgentModelID()));

			}

			// FIXME: Need to keep this case on the JADE API side, everything else in
			// the main DistaSys API
			NativeDistributedAutonomousAgent nativeDistributedAutonomousAgent = (NativeDistributedAutonomousAgent) distAutAgent
					.getNativeDistributedAutonomousAgent();
			assert (nativeDistributedAutonomousAgent != null);

			fm = getDistSysRunContext()
					.getDistSysRunGroupContext()
					.convertDocumentSentToDistributedAutonomousAgentToFrameworkMessage(
							distributedAutonomousAgentElement,
							distAutAgent.getDistributedAutonomousAgentID(),
							SYSTEM_TYPE.SIMULATION_ENGINE, SYSTEM_TYPE.DISTRIBUTED_SYSTEM);

			assert (nativeDistributedAutonomousAgent.getDistributedAutonomousAgentID()
					.equals(distributedAutonomousAgentID));
			String messageID = UUID.randomUUID().toString();
			nativeDistributedAutonomousAgent.receiveMessage(fm, messageID, null,
					jadeControllerAgent);

			// At this point the distributed agent has received the message from here/the
			// controller, the distributed agent has notified the controller of its
			// decision, the controller has send the message over
			// the wire, and control has returned here.
		}

		/*
		 * if (fc == null || !fc.equals(FRAMEWORK_COMMAND.START_SIMULATION)) throw new
		 * CsfInitializationRuntimeException(
		 * "The JADE Controller Agent tried to read message from the simulation engine, but did not understand the command: "
		 * + fc.toString());
		 */

		return null;
	}

	public void setJadeControllerAgent(JadeController jadeControllerAgent) {
		this.jadeControllerAgent = jadeControllerAgent;

	}
}
