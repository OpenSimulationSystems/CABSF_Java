package org.opensimulationsystems.cabsf.common.model.configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.Filter;
import org.opensimulationsystems.cabsf.common.internal.messaging.MessagingUtilities;
import org.opensimulationsystems.cabsf.common.internal.messaging.xml.XMLUtilities;
import org.opensimulationsystems.cabsf.common.internal.systems.AgentMappingHelper;
import org.opensimulationsystems.cabsf.common.model.AgentMapping;
import org.opensimulationsystems.cabsf.common.model.cabsfexceptions.CabsfInitializationRuntimeException;
import org.opensimulationsystems.cabsf.sim.core.api.configuration.SimulationRunConfiguration;

// TODO: Auto-generated Javadoc
/**
 * The configuration object for a simulation run group.
 *
 * @author Jorge Calderon
 * @version 0.2
 * @since 0.1
 */
public class RunGroupConfiguration {
    // TODO set all of these in the configuration file
    /** The simulation engine id. */
    private final String simulationEngineID = "REPAST_SIMPHONY";

    /** The namespace str. */
    private final String namespaceStr = "http://www.opensimulationsystems.org/cabsf/schemas/CabsfMessageExchange/0.1.0";

    /** The namespace. */
    private final Namespace namespace = Namespace.getNamespace("x", namespaceStr);

    /** The location template. */
    private final Element locationTemplate = null;

    /** The element filter. */
    private final Filter<Element> elementFilter = new org.jdom2.filter.ElementFilter();

    /** The simulation agents belong to one class. */
    private final boolean simulationAgentsBelongToOneClass = false;

    // TODO: Actually populate these values
    // LOW: Support Multiple Simulation Run Configurations
    /** The simulation run configurations. */
    ArrayList<SimulationRunConfiguration> simulationRunConfigurations;

    /** The CABSF configuration Document. */
    private Document cabsfConfigurationDocument = null;

    /** The framework status_ x path. */
    final private String distributedSystems_XPath = "/x:CabsfSimulationConfiguration/x:AgentMappings/x:DistributedSystems";

    final private String numberOfSimulationRuns_XPath = "/x:CabsfSimulationConfiguration/x:SimulationEngineSpecificConfigurations/x:AllSimulationRuns/x:NumberOfSimulationRuns";

    /** The agents ready for simulation side mapping. */
    private final HashSet<AgentMapping> agentsReadyForSimulationSideMapping = new HashSet<AgentMapping>();

    /** The agent types. */
    private final HashSet<String> agentTypes = new HashSet<String>();

    private Integer numberOfDistributedAutonomousAgents;

    private Integer numberOfSimulationRuns;

    // TODO: Support Multiple Distributed Systems
    private List<Element> distributedAutonomousAgentElements;

    // TODO: Support Multiple Distributed Systems
    private Element distSys;

    // TODO: Support Multiple Distributed Systems
    private String distSysIDstr;

    /**
     * Instantiates a new simulation run group configuration.
     *
     * @param cabsfConfigurationFileName
     *            the framework configuration file name name
     */
    public RunGroupConfiguration(final String cabsfConfigurationFileName) {
        try {
            cabsfConfigurationDocument = MessagingUtilities
                    .createDocumentFromFileSystemPath(cabsfConfigurationFileName);
        } catch (JDOMException | IOException e) {
            throw new CabsfInitializationRuntimeException(
                    "Failed to read the CABSF Configuration File: "
                            + cabsfConfigurationFileName, e);
        }
        if (cabsfConfigurationDocument == null) {
            throw new CabsfInitializationRuntimeException(
                    "Error reading the CABSF Configuration File: "
                            + cabsfConfigurationFileName);
        }

        processHeaderSection();

        /*
         * if (cabsfConfigurationFileName
         * .equals("PLACEHOLDER_FOR_CABSF_CONFIGURATION_FILE")) {
         * this.simulationAgentsBelongToOneClass = true; }
         */

    }

    /**
     * Populate the elements from the DOcu.
     *
     * @return the hash set
     */
    public HashSet<AgentMapping> createAgentMappingObjects() {

        for (int i = 0; i < distributedAutonomousAgentElements.size(); i++) {
            final Element agent = distributedAutonomousAgentElements.get(i);
            final String distAutAgentID = agent.getChild("DistributedAutonomousAgentID",
                    namespace).getValue();

            // Get Agent Models
            final List<Element> agentModelsElements = agent.getChildren("AgentModels",
                    namespace);
            assert (agentModelsElements.size() == 1);

            final List<Element> agentModels = agentModelsElements.get(0).getChildren(
                    "AgentModel", namespace);
            // TODO: Support Multiple Agent Models
            assert (agentModels.size() == 1);
            final Element agentModel = agentModels.get(0);
            final String distributedAutonomousAgentModelID = agentModel.getChildText(
                    "DistributedAutonomousAgentModelID", namespace);

            final String simulationEngineClass = agentModel.getChildText(
                    "SimulationEngineClass", namespace);
            if (distributedAutonomousAgentModelID == null
                    || distributedAutonomousAgentModelID.equals("")
                    || simulationEngineClass == null || simulationEngineClass.equals("")) {
                throw new CabsfInitializationRuntimeException(
                        "The Distributed Autonomous Agent Model ID and Simulation Engine Class must be supplied for each agent model. ");
            }

            // Create Agent Mapping Object, but Don't map actual agent yetl
            AgentMappingHelper.createAgentMappingObjButDontMap(
                    agentsReadyForSimulationSideMapping, distSysIDstr, distAutAgentID,
                    distributedAutonomousAgentModelID, simulationEngineClass);
        }

        // TODO: Support more than 1 Distributed System
        return agentsReadyForSimulationSideMapping;
    }

    /**
     * Gets the agents ready for simulation side mapping.
     *
     * @return the agents ready for simulation side mapping
     */
    public HashSet<AgentMapping> getAgentsReadyForSimulationSideMapping() {
        return agentsReadyForSimulationSideMapping;
    }

    /**
     * Gets the agent types.
     *
     * @return the agent types
     */
    public HashSet<String> getAgentTypes() {
        return agentTypes;
    }

    /*	*//**
     * Gets the simulation agents belong to one class.
     *
     * @return the simulation agents belong to one class
     */
    /*
     * public boolean getSimulationAgentsBelongToOneClass() { return
     * simulationAgentsBelongToOneClass; }
     */

    public Document getCabsfConfigurationDocument() {
        return cabsfConfigurationDocument;
    }

    public Integer getNumberOfDistributedAutonomousAgents() {
        return numberOfDistributedAutonomousAgents;
    }

    public long getNumberOfSimulationRuns() {
        return numberOfSimulationRuns;

    }

    /**
     * Gets the simulation engine id.
     *
     * @return the simulation engine id
     */
    public String getSimulationEngineID() {
        return simulationEngineID;
    }

    /**
     * Gets the simulation run configuration.
     *
     * @return the simulation run configuration
     */
    public SimulationRunConfiguration getSimulationRunConfiguration() {
        return simulationRunConfigurations.get(0);

    }

    /**
     * Set up the agent types (classes), from the configuration file.
     *
     * @param agentTypeElements
     *            the agent type elements
     */
    private void processAgentTypes(final List<Element> agentTypeElements) {
        for (final Element agentTypeElement : agentTypeElements) {
            final String type = agentTypeElement.getAttribute("type").getValue();
            System.out.println("[CABSF - Common API] Agent type/class to be mapped: "
                    + type);
            agentTypes.add(type);
        }
    }

    private void processHeaderSection() {
        /*
         * final List<Element> distributedSystemsElements = (List<Element>)
         * XMLUtilities .executeXPath(cabsfConfigurationDocument,
         * distributedSystems_XPath, namespaceStr, elementFilter); assert
         * (distributedSystemsElements.size() == 1);
         */

        final List<Element> distributedSystemsElements = (List<Element>) XMLUtilities
                .executeXPath(cabsfConfigurationDocument, distributedSystems_XPath,
                        namespaceStr, elementFilter);
        assert (distributedSystemsElements.size() == 1);

        final List<Element> numberOfSimulationRunsElements = (List<Element>) XMLUtilities
                .executeXPath(cabsfConfigurationDocument, numberOfSimulationRuns_XPath,
                        namespaceStr, elementFilter);
        if (numberOfSimulationRunsElements.size() != 1) {
            throw new CabsfInitializationRuntimeException(
                    "Number of simulation runs is not defined in the configuration file.");
        }
        final String numberOfSimulationRunsStr = numberOfSimulationRunsElements.get(0)
                .getText();
        try {
            numberOfSimulationRuns = Integer.parseInt(numberOfSimulationRunsStr);
        } catch (final NumberFormatException e) {
            throw new CabsfInitializationRuntimeException(
                    "Number of simulation runs is not defined in the configuration file.");
        }

        final List<Element> distributedSystemElements = distributedSystemsElements.get(0)
                .getChildren("DistributedSystem", namespace);
        // TODO: Support more than 1 Distributed System
        assert (distributedSystemElements.size() == 1);

        distSys = distributedSystemElements.get(0);

        final Element distSysIDelement = distributedSystemElements.get(0).getChild(
                "DistributedSystemID", namespace);

        // final HashMap<Element, List<Element>>
        // distributedSystemToDistributedAutonomousAgents = new HashSet<Element,
        // List<Element>>();

        distSysIDstr = distSysIDelement.getValue();
        if (distSysIDstr == null || distSysIDstr.equals("")) {
            throw new CabsfInitializationRuntimeException(
                    "The Distributed System ID must be supplied: "
                            + distributedSystems_XPath);
        }

        final List<Element> distributedAutonomousAgentsElements = distSys.getChildren(
                "DistributedSoftwareAgents", namespace);
        assert (distributedAutonomousAgentsElements.size() == 1);

        final List<Element> agentTypeElements = distributedAutonomousAgentsElements
                .get(0).getChildren("AgentType", namespace);
        assert (agentTypeElements.size() >= 1);
        processAgentTypes(agentTypeElements);

        // TODO: Support multiple agent types
        distributedAutonomousAgentElements = agentTypeElements.get(0).getChildren(
                "DistributedSoftwareAgent", namespace);
        assert (distributedAutonomousAgentElements.size() >= 1);

        this.numberOfDistributedAutonomousAgents = distributedAutonomousAgentElements
                .size();
    }

    /**
     * Sets the cabsf configuration document.
     *
     * @param cabsfConfigurationDocument
     *            the new cabsf configuration document
     */
    public void setCabsfConfigurationDocument(final Document cabsfConfigurationDocument) {
        this.cabsfConfigurationDocument = cabsfConfigurationDocument;
    }

}
