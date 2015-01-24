package jzombies;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.jdom2.Element;
import org.simulationsystems.csf.common.csfmodel.SYSTEM_TYPE;
import org.simulationsystems.csf.common.csfmodel.csfexceptions.CsfCheckedException;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessageImpl;
import org.simulationsystems.csf.common.internal.messaging.xml.XMLUtilities;
import org.simulationsystems.csf.distsys.adapters.jade.api.JADE_MAS_AgentContext;
import org.simulationsystems.csf.distsys.adapters.jade.api.nativeagents.CsfDistributedJADEagentWrapper;
import org.simulationsystems.csf.distsys.adapters.jade.api.nativeagents.NativeDistributedAutonomousAgent;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jzombies.JZombies_Csf;

public class HumanJADE extends Agent {
	private String logPrefix;
	private JADE_MAS_AgentContext jade_MAS_AgentContext;
	private JZombies_Csf jzombies_CSF;
	private String distributedAutonomousAgentID;
	private String distributedAutonomousAgentModelID;
	private AID jadeControllerAgent;

	protected void setup() {
		logPrefix = "[HumanJADE " + getAID().getName() + "]";
		addBehaviour(new HumanJADE_Server());

		// The Common Simulation Framework (CSF) context object specific to CSF JADE
		// agents. Gives the user access to many convenience methods for dealing with XML
		// messages coming from the simulation engine/runtime such as Repast Simphony.
		jade_MAS_AgentContext = new JADE_MAS_AgentContext();
		// This class contains convenience methods specific to this one simulation.
		jzombies_CSF = new JZombies_Csf(jade_MAS_AgentContext);
		//TODO: Support global JADE naming
		// The mapping between the JADE agent and Repast agent actually happens on the
		// "distributed autonomous agent model" level. So a JADE agent can theoretically
		// in the future run several "agent models". For now only 1 to 1 is supported,
		// however, there model must be identified separately.
		//TODO: Support global JADE naming
		distributedAutonomousAgentModelID = getAID().getLocalName() + "MODEL";

		// Register this agent as one of the distributed JADE agents in the simulation in
		// the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("jade-csf-agents");
		sd.setName("non-admin-agents"); //All JADE agents other than the JADE Controller Agent
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			System.out
					.println(logPrefix
							+ " Error registering this JADE agent in the yellow pages");
			doDelete(); // Cleanup and remove this agent from the MAS.
			fe.printStackTrace();
		}
		
		//Find the JADE Controller Agent
		System.out.println(logPrefix+" Looking for the JADE Controller Agent");
		DFAgentDescription template = new DFAgentDescription();
		sd = new ServiceDescription();
		sd.setType("jade-csf-agents");
		sd.setName("jade-controller-agent");
		template.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(this, template);
			assert (result.length == 1);
			System.out.println(logPrefix+" Found the JADE Controller Agent: "+result[0].getName().getName());
			jadeControllerAgent = result[0].getName();
			
		} catch (FIPAException fe) {
			System.out
					.println("[JADE Controller Agent] FIPA error in finding the JADE Controller Agent.  Terminating.");
			doDelete();
			fe.printStackTrace();
		}
	}

	/*
	 * Inner class that specifies the behavior for this agent
	 * 
	 * This behavior is constantly re-run
	 */
	private class HumanJADE_Server extends CyclicBehaviour {
		// Called every time a message comes into the queue to this agent. This is done
		// sequentially due to the fact that a JADE agent only runs a single thread.
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			// Check the queue. null means no message has come in matching the template.
			ACLMessage aclMsg = myAgent.receive(mt);

			if (aclMsg != null) {
				// Message matching the template has come in. Get the XML String.
				String msgStr = aclMsg.getContent();

				FrameworkMessage msg = null;
				// Convert the message content from an XML string to a (CSF)
				// FrameworkMessage to gain access to convenience methods.
				try {
					msg = new FrameworkMessageImpl(SYSTEM_TYPE.SIMULATION_ENGINE,
							SYSTEM_TYPE.DISTRIBUTED_SYSTEM, msgStr);
				} catch (CsfCheckedException e) {
					System.out
							.println(logPrefix
									+ " Error parsing message from the JADE Controller Agent to this agent.  Message: "
									+ msgStr);
					doDelete(); // Cleanup and remove this agent from the MAS.
					e.printStackTrace();
				}

				System.out.println(logPrefix + " Received Message: " + aclMsg.getConversationId() + " " +aclMsg.getReplyWith()+ " Message:"+ msgStr);

				// Get the current grid location of this agent from the simulation
				// engine/runtime. The result is a list (X, Y)
				List<String> selfPoint = msg.getSelfLocation(msg);
				assert (selfPoint.size() == 2);
				for (int i = 0; i < selfPoint.size(); i++) {
					System.out.println(logPrefix + " Self Location: " + String.valueOf(i)
							+ " : " + String.valueOf(selfPoint.get(i)));
				}

				// Get the part of the XML message dealing with this JADE agent
				// (distributed autonomous agent)
				Element distributedAutonomousAgentElement = msg
						.getNextDistributedAutonomousAgent(msg.getDocument(), null);

				// Uses a convenience method that is specific to the JZombies simulation
				// to get the location of the zombies. This convience method is located in
				// the Repast project, hence why we need a dependency on that project.
				// There is no direct use of Repast code from this JADE agent.
				List<String> pointWithLeastZombiesPoint = jzombies_CSF
						.getPointWithLeastZombies(distributedAutonomousAgentElement, msg);

				for (int i = 0; i < pointWithLeastZombiesPoint.size(); i++) {
					System.out.println(logPrefix + " Received Zombie location "
							+ String.valueOf(i) + " : "
							+ String.valueOf(pointWithLeastZombiesPoint.get(i)));
				}

				List<String> pointToMoveTo = chooseMoveTowardsLocation(selfPoint,
						pointWithLeastZombiesPoint);

				// Send the decision on where to move to
				msg = jzombies_CSF.convertMoveToPointToFrameworkMessage(pointToMoveTo,
						distributedAutonomousAgentID, distributedAutonomousAgentModelID);
				System.out.println(logPrefix
						+ " Sending move decision to the JADE Controller Agent: "+ aclMsg.getConversationId() + " Message:"
						+ XMLUtilities.convertDocumentToXMLString(msg.getDocument()
								.getRootElement(), true));
	
				//Send message to the JADE Controller Agent
				ACLMessage response =  aclMsg.createReply();
				response.setPerformative(ACLMessage.INFORM);
				response.setContent(msg.toPrettyPrintedXMLString());
				myAgent.send(response);

			} else {
				// This behavior is de-activated until a new message comes into the queue.
				block();
			}

		}

		private List<String> chooseMoveTowardsLocation(List<String> selfPoint,
				List<String> pointWithLeastZombiesPoint) {

			return pointWithLeastZombiesPoint;

		}
	}
}
