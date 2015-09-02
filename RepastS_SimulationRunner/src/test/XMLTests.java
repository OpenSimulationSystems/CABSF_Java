package test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jzombies.JZombies_CABSF_Helper;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.opensimulationsystems.cabsf.common.internal.messaging.xml.XMLUtilities;
import org.opensimulationsystems.cabsf.common.model.SYSTEM_TYPE;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FrameworkMessage;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FrameworkMessageImpl;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.JADE_MAS_AdapterAPI;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.JADE_MAS_AgentContext;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.JADE_MAS_RunContext;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.JadeControllerInterface;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.nativeagents.NativeDistributedAutonomousAgent;
import org.opensimulationsystems.cabsf.distsys.mas.mocks.MockHumanJADE_Agent;
import org.opensimulationsystems.cabsf.sim.adapters.simengines.repastS.api.CabsfRepastS_AgentContext;
import org.opensimulationsystems.cabsf.sim.adapters.simengines.repastS.api.RepastS_SimulationRunGroupContext;
import org.opensimulationsystems.cabsf.sim.core.api.SimulationAPI;
import org.opensimulationsystems.cabsf.sim.core.api.SimulationRunContext;
import org.opensimulationsystems.cabsf.sim.core.api.SimulationRunGroupContext;

import prisonersdilemma.DECISION;
import prisonersdilemma.PrisonersDilemma_CABSF_Helper;

public class XMLTests implements JadeControllerInterface {
    static private XMLTests instance = new XMLTests();

    static private SimulationRunContext simulationRunContext;
    static private SimulationRunGroupContext simulationRunGroupContext;
    static private SimulationAPI simulationAPI;
    static private String simToolNameToSetInSimulationAPI;
    private static JZombies_CABSF_Helper jZombies_CABSF_Helper;
    private static PrisonersDilemma_CABSF_Helper prisonersDilemma_CABSF_Helper;
    static JADE_MAS_AgentContext jade_MAS_AgentContext;
    static private CabsfRepastS_AgentContext cabsfRepastS_AgentContext;

    private static JADE_MAS_RunContext jade_MAS_RunContext;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        cabsfRepastS_AgentContext = new CabsfRepastS_AgentContext();
        cabsfRepastS_AgentContext.setBypassRepastRuntimeForTestingPurposes(true);
        cabsfRepastS_AgentContext.initializeCabsfAgent(null);

        final JADE_MAS_AdapterAPI jade_MAS_AdapterAPI = JADE_MAS_AdapterAPI.getInstance();
        jade_MAS_AdapterAPI.initializeAPI("TEMP");
        // jade_MAS_RunContext =
        // jade_MAS_AdapterAPI.initializeSimulationRun(null,
        // jade_MAS_RunGroupContext, instance);
        jade_MAS_RunContext = new JADE_MAS_RunContext(null);

        jZombies_CABSF_Helper = new JZombies_CABSF_Helper(cabsfRepastS_AgentContext);
        prisonersDilemma_CABSF_Helper = new PrisonersDilemma_CABSF_Helper(
                cabsfRepastS_AgentContext);

        jade_MAS_AgentContext = new JADE_MAS_AgentContext();
        jade_MAS_AgentContext.initializeCabsfAgent("TEST");

        simulationAPI = SimulationAPI.getInstance();
        simToolNameToSetInSimulationAPI = "REPAST_SIMPHONY";
        simulationRunGroupContext = null;
        simulationRunContext = null;

        try {
            simulationRunGroupContext = simulationAPI.initializeAPI("TEMP",
                    simToolNameToSetInSimulationAPI);
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        final RepastS_SimulationRunGroupContext repastS_SimulationRunGroupContext = new RepastS_SimulationRunGroupContext(
                simulationRunGroupContext);
        cabsfRepastS_AgentContext
        .setRepastS_SimulationRunGroupContext(repastS_SimulationRunGroupContext);

    }

    /*
     * static private Document documentTemplateInstance = null; static private
     * String namespaceStr =
     * "http://www.opensimulationsystems.org/cabsf/schemas/CabsfMessageExchange/0.1.0"
     * ; static private Namespace namespace = Namespace.getNamespace("x",
     * namespaceStr); static private Element agentModelTemplate = null; static
     * private Element locationTemplate; static private Filter<Element>
     * elementFilter = new org.jdom2.filter.ElementFilter();
     *
     * static Document getDocument() { return documentTemplateInstance.clone();
     * }
     *
     * static private void setupElementTemplates(Document doc) { // Agent Model
     * Template List<Element> agentModel = (List<Element>) XMLUtilities
     * .executeXPath( doc,
     * "/x:CabsfMessageExchange/x:ReceivingEntities/x:DistributedSystem/x:DistributedAutonomousAgents/x:DistributedAutonomousAgent/x:AgentModels/x:AgentModel[1]"
     * , namespaceStr, elementFilter); agentModelTemplate = agentModel.get(0);
     *
     * // Location Template
     *
     * @SuppressWarnings("unchecked") // TODO: Support multiple actors
     * List<Element> agentLocation = (List<Element>) XMLUtilities .executeXPath(
     * doc,
     * "/x:CabsfMessageExchange/x:ReceivingEntities/x:DistributedSystem/x:DistributedAutonomousAgents/x:DistributedAutonomousAgent/x:AgentModels/x:AgentModel/x:Actor/x:EnvironmentChanges/x:CommonEnvironmentChanges/x:EnvironmentChange/x:Location"
     * , namespaceStr, elementFilter); locationTemplate =
     * agentLocation.get(0).clone(); locationTemplate.setAttribute("id", ""); }
     *
     * @BeforeClass public static void setUpBeforeClass() throws Exception { try
     * { documentTemplateInstance = MessagingUtilities
     * .createCachedMessageExchangeTemplate();
     * setupElementTemplates(documentTemplateInstance); } catch (JDOMException
     * e) {
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
     * @SuppressWarnings("unchecked") List<Element> xPathSearchedNodes =
     * (List<Element>) XMLUtilities.executeXPath(doc,
     * "/x:CabsfMessageExchange/x:SendingEntity/x:Name", namespaceStr,
     * elementFilter); System.out.println(xPathSearchedNodes.get(0).getValue());
     *
     * xPathSearchedNodes.get(0).setText("new");
     * System.out.println("New Document: " + new
     * XMLOutputter().outputString(doc)); }
     *
     * // TODO: Extents/multiple dimensions public Element
     * populateThisActorLocationInAgentModel(Element actor, String gridPointX,
     * String gridPointY) {
     *
     * @SuppressWarnings("unchecked") // TODO: Support multiple actors
     * List<Element> thisAgentLocation = (List<Element>) XMLUtilities
     * .executeXPath( actor,
     * "./x:EnvironmentChanges/x:CommonEnvironmentChanges/x:EnvironmentChange/x:Location[@id='self']"
     * , namespaceStr, elementFilter);
     *
     * thisAgentLocation.get(0).getChild("GridPointX",
     * namespace).setText(gridPointX);
     * thisAgentLocation.get(0).getChild("GridPointY",
     * namespace).setText(gridPointY);
     * XMLUtilities.convertElementToXMLString(thisAgentLocation.get(0), true);
     * return actor; }
     *
     * public void setIDForActorInAgentModel(Content actor, String ID) { //
     * TODO: Support multiple actors List<Element> agentModelID =
     * (List<Element>) XMLUtilities.executeXPath(actor, "./x:ID", namespaceStr,
     * elementFilter); Element e = agentModelID.get(0); e.setText(ID);
     *
     * }
     *
     * private Element getNextAgentModelActor(Object doc) {
     *
     * @SuppressWarnings("unchecked") List<Element> agentModels =
     * (List<Element>) XMLUtilities .executeXPath( doc,
     * "/x:CabsfMessageExchange/x:ReceivingEntities/x:DistributedSystem/x:DistributedAutonomousAgents/x:DistributedAutonomousAgent/x:AgentModels/x:AgentModel"
     * , namespaceStr, elementFilter); Element agentModel = null; if
     * (!firstAgentModelActorPopulated) { firstAgentModelActorPopulated = true;
     * agentModel = agentModels.get(0).getChild("Actor", namespace); return
     * agentModel; } else { agentModel = agentModelTemplate.clone();
     * agentModels.get(0).addContent(agentModel); return agentModel; } }
     *
     * private Element getNextNonSelfLocationForActor(Element actor) {
     *
     * @SuppressWarnings("unchecked") List<Element> environmentChange =
     * (List<Element>) XMLUtilities.executeXPath( actor,
     * "./x:EnvironmentChanges/x:CommonEnvironmentChanges/x:EnvironmentChange",
     * namespaceStr, elementFilter); Element newLocation =
     * locationTemplate.clone();
     * environmentChange.get(0).addContent(newLocation); return newLocation; }
     *
     *
     * Returns an
     *
     * public Element processActorForAgentModel(Element actor, String ID, String
     * gridPointX, String gridPointY) { // Select Agent Model
     *
     * // Go ahead and populate the already created agent model from the
     * Document // template setIDForActorInAgentModel(actor, ID); return
     * populateThisActorLocationInAgentModel(actor, gridPointX, gridPointY); }
     *
     * private Element populatePointWithLeastZombies(Element agentModelActor,
     * String GridPointX, String GridPointY) { Element location =
     * getNextNonSelfLocationForActor(agentModelActor);
     * location.getChild("GridPointX", namespace).setText(GridPointX);
     * location.getChild("GridPointY", namespace).setText(GridPointY); return
     * location; }
     */

    String xmlString = "<DistributedAutonomousAgent xmlns=\"http://www.opensimulationsystems.org/cabsf/schemas/CabsfMessageExchange/0.1.0\"\r\n"
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
            + "                <test/><ID>distAutAgentMode2</ID>\r\n"
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
            + "                  <!-- Common Cabsf Actions -->\r\n"
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

    @Override
    public void receiveMessage(final FrameworkMessage message, final String messageID,
            final String inReplyToMessageID) {
        // TODO Auto-generated method stub

    }

    @Test
    // @Ignore
    public void testPopulatePrisonersDilemmaMessage() {
        testPopulatePrisonersDilemmaMessageHelper();
    }

    FrameworkMessage testPopulatePrisonersDilemmaMessageHelper() {
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

        final FrameworkMessage msg = new FrameworkMessageImpl(
                SYSTEM_TYPE.SIMULATION_ENGINE, SYSTEM_TYPE.DISTRIBUTED_SYSTEM,
                simulationRunGroupContext.getBlankCachedMessageExchangeTemplate());

        // Get the distributed autonomous agent element and set the ID
        final Element distributedAutonomousAgentElement = msg
                .getNextDistributedAutonomousAgent(msg.getDocument(),
                        simulationRunGroupContext
                        .getCachedDistributedAutonomousAgentTemplate());
        msg.setDistributedAutonomousAgentID(distributedAutonomousAgentElement,
                "distAutAgent2");

        // Get the agent model actor and set the ID
        final Element agentModelActor = msg.getNextAgentModelActor(
                distributedAutonomousAgentElement,
                simulationRunGroupContext.getCachedAgentModelActorTemplate());
        msg.setIDForActorInAgentModel(agentModelActor, "distAutAgentModel2");
        final int round = 1;
        final DECISION otherPlayerLastDecision = DECISION.COOPERATE;
        prisonersDilemma_CABSF_Helper.populatePrisonersDilemmaFrameworkMessage(msg,
                agentModelActor, round, otherPlayerLastDecision, null);

        System.out.println("Prisoner's Dilemma MSG:"
                + XMLUtilities.convertDocumentToXMLString(msg.getDocument(), true));

        return msg;
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

        final FrameworkMessage msg = new FrameworkMessageImpl(
                SYSTEM_TYPE.SIMULATION_ENGINE, SYSTEM_TYPE.DISTRIBUTED_SYSTEM,
                simulationRunGroupContext.getBlankCachedMessageExchangeTemplate());

        List<String> thisAgentModelPosition = new ArrayList<String>();
        thisAgentModelPosition.add("1");
        thisAgentModelPosition.add("2");
        List<String> pointLeastZombies = new ArrayList<String>();
        pointLeastZombies.add("3");
        pointLeastZombies.add("4");

        jZombies_CABSF_Helper.populateZombiesMessage(msg, "distAutAgent1",
                "distAutAgentMode1", thisAgentModelPosition, pointLeastZombies);

        thisAgentModelPosition = new ArrayList<String>();
        thisAgentModelPosition.add("5");
        thisAgentModelPosition.add("6");
        pointLeastZombies = new ArrayList<String>();
        pointLeastZombies.add("7");
        pointLeastZombies.add("8");

        jZombies_CABSF_Helper.populateZombiesMessage(msg, "distAutAgent2",
                "distAutAgentMode2", thisAgentModelPosition, pointLeastZombies);

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
        } catch (final JDOMException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        FrameworkMessage msg = null;
        msg = new FrameworkMessageImpl(SYSTEM_TYPE.SIMULATION_ENGINE,
                SYSTEM_TYPE.DISTRIBUTED_SYSTEM,
                simulationRunGroupContext.getBlankCachedMessageExchangeTemplate());

        msg.addDistributedAutonomousAgent(msg.getDocument(),
                distributedAutonomousAgentElement, true);

        new JADE_MAS_AgentContext();

        msg.getSelfLocationFromFirstAgentModel(distributedAutonomousAgentElement, msg);

        jZombies_CABSF_Helper.getPointWithLeastZombies(distributedAutonomousAgentElement,
                msg);

        System.out.println("Msg:"
                + XMLUtilities.convertDocumentToXMLString(msg.getDocument(), true));

        // return msg;
    }

    @Test
    @Ignore
    public void testReceiveMessageInJADEAgent() {
        final NativeDistributedAutonomousAgent nativeDistributedAutonomousAgent = new MockHumanJADE_Agent(
                "DistSys1", "DistributedSystemAutonomousAgent1",
                "DistributedAgentModel1", "Human");
        try {
            final String messageID = UUID.randomUUID().toString();
            final String distributedAutonomousAgentID = UUID.randomUUID().toString();

            final Element distributedAutonomousAgentElement = XMLUtilities
                    .xmlStringTojdom2Document(xmlString).getRootElement();
            final FrameworkMessage msg = jade_MAS_AgentContext
                    .convertDocumentToSendToDAAtoFrameworkMessage(
                            distributedAutonomousAgentElement,
                            distributedAutonomousAgentID, SYSTEM_TYPE.SIMULATION_ENGINE,
                            SYSTEM_TYPE.DISTRIBUTED_SYSTEM);
            nativeDistributedAutonomousAgent.receiveMessage(msg, messageID, null, this);
        } catch (final JDOMException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Test
    // @Ignore
    public void testReceiveMessageInPrisonersDilemmaJADEagent() {
        final FrameworkMessage msg = testPopulatePrisonersDilemmaMessageHelper();

        final Element distributedAutonomousAgent = msg.getNextDistributedAutonomousAgent(
                msg.getDocument(), null);

        /*
         * Element agentModelActor =
         * msg.getNextAgentModelActor(distributedAutonomousAgent,
         * cabsfRepastS_AgentContext.getCachedAgentModelActorTemplate());
         */
        final DECISION decision = prisonersDilemma_CABSF_Helper.getOtherPlayerDecision(
                distributedAutonomousAgent, msg);
        System.out.println("Read decision: " + decision.toString());

        final int roundNumber = prisonersDilemma_CABSF_Helper.getRoundNumber(
                distributedAutonomousAgent, msg);
        System.out.println("Read round number: " + String.valueOf(roundNumber));
    }

    /*
     * @Test public void testWaitMethod() {
     * jade_MAS_RunContext.waitForAndProcessSimulationEngineMessageAfterHandshake
     * (); }
     */

}
