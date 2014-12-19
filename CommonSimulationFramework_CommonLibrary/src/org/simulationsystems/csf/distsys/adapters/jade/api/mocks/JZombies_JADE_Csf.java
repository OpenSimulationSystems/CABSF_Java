package org.simulationsystems.csf.distsys.adapters.jade.api.mocks;

import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filter;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.internal.messaging.xml.XMLUtilities;
import org.simulationsystems.csf.distsys.adapters.jade.api.JADE_MAS_AgentContext;

public class JZombies_JADE_Csf {
	private JADE_MAS_AgentContext jade_MAS_AgentContext;
	//TODO: Remove these after centralizing this code
	private Filter<Element> elementFilter = new org.jdom2.filter.ElementFilter();
	private String namespaceStr = "http://www.simulationsystems.org/csf/schemas/CsfMessageExchange/0.1.0";
	private Namespace namespace = Namespace.getNamespace("x", namespaceStr);

	
	@SuppressWarnings("unused")
	private JZombies_JADE_Csf() {
	}
	
	public JZombies_JADE_Csf(JADE_MAS_AgentContext jade_MAS_AgentContext) {
		this.jade_MAS_AgentContext = jade_MAS_AgentContext;
	}
	
	public Element getPointWithLeastZombies(Document distributedAutononomousAgent, FrameworkMessage msg) {
		Element agentModelActor = msg.getNextAgentModelActor(distributedAutononomousAgent, jade_MAS_AgentContext.getJade_MAS_RunContext().getCachedAgentModelActorTemplate());
		
		List<Element> environmentChange = (List<Element>) XMLUtilities
				.executeXPath(
						agentModelActor,
						"./x:EnvironmentChanges/x:SimulationDefinedEnvironmentChanges/x:EnvironmentChange",
						namespaceStr, elementFilter);
		
		//FIXME: check the attributes
		Element location = environmentChange.get(0).getChild("Location");
		
		String xValue = location.getChild("GridPointX", namespace).getText();
		String yValue = location.getChild("GridPointY", namespace).getText();
		System.out.println("Grid Point X: " + xValue);
		System.out.println("Grid Point Y: " + yValue);
		
/*		location.setAttribute("category", "neighborhood");
		location.setAttribute("includecenter", "true");
		location.setAttribute("entitytype", "Zombie");*/
		return location;
	}
}
