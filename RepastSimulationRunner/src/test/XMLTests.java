package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jzombies.JZombies_Csf;

import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.Filter;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.simulationsystems.csf.common.csfmodel.SYSTEM_TYPE;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FRAMEWORK_COMMAND;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessageImpl;
import org.simulationsystems.csf.common.internal.messaging.MessagingUtilities;
import org.simulationsystems.csf.common.internal.messaging.xml.XMLUtilities;
import org.simulationsystems.csf.distsys.adapters.jade.api.JADE_MAS_AdapterAPI;
import org.simulationsystems.csf.distsys.adapters.jade.api.JADE_MAS_AgentContext;
import org.simulationsystems.csf.distsys.adapters.jade.api.JADE_MAS_RunContext;
import org.simulationsystems.csf.distsys.adapters.jade.api.JADE_MAS_RunGroupContext;
import org.simulationsystems.csf.distsys.adapters.jade.api.JadeControllerMock;
import org.simulationsystems.csf.distsys.adapters.jade.api.nativeagents.NativeDistributedAutonomousAgent;
import org.simulationsystems.csf.distsys.mas.mocks.MockHumanJADE_Agent;
import org.simulationsystems.csf.sim.core.api.SimulationAPI;
import org.simulationsystems.csf.sim.core.api.SimulationRunContext;
import org.simulationsystems.csf.sim.core.api.SimulationRunGroupContext;
import org.simulationsystems.csf.sim.engines.adapters.repastS.api.RepastS_AgentContext;
import org.simulationsystems.csf.sim.engines.adapters.repastS.api.RepastS_SimulationRunGroupContext;

public class XMLTests implements JadeControllerMock {
	static private XMLTests instance = new XMLTests();

	static private SimulationRunContext simulationRunContext;
	static private SimulationRunGroupContext simulationRunGroupContext;
	static private SimulationAPI simulationAPI;
	static private String simToolNameToSetInSimulationAPI;
	private static JZombies_Csf jZombies_Csf;
	static JADE_MAS_AgentContext jade_MAS_AgentContext;
	static private RepastS_AgentContext repastS_AgentContext;

	private static JADE_MAS_RunContext jade_MAS_RunContext;

	String xmlString = "<DistributedAutonomousAgent xmlns=\"http://www.simulationsystems.org/csf/schemas/CsfMessageExchange/0.1.0\"\r\n"
			+ "	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n"
			+ "          <Name />\r\n"
			+ "          <ID>distAutAgent2</ID>\r\n"
			+ "          <AgentModels>\r\n"
			+ "            <AllAgentModels>\r\n"
			+ "              <!-- To all Agent Models in this Distributed System -->\r\n"
			+ "              <ControlMessages>\r\n"
			+ "                <!-- START_SIMULATION or END_SIMULATION -->\r\n"
			+ "                <Command />\r\n"
			+ "              </ControlMessages>\r\n"
			+ "            </AllAgentModels>\r\n"
			+ "            <!-- Message submitted to this one agent model in the parent distributed autonomous agent -->\r\n"
			+ "            <AgentModel>\r\n"
			+ "              <!-- Message about this one actor to this one agent model (parent element) under the parent distributed autonomous agent -->\r\n"
			+ "              <Actor>\r\n"
			+ "                <!-- RE: Self or another agent model -->\r\n"
			+ "                <ID>distAutAgentMode2</ID>\r\n"
			+ "                <!-- EnvironmentChanges Could be used to communicate all local environment \r\n"
			+ "									information if desired, not just changes since the last tick -->\r\n"
			+ "                <EnvironmentChanges>\r\n"
			+ "                  <CommonEnvironmentChanges>\r\n"
			+ "                    <EnvironmentChange>\r\n"
			+ "                      <!-- self = the actor above. Initial location or change -->\r\n"
			+ "                      <Location id=\"self\">\r\n"
			+ "                        <GridPointX>5</GridPointX>\r\n"
			+ "                        <GridPointY>6</GridPointY>\r\n"
			+ "                        <GridPointZ />\r\n"
			+ "                      </Location>\r\n"
			+ "                    </EnvironmentChange>\r\n"
			+ "                  </CommonEnvironmentChanges>\r\n"
			+ "                  <SimulationDefinedEnvironmentChanges>\r\n"
			+ "                    <EnvironmentChange>\r\n"
			+ "                      <!-- key/value pair suggested, format is open -->\r\n"
			+ "                      <Location id=\"\" category=\"neighborhood\" includecenter=\"true\" entitytype=\"Zombie\">\r\n"
			+ "                        <GridPointX>7</GridPointX>\r\n"
			+ "                        <GridPointY>8</GridPointY>\r\n"
			+ "                        <GridPointZ />\r\n"
			+ "                      </Location>\r\n"
			+ "                    </EnvironmentChange>\r\n"
			+ "                  </SimulationDefinedEnvironmentChanges>\r\n"
			+ "                </EnvironmentChanges>\r\n"
			+ "                <!-- These are changes in state that are not observable by other \r\n"
			+ "									agents. Only for use by this agent or to populate the global environment \r\n"
			+ "									for the simulation administrator. Prefer use of EnvironmentChanges over StateChanges \r\n"
			+ "									for changes that are at least in theory observable by other agents/agent \r\n"
			+ "									models in the simulation model. -->\r\n"
			+ "                <Actions>\r\n"
			+ "                  <!-- Common CSF Actions -->\r\n"
			+ "                  <CommonActions>\r\n"
			+ "                    <Action>\r\n"
			+ "                      <Move>\r\n"
			+ "                        <GridPointX />\r\n"
			+ "                        <GridPointY />\r\n"
			+ "                        <GridPointZ />\r\n"
			+ "                      </Move>\r\n"
			+ "                    </Action>\r\n"
			+ "                  </CommonActions>\r\n"
			+ "                  <!-- Optional, based on the simulation -->\r\n"
			+ "                  <SimulationDefinedActions>\r\n"
			+ "                    <!-- key/value pair suggested, format is open -->\r\n"
			+ "                  </SimulationDefinedActions>\r\n"
			+ "                </Actions>\r\n"
			+ "                <MessageExchange>\r\n"
			+ "                  <CommonMessages>\r\n"
			+ "                    <!-- To this agent model, regarding actor above -->\r\n"
			+ "                    <Message>\r\n"
			+ "                      <From />\r\n"
			+ "                      <!-- key/value pair here -->\r\n"
			+ "                    </Message>\r\n"
			+ "                  </CommonMessages>\r\n"
			+ "                  <SimulationDefinedMessages>\r\n"
			+ "                    <Message>\r\n"
			+ "                      <!-- key/value pair suggested, format is open -->\r\n"
			+ "                    </Message>\r\n"
			+ "                  </SimulationDefinedMessages>\r\n"
			+ "                </MessageExchange>\r\n"
			+ "                <InternalStateChanges>\r\n"
			+ "                  <CommonInternalStateChanges>\r\n"
			+ "                    <InternalStateChange>\r\n"
			+ "                      <!-- Key value pair -->\r\n"
			+ "                    </InternalStateChange>\r\n"
			+ "                    <InternalStateUpdate />\r\n"
			+ "                  </CommonInternalStateChanges>\r\n"
			+ "                  <!-- User/simulation defined common states -->\r\n"
			+ "                  <!-- Children may contain any format -->\r\n"
			+ "                  <SimulationDefinedInternalStateChanges>\r\n"
			+ "                    <InternalStateChange>\r\n"
			+ "                      <!-- key/value pair suggested, format is open -->\r\n"
			+ "                    </InternalStateChange>\r\n"
			+ "                  </SimulationDefinedInternalStateChanges>\r\n"
			+ "                </InternalStateChanges>\r\n"
			+ "              </Actor>\r\n"
			+ "            </AgentModel>\r\n"
			+ "          </AgentModels>\r\n"
			+ "        </DistributedAutonomousAgent>\r\n" + "";

	/*
	 * static private Document documentTemplateInstance = null; static private String
	 * namespaceStr =
	 * "http://www.simulationsystems.org/csf/schemas/CsfMessageExchange/0.1.0"; static
	 * private Namespace namespace = Namespace.getNamespace("x", namespaceStr); static
	 * private Element agentModelTemplate = null; static private Element locationTemplate;
	 * static private Filter<Element> elementFilter = new
	 * org.jdom2.filter.ElementFilter();
	 * 
	 * static Document getDocument() { return documentTemplateInstance.clone(); }
	 * 
	 * static private void setupElementTemplates(Document doc) { // Agent Model Template
	 * List<Element> agentModel = (List<Element>) XMLUtilities .executeXPath( doc,
	 * "/x:CsfMessageExchange/x:ReceivingEntities/x:DistributedSystem/x:DistributedAutonomousAgents/x:DistributedAutonomousAgent/x:AgentModels/x:AgentModel[1]"
	 * , namespaceStr, elementFilter); agentModelTemplate = agentModel.get(0);
	 * 
	 * // Location Template
	 * 
	 * @SuppressWarnings("unchecked") // TODO: Support multiple actors List<Element>
	 * agentLocation = (List<Element>) XMLUtilities .executeXPath( doc,
	 * "/x:CsfMessageExchange/x:ReceivingEntities/x:DistributedSystem/x:DistributedAutonomousAgents/x:DistributedAutonomousAgent/x:AgentModels/x:AgentModel/x:Actor/x:EnvironmentChanges/x:CommonEnvironmentChanges/x:EnvironmentChange/x:Location"
	 * , namespaceStr, elementFilter); locationTemplate = agentLocation.get(0).clone();
	 * locationTemplate.setAttribute("id", ""); }
	 * 
	 * @BeforeClass public static void setUpBeforeClass() throws Exception { try {
	 * documentTemplateInstance = MessagingUtilities
	 * .createCachedMessageExchangeTemplate();
	 * setupElementTemplates(documentTemplateInstance); } catch (JDOMException e) {
	 * 
	 * } catch (IOException e) {
	 * 
	 * } }
	 * 
	 * @AfterClass public static void tearDownAfterClass() throws Exception { }
	 * 
	 * private boolean firstAgentModelActorPopulated;
	 * 
	 * @Before public void setUp() throws Exception { }
	 * 
	 * @After public void tearDown() throws Exception { }
	 * 
	 * @Test
	 * 
	 * @Ignore public void testCachedMessageExchangeTemplate() { Document doc =
	 * getDocument();
	 * 
	 * @SuppressWarnings("unchecked") List<Element> xPathSearchedNodes = (List<Element>)
	 * XMLUtilities.executeXPath(doc, "/x:CsfMessageExchange/x:SendingEntity/x:Name",
	 * namespaceStr, elementFilter);
	 * System.out.println(xPathSearchedNodes.get(0).getValue());
	 * 
	 * xPathSearchedNodes.get(0).setText("new"); System.out.println("New Document: " + new
	 * XMLOutputter().outputString(doc)); }
	 * 
	 * // TODO: Extents/multiple dimensions public Element
	 * populateThisActorLocationInAgentModel(Element actor, String gridPointX, String
	 * gridPointY) {
	 * 
	 * @SuppressWarnings("unchecked") // TODO: Support multiple actors List<Element>
	 * thisAgentLocation = (List<Element>) XMLUtilities .executeXPath( actor,
	 * "./x:EnvironmentChanges/x:CommonEnvironmentChanges/x:EnvironmentChange/x:Location[@id='self']"
	 * , namespaceStr, elementFilter);
	 * 
	 * thisAgentLocation.get(0).getChild("GridPointX", namespace).setText(gridPointX);
	 * thisAgentLocation.get(0).getChild("GridPointY", namespace).setText(gridPointY);
	 * XMLUtilities.convertElementToXMLString(thisAgentLocation.get(0), true); return
	 * actor; }
	 * 
	 * public void setIDForActorInAgentModel(Content actor, String ID) { // TODO: Support
	 * multiple actors List<Element> agentModelID = (List<Element>)
	 * XMLUtilities.executeXPath(actor, "./x:ID", namespaceStr, elementFilter); Element e
	 * = agentModelID.get(0); e.setText(ID);
	 * 
	 * }
	 * 
	 * private Element getNextAgentModelActor(Object doc) {
	 * 
	 * @SuppressWarnings("unchecked") List<Element> agentModels = (List<Element>)
	 * XMLUtilities .executeXPath( doc,
	 * "/x:CsfMessageExchange/x:ReceivingEntities/x:DistributedSystem/x:DistributedAutonomousAgents/x:DistributedAutonomousAgent/x:AgentModels/x:AgentModel"
	 * , namespaceStr, elementFilter); Element agentModel = null; if
	 * (!firstAgentModelActorPopulated) { firstAgentModelActorPopulated = true; agentModel
	 * = agentModels.get(0).getChild("Actor", namespace); return agentModel; } else {
	 * agentModel = agentModelTemplate.clone(); agentModels.get(0).addContent(agentModel);
	 * return agentModel; } }
	 * 
	 * private Element getNextNonSelfLocationForActor(Element actor) {
	 * 
	 * @SuppressWarnings("unchecked") List<Element> environmentChange = (List<Element>)
	 * XMLUtilities.executeXPath( actor,
	 * "./x:EnvironmentChanges/x:CommonEnvironmentChanges/x:EnvironmentChange",
	 * namespaceStr, elementFilter); Element newLocation = locationTemplate.clone();
	 * environmentChange.get(0).addContent(newLocation); return newLocation; }
	 * 
	 * 
	 * Returns an
	 * 
	 * public Element processActorForAgentModel(Element actor, String ID, String
	 * gridPointX, String gridPointY) { // Select Agent Model
	 * 
	 * // Go ahead and populate the already created agent model from the Document //
	 * template setIDForActorInAgentModel(actor, ID); return
	 * populateThisActorLocationInAgentModel(actor, gridPointX, gridPointY); }
	 * 
	 * private Element populatePointWithLeastZombies(Element agentModelActor, String
	 * GridPointX, String GridPointY) { Element location =
	 * getNextNonSelfLocationForActor(agentModelActor); location.getChild("GridPointX",
	 * namespace).setText(GridPointX); location.getChild("GridPointY",
	 * namespace).setText(GridPointY); return location; }
	 */

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		repastS_AgentContext = new RepastS_AgentContext();
		repastS_AgentContext.setBypassRepastRuntimeForTestingPurposes(true);
		repastS_AgentContext.initializeCsfAgent(null,null);

		JADE_MAS_AdapterAPI jade_MAS_AdapterAPI = JADE_MAS_AdapterAPI.getInstance();
		JADE_MAS_RunGroupContext jade_MAS_RunGroupContext = jade_MAS_AdapterAPI
				.initializeAPI("TEMP");
		// jade_MAS_RunContext = jade_MAS_AdapterAPI.initializeSimulationRun(null,
		// jade_MAS_RunGroupContext, instance);
		jade_MAS_RunContext = new JADE_MAS_RunContext(null);

		jZombies_Csf = new JZombies_Csf(repastS_AgentContext);
		jade_MAS_AgentContext = new JADE_MAS_AgentContext();
		jade_MAS_AgentContext.initializeCsfAgent("TEST");

		simulationAPI = SimulationAPI.getInstance();
		simToolNameToSetInSimulationAPI = "REPAST_SIMPHONY";
		simulationRunGroupContext = null;
		simulationRunContext = null;

		try {
			simulationRunGroupContext = simulationAPI.initializeAPI("TEMP",
					simToolNameToSetInSimulationAPI);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		RepastS_SimulationRunGroupContext repastS_SimulationRunGroupContext = new RepastS_SimulationRunGroupContext(
				simulationRunGroupContext);
		repastS_AgentContext
				.setRepastS_SimulationRunGroupContext(repastS_SimulationRunGroupContext);

	}

	@Test
	@Ignore
	public void testPopulateZombiesMessage() {
		/*
		 * Document doc = getDocument();
		 * 
		 * @SuppressWarnings("unchecked")
		 * 
		 * //TODO: Support for other actors Element agentModelActor =
		 * getNextAgentModelActor(doc);
		 * 
		 * // For each agent model/actor Element actor =
		 * processActorForAgentModel(agentModelActor, "teststring1", "1", "2");
		 * populatePointWithLeastZombies(agentModelActor, "5", "6");
		 */

		FrameworkMessage msg = new FrameworkMessageImpl(SYSTEM_TYPE.SIMULATION_ENGINE,
				SYSTEM_TYPE.DISTRIBUTED_SYSTEM,
				simulationRunGroupContext.getBlankCachedMessageExchangeTemplate());

		List<String> thisAgentModelPosition = new ArrayList<String>();
		thisAgentModelPosition.add("1");
		thisAgentModelPosition.add("2");
		List<String> pointLeastZombies = new ArrayList<String>();
		pointLeastZombies.add("3");
		pointLeastZombies.add("4");

		jZombies_Csf.populateZombiesMessage(msg, "distAutAgent1", "distAutAgentMode1",
				thisAgentModelPosition, pointLeastZombies);

		thisAgentModelPosition = new ArrayList<String>();
		thisAgentModelPosition.add("5");
		thisAgentModelPosition.add("6");
		pointLeastZombies = new ArrayList<String>();
		pointLeastZombies.add("7");
		pointLeastZombies.add("8");

		jZombies_Csf.populateZombiesMessage(msg, "distAutAgent2", "distAutAgentMode2",
				thisAgentModelPosition, pointLeastZombies);

		System.out.println("Msg:"
				+ XMLUtilities.convertDocumentToXMLString(msg.getDocument(), true));

	}

	@Test
	@Ignore
	public void testReadZombiesAndSelfLocations() {
		Element distributedAutonomousAgentElement = null;
		try {
			distributedAutonomousAgentElement = XMLUtilities.xmlStringTojdom2Document(
					xmlString).getRootElement();
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		FrameworkMessage msg = null;
		msg = new FrameworkMessageImpl(SYSTEM_TYPE.SIMULATION_ENGINE,
				SYSTEM_TYPE.DISTRIBUTED_SYSTEM,
				simulationRunGroupContext.getBlankCachedMessageExchangeTemplate());

		msg.addDistributedAutonomousAgent(msg.getDocument(),
				distributedAutonomousAgentElement, true);

		JADE_MAS_AgentContext jade_MAS_AgentContext = new JADE_MAS_AgentContext();

		msg.getSelfLocation(distributedAutonomousAgentElement, msg);

		jZombies_Csf.getPointWithLeastZombies(distributedAutonomousAgentElement, msg);

		System.out.println("Msg:"
				+ XMLUtilities.convertDocumentToXMLString(msg.getDocument(), true));

	}

	@Test
	@Ignore
	public void testReceiveMessageInJADEAgent() {
		NativeDistributedAutonomousAgent nativeDistributedAutonomousAgent = new MockHumanJADE_Agent(
				"DistSys1", "DistributedSystemAutonomousAgent1",
				"DistributedAgentModel1", "Human");
		try {
			String messageID = UUID.randomUUID().toString();
			String distributedAutonomousAgentID = UUID.randomUUID().toString();

			Element distributedAutonomousAgentElement = XMLUtilities
					.xmlStringTojdom2Document(xmlString).getRootElement();
			FrameworkMessage msg = jade_MAS_AgentContext
					.convertDocumentSentToDistributedAutonomousAgentToFrameworkMessage(
							distributedAutonomousAgentElement,
							distributedAutonomousAgentID, SYSTEM_TYPE.SIMULATION_ENGINE,
							SYSTEM_TYPE.DISTRIBUTED_SYSTEM);
			nativeDistributedAutonomousAgent.receiveMessage(msg, messageID, null, this);
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void receiveMessage(FrameworkMessage message, String messageID,
			String inReplyToMessageID) {
		// TODO Auto-generated method stub

	}

	/*
	 * @Test public void testWaitMethod() {
	 * jade_MAS_RunContext.waitForAndProcessSimulationEngineMessageAfterHandshake(); }
	 */

}
