package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

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
import org.simulationsystems.csf.sim.core.api.SimulationAPI;
import org.simulationsystems.csf.sim.core.api.SimulationRunContext;
import org.simulationsystems.csf.sim.core.api.SimulationRunGroupContext;
import org.simulationsystems.csf.sim.engines.adapters.repastS.api.RepastS_AgentContext;

public class XMLTests {
/*	static private Document documentTemplateInstance = null;
	static private String namespaceStr = "http://www.simulationsystems.org/csf/schemas/CsfMessageExchange/0.1.0";
	static private Namespace namespace = Namespace.getNamespace("x", namespaceStr);
	static private Element agentModelTemplate = null;
	static private Element locationTemplate;
	static private Filter<Element> elementFilter = new org.jdom2.filter.ElementFilter();

	static Document getDocument() {
		return documentTemplateInstance.clone();
	}

	static private void setupElementTemplates(Document doc) {
		// Agent Model Template
		List<Element> agentModel = (List<Element>) XMLUtilities
				.executeXPath(
						doc,
						"/x:CsfMessageExchange/x:ReceivingEntities/x:DistributedSystem/x:DistributedAutonomousAgents/x:DistributedAutonomousAgent/x:AgentModels/x:AgentModel[1]",
						namespaceStr, elementFilter);
		agentModelTemplate = agentModel.get(0);

		// Location Template
		@SuppressWarnings("unchecked")
		// TODO: Support multiple actors
		List<Element> agentLocation = (List<Element>) XMLUtilities
				.executeXPath(
						doc,
						"/x:CsfMessageExchange/x:ReceivingEntities/x:DistributedSystem/x:DistributedAutonomousAgents/x:DistributedAutonomousAgent/x:AgentModels/x:AgentModel/x:Actor/x:EnvironmentChanges/x:CommonEnvironmentChanges/x:EnvironmentChange/x:Location",
						namespaceStr, elementFilter);
		locationTemplate = agentLocation.get(0).clone();
		locationTemplate.setAttribute("id", "");
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try {
			documentTemplateInstance = MessagingUtilities
					.createCachedMessageExchangeTemplate();
			setupElementTemplates(documentTemplateInstance);
		} catch (JDOMException e) {

		} catch (IOException e) {

		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	private boolean firstAgentModelActorPopulated;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	@Ignore
	public void testCachedMessageExchangeTemplate() {
		Document doc = getDocument();

		@SuppressWarnings("unchecked")
		List<Element> xPathSearchedNodes = (List<Element>) XMLUtilities.executeXPath(doc,
				"/x:CsfMessageExchange/x:SendingEntity/x:Name", namespaceStr,
				elementFilter);
		System.out.println(xPathSearchedNodes.get(0).getValue());

		xPathSearchedNodes.get(0).setText("new");
		System.out.println("New Document: " + new XMLOutputter().outputString(doc));
	}

	// TODO: Extents/multiple dimensions
	public Element populateThisActorLocationInAgentModel(Element actor,
			String gridPointX, String gridPointY) {
		@SuppressWarnings("unchecked")
		// TODO: Support multiple actors
		List<Element> thisAgentLocation = (List<Element>) XMLUtilities
				.executeXPath(
						actor,
						"./x:EnvironmentChanges/x:CommonEnvironmentChanges/x:EnvironmentChange/x:Location[@id='self']",
						namespaceStr, elementFilter);

		thisAgentLocation.get(0).getChild("GridPointX", namespace).setText(gridPointX);
		thisAgentLocation.get(0).getChild("GridPointY", namespace).setText(gridPointY);
		XMLUtilities.convertElementToXMLString(thisAgentLocation.get(0), true);
		return actor;
	}

	public void setIDForActorInAgentModel(Content actor, String ID) {
		// TODO: Support multiple actors
		List<Element> agentModelID = (List<Element>) XMLUtilities.executeXPath(actor,
				"./x:ID", namespaceStr, elementFilter);
		Element e = agentModelID.get(0);
		e.setText(ID);

	}

	private Element getNextAgentModelActor(Object doc) {
		@SuppressWarnings("unchecked")
		List<Element> agentModels = (List<Element>) XMLUtilities
				.executeXPath(
						doc,
						"/x:CsfMessageExchange/x:ReceivingEntities/x:DistributedSystem/x:DistributedAutonomousAgents/x:DistributedAutonomousAgent/x:AgentModels/x:AgentModel",
						namespaceStr, elementFilter);
		Element agentModel = null;
		if (!firstAgentModelActorPopulated) {
			firstAgentModelActorPopulated = true;
			agentModel = agentModels.get(0).getChild("Actor", namespace);
			return agentModel;
		} else {
			agentModel = agentModelTemplate.clone();
			agentModels.get(0).addContent(agentModel);
			return agentModel;
		}
	}

	private Element getNextNonSelfLocationForActor(Element actor) {
		@SuppressWarnings("unchecked")
		List<Element> environmentChange = (List<Element>) XMLUtilities.executeXPath(
				actor,
				"./x:EnvironmentChanges/x:CommonEnvironmentChanges/x:EnvironmentChange",
				namespaceStr, elementFilter);
		Element newLocation = locationTemplate.clone();
		environmentChange.get(0).addContent(newLocation);
		return newLocation;
	}

	
	 * Returns an
	 
	public Element processActorForAgentModel(Element actor, String ID, String gridPointX,
			String gridPointY) {
		// Select Agent Model

		// Go ahead and populate the already created agent model from the Document
		// template
		setIDForActorInAgentModel(actor, ID);
		return populateThisActorLocationInAgentModel(actor, gridPointX, gridPointY);
	}

	private Element populatePointWithLeastZombies(Element agentModelActor,
			String GridPointX, String GridPointY) {
		Element location = getNextNonSelfLocationForActor(agentModelActor);
		location.getChild("GridPointX", namespace).setText(GridPointX);
		location.getChild("GridPointY", namespace).setText(GridPointY);
		return location;
	}*/

	@Test
	public void testSetInitialLocationOfLocalZombies() {
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
		RepastS_AgentContext repastS_AgentContext = new RepastS_AgentContext();
		JZombies_Csf jZombies_Csf = new JZombies_Csf(repastS_AgentContext);
		
		SimulationAPI simulationAPI = SimulationAPI.getInstance();
		String simToolNameToSetInSimulationAPI = "REPAST_SIMPHONY";
		SimulationRunGroupContext simulationRunGroupContext=null;
		SimulationRunContext simulationRunContext = null;
		try {
			simulationRunGroupContext = simulationAPI.initializeAPI("TEMP", simToolNameToSetInSimulationAPI);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		FrameworkMessage msg = new FrameworkMessageImpl(SYSTEM_TYPE.SIMULATION_ENGINE,
				SYSTEM_TYPE.DISTRIBUTED_SYSTEM,
				simulationRunGroupContext.getCachedMessageExchangeTemplate());
		Element agentModelActor = msg.getNextAgentModelActor(msg.getDocument(), simulationRunGroupContext.getCachedAgentModelActorTemplate());
		msg.processActorForAgentModel(agentModelActor, "teststring1", "1", "2");
		jZombies_Csf.populatePointWithLeastZombies(msg, agentModelActor, "3", "4", simulationRunGroupContext.getCachedLocationTemplate());
		
		Element agentModelActor2 = msg.getNextAgentModelActor(msg.getDocument(), simulationRunGroupContext.getCachedAgentModelActorTemplate());
		msg.processActorForAgentModel(agentModelActor2, "teststring2", "3", "4");
		jZombies_Csf.populatePointWithLeastZombies(msg, agentModelActor2, "5", "6", simulationRunGroupContext.getCachedLocationTemplate());
		
		
		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
		String xmlString = outputter.outputString(msg.getDocument());
		System.out.println(xmlString);
	}

}
