package org.opensimulationsystems.cabsf.sim.adapters.simengines.repastS.api;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filter;
import org.opensimulationsystems.cabsf.common.internal.messaging.MessagingUtilities;
import org.opensimulationsystems.cabsf.common.internal.messaging.xml.XMLUtilities;
import org.opensimulationsystems.cabsf.common.model.SYSTEM_TYPE;
import org.opensimulationsystems.cabsf.common.model.cabsfexceptions.CabsfMessagingRuntimeException;
import org.opensimulationsystems.cabsf.common.model.cabsfexceptions.CabsfRuntimeException;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FRAMEWORK_COMMAND;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FrameworkMessage;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FrameworkMessageImpl;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.STATUS;
import org.opensimulationsystems.cabsf.sim.core.api.SimulationAPI;
import org.opensimulationsystems.cabsf.sim.core.api.SimulationRunContext;
import org.opensimulationsystems.cabsf.sim.core.api.SimulationRunGroupContext;

import repast.simphony.context.Context;
import repast.simphony.engine.controller.Controller;
import repast.simphony.parameter.DefaultParameters;
import repast.simphony.random.RandomHelper;

// TODO: Auto-generated Javadoc
/**
 * The Repast Simphony Adapter API context factory. This class is the entry
 * point for RepastS simulations to use the Common Agent-Based Simulation
 * Framework.
 *
 * Note: This API was originally intended to only be used at the RepastS
 * simulation-level, not at the individual RepastS agent-level. However, in the
 * current version, the individual agents do use this API directly in addition
 * to the agent-level API (RepastS Agent API). This may change in newer versions
 * so that agents only use instantiate the agent-level API.
 *
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class RepastS_SimulationAdapterAPI {

    /** The instance. */
    private static RepastS_SimulationAdapterAPI instance =
            new RepastS_SimulationAdapterAPI();

    /**
     * The API singleton for adaptor.
     *
     * @return single instance of RepastS_SimulationAdapterAPI
     */
    public static RepastS_SimulationAdapterAPI getInstance() {
        return instance;
    }

    /** The CABSF-wide Simulation API. Intended to be used across ABMS systems. */
    private final SimulationAPI simulationAPI =
            SimulationAPI.getInstance();

    /** The simulation tool name to set in simulation API. */
    private final String simToolNameToSetInSimulationAPI =
            "REPAST_SIMPHONY";

    /**
     * Instantiates a new repast s_ simulation adapter api.
     */
    private RepastS_SimulationAdapterAPI() {
        super();
    }

    /**
     * Apply rssr parameters fix.
     *
     * @param controller
     *            the controller
     * @param scenarioDir
     *            the scenario dir
     * @param secondProgramArgument
     *            the cabsf configuration file name
     * @return the default parameters
     * @throws JDOMException
     *             the JDOM exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */

    public DefaultParameters applyRssrParametersFix(final Controller controller,
            final File scenarioDir, final String secondProgramArgument)
            throws JDOMException, IOException {
        if (!shouldApplypParametersFix(secondProgramArgument)) {
            return null;
        }

        /** The element filter. */
        final Filter<Element> elementFilter =
                new org.jdom2.filter.ElementFilter();

        // Set the Parameters across all simulation runs of this simulation
        final Document repastSconfigFile =
                MessagingUtilities.createDocumentFromFileSystemPath(scenarioDir
                        .getAbsolutePath() + "/parameters.xml");
        assert (repastSconfigFile != null);

        final List<Element> parameters =
                (List<Element>) XMLUtilities.executeXPath(repastSconfigFile,
                        "/parameters", null, elementFilter);
        // TODO: Support more than 1 Distributed System
        assert (parameters.size() == 1);

        final List<Element> parameterElements =
                parameters.get(0).getChildren();
        final DefaultParameters defaultParameters =
                new DefaultParameters();

        for (int i =
                0; i < parameterElements.size(); i++) {
            // Handle "int" parameters only
            final String paramName =
                    parameterElements.get(i).getAttributeValue("name");
            final String displayName =
                    parameterElements.get(i).getAttributeValue("displayName");
            final String defaultValue =
                    parameterElements.get(i).getAttributeValue("defaultValue");

            final Random rand =
                    new Random();
            final Number num =
                    rand.nextInt(Integer.MAX_VALUE);

            if (defaultValue.equals("__NULL__")) {
                defaultParameters.addParameter(paramName, displayName, Number.class,
                // null, true);
                        num, true);
            } else if (parameterElements.get(i).getAttributeValue("type").equals("int")) {
                defaultParameters.addParameter(paramName, displayName, Number.class,
                        Integer.parseInt(defaultValue), true);
            } else if (parameterElements.get(i).getAttributeValue("type")
                    .equalsIgnoreCase("String")) {
                defaultParameters.addParameter(paramName, displayName, String.class,
                        defaultValue, true);
            }
        }
        controller.runParameterSetters(defaultParameters);

        return defaultParameters;

    }

    /**
     * Apply rssr random seed context add fix.
     *
     * @param controller
     *            the controller
     * @param scenarioDir
     *            the scenario dir
     * @param secondProgramArgument
     *            the second program argument
     * @return true, if successful
     * @throws JDOMException
     *             the JDOM exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public boolean applyRssrRandomSeedContextAddFix(final Controller controller,
            final File scenarioDir, final String secondProgramArgument)
            throws JDOMException, IOException {
        if (!shouldApplyRandomSeedContextAddFix(secondProgramArgument)) {
            return false;
        }

        RandomHelper.nextDouble();
        RandomHelper.nextDouble();

        return true;
    }

    /**
     * Convert first program argument to configuration file name.
     *
     * @param secondProgramArgument
     *            the second program argument
     * @return the the configuration file name, if it is a configuration file.
     *         Otherwise return null if it's just flags that are being passed
     *         in, and the simulation is being run CABSF-disabled.
     */
    public String convertFirstProgramArgumentToConfigurationFileName(
            final String secondProgramArgument) {
        if (secondProgramArgument.toUpperCase().contains(
                "RssrParametersFix".toUpperCase())
                || secondProgramArgument.toUpperCase().contains(
                        "RssrRandomSeedContextAddFix".toUpperCase())) {
            return null;
        } else {
            return secondProgramArgument;
        }

    }

    /**
     * Initializes the Common Agent-Based Simulation Framework on the RepastS
     * simulation side, based on the supplied CABSF configuration property file.
     * Calls the simulation-adaptor-wide Simulation API to initialize the
     * simulation run group.
     *
     * NOTE: The current version initialization is hard coded to only work with
     * the two reference implementation simulations. The CABSF configuration
     * filename is used to switch the configuration based on the simulation. In
     * the future, this filename will be used to read an XML configuration file
     * from the file system so that the CABSF can be used for any simulation.
     *
     * @param cabsfConfigurationFileName
     *            the framework configuration file name
     * @return the repast s_ simulation run group context
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public RepastS_SimulationRunGroupContext initializeAPI(
            final String cabsfConfigurationFileName) throws IOException {

        final SimulationRunGroupContext simulationRunGroupContext =
                simulationAPI.initializeAPI(cabsfConfigurationFileName,
                        simToolNameToSetInSimulationAPI);

        // Set the Repast-Simphony-specific objects, using the Decorator Pattern
        final RepastS_SimulationRunGroupContext repastS_SimulationRunGroupContext =
                new RepastS_SimulationRunGroupContext(simulationRunGroupContext);

        return repastS_SimulationRunGroupContext;
    }

    // LOW: Allow the same simulation agent class to be both distributed and
    // non-distributed.
    /**
     * Initializes a single CABSF Repast Simphony simulation run. This method
     * configures the (already-created in the simulation API initialization)
     * AgentMapping objects.
     *
     * @param nativeRepastScontextForThisRun
     *            the native repast context for this run
     * @param repastS_SimulationRunGroupContext
     *            the repast s_ simulation run group context
     * @param executeHandshake
     *            the execute handshake
     * @return the repast s_ simulation run context
     */
    public RepastS_SimulationRunContext initializeSimulationRun(
            final Context<Object> nativeRepastScontextForThisRun,
            final RepastS_SimulationRunGroupContext repastS_SimulationRunGroupContext,
            final boolean executeHandshake) {
        final SimulationRunContext simulationRunContext =
                simulationAPI.initializeSimulationRun(nativeRepastScontextForThisRun,
                        repastS_SimulationRunGroupContext.getSimulationRunGroupContext());

        // User Decorator Pattern for RepastS_SimulationRunContext
        final RepastS_SimulationRunContext repastS_SimulationRunContext =
                new RepastS_SimulationRunContext(simulationRunContext);
        repastS_SimulationRunContext
                .setRepastContextForThisRun(nativeRepastScontextForThisRun);

        repastS_SimulationRunContext
                .setRepastRunGroupContext(repastS_SimulationRunGroupContext);

        // Make the context available to the agents in the Repast model
        nativeRepastScontextForThisRun.add(repastS_SimulationRunContext);

        // LOW: Support multiple Simulation Run Groups. For now just assume that
        // there's one.
        // LOW: Handle multiple distributed systems
        // TODO: Move distributed system manager to main level? same for on the
        // distributed side (simulation engine manager)
        repastS_SimulationRunContext.getSimulationDistributedSystemManagers().iterator()
                .next().createAgentMappingObjects();

        boolean atLeastOneMappingPerformed =
                false;

        // Find all of the individual RepastS agents to be mapped in the
        // framework to distributed agents
        // TODO: Move all of this to the main simulation API to simplify Adapter
        // code.
        // (Same for JADE API side)
        @SuppressWarnings({ "rawtypes" })
        final Iterable<Class> simulationAgentsClasses =
                nativeRepastScontextForThisRun.getAgentTypes();

        // For each simulation agent class
        for (@SuppressWarnings("rawtypes")
        final Class simulationAgentClass : simulationAgentsClasses) {
            // LOW: Allow individual simulation agent classes to be either
            // simulation-only
            // or
            // representations of distributed agents.
            // TODO: Handle multiple distributed systems
            if (repastS_SimulationRunContext.getSimulationDistributedSystemManagers()
                    .iterator().next().isAgentClassDistributedType(simulationAgentClass)) {
                @SuppressWarnings("unchecked")
                final Class<Object> simulationAgentClazz =
                        simulationAgentClass;
                final Iterable<Object> simulationAgentsInSingleClass =
                        nativeRepastScontextForThisRun
                                .getAgentLayer(simulationAgentClazz);

                // TODO: Look into handling multiple classes in the mapping
                /*
                 * if (repastS_SimulationRunContext.getSimulationRunContext()
                 * .getSimulationRunGroupContext()
                 * .getSimulationRunGroupConfiguration()
                 * .getSimulationAgentsBelongToOneClass()) {
                 */

                // For an agent class type, for each individual simulation
                // agent, map to an existing free AgentMapping object
                for (final Object simulationAgent : simulationAgentsInSingleClass) {
                    atLeastOneMappingPerformed =
                            true;
                    System.out.println("Attempting to map: " + simulationAgent);
                    mapSimulationSideAgent(simulationAgent,
                            repastS_SimulationRunContext.getSimulationRunContext());
                }

                // TODO: Support partial mappings for one class
                // Elsewhere we check for the opposite mismatch (agents in the
                // CABSF
                // configuration are less than the number of agents in the
                // simulation
                // runtime.
                if (repastS_SimulationRunContext.getSimulationRunGroupConfiguration()
                        .getAgentsReadyForSimulationSideMapping().size() > 0) {
                    throw new CabsfRuntimeException(
                            "The number of agent models to be mapped in the CABSF configuration exceed the number of agents instantiated in the simulation runtime.");
                }
                ;

            } else {
                continue; // Not an agent we need to map.
            }
        }

        if (atLeastOneMappingPerformed != true) {
            throw new CabsfRuntimeException(
                    "No mapping was performed of simulation agent(s) to distributed autonomous agent(s) and agent model IDs.  At one mapping is expected to be performed.  Your configuration file's agent mapping section may not be set up properly.");
        }

        if (executeHandshake) {
            // TODO: Move this whole section to the main simulation API?
            // 1 - Wait for the command from the simulation administrator to
            // start
            // the simulation
            final FRAMEWORK_COMMAND fc =
                    repastS_SimulationRunContext
                            .readFrameworkMessageFromSimulationAdministrator()
                            .getFrameworkToSimulationEngineCommand();
            if (fc != FRAMEWORK_COMMAND.START_SIMULATION) {
                throw new CabsfMessagingRuntimeException(
                        "Did not understand the message from the simulation administrator");
            }

            // 2 - Message the distributed systems that the simulation has
            // started
            // and is ready to accept messages from the distributed agents.
            final FrameworkMessage msg =
                    new FrameworkMessageImpl(SYSTEM_TYPE.SIMULATION_ENGINE,
                            SYSTEM_TYPE.DISTRIBUTED_SYSTEM,
                            repastS_SimulationRunContext
                                    .getBlankCachedMessageExchangeTemplate());
            msg.setFrameworkToDistributedSystemCommand(FRAMEWORK_COMMAND.START_SIMULATION);
            // TODO: Loop through the multiple distributed systems
            repastS_SimulationRunContext.messageDistributedSystems(msg,
                    repastS_SimulationRunContext.getSimulationRunContext());

            // Wait for distributed system to confirm that simulation is ready
            // to begin
            final STATUS st =
                    repastS_SimulationRunContext
                            .readFrameworkMessageFromDistributedSystem().getStatus();
            // TODO: Identify which distributed system caused the error.
            // TODO: Set these up as checked exceptions?
            if (st != STATUS.READY_TO_START_SIMULATION) {
                throw new CabsfMessagingRuntimeException(
                        "Did not understand the message from the simulation distributed system.");
            }
        }

        // The distributed agent (models) have previously been mapped.
        // Now we're ready to perform the steps in the simulation.
        return repastS_SimulationRunContext;
    }

    /**
     * After the Simulation and Common Framework are initialized, the Simulation
     * Adaptor API (or child class) is initialized, and prior to executing a
     * simulation run, this method must be called to configure the
     * simulation-side of the AgentMappings for one type (class) of simulation
     * agent. If multiple agent classes are distributed, this method must be
     * called for each type.
     *
     * @param simulationAgent
     *            the simulation agent
     * @param simulationRunContext
     *            the simulation run context
     */
    private void mapSimulationSideAgent(final Object simulationAgent,
            final SimulationRunContext simulationRunContext) {
        simulationAPI.mapSimulationSideAgent(simulationAgent, simulationRunContext);
    }

    /**
     * Map simulation side agents.
     *
     * @param agentsOfOneType
     *            the agents of one type
     * @param simulationRunContext
     *            the simulation run context
     * @see mapSimulationSideAgent
     */
    @SuppressWarnings("unused")
    private void mapSimulationSideAgents(final Iterable<Object> agentsOfOneType,
            final SimulationRunContext simulationRunContext) {
        for (final Object simulationAgent : agentsOfOneType) {
            mapSimulationSideAgent(simulationAgent, simulationRunContext);
        }
    }

    /**
     * Returns whether the parameters fix should be applied, depending on the
     * presence of the flag in the configuration file, or the second RSSR
     * argument.
     *
     * @param cabsfConfigurationDocumentStr
     *            the cabsf configuration document str
     * @return true, if successful
     * @throws JDOMException
     *             the JDOM exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private boolean shouldApplypParametersFix(final String cabsfConfigurationDocumentStr)
            throws JDOMException, IOException {
        if (cabsfConfigurationDocumentStr != null
                && cabsfConfigurationDocumentStr.toUpperCase().contains(
                        "ApplyRssrParametersFix".toUpperCase())) {
            return true;
        }
        if (cabsfConfigurationDocumentStr != null
                && cabsfConfigurationDocumentStr.toUpperCase().contains(
                        "NoRssrParametersFix".toUpperCase())) {
            return false;
        }
        if (cabsfConfigurationDocumentStr != null
                && cabsfConfigurationDocumentStr.toUpperCase().contains(
                        "RssrRandomSeedContextAddFix".toUpperCase())) {
            return false;
        }

        // The checks above are only for RSSR non-CABSF-enabled runs. Below, we
        // have to check the configuration file.
        final Filter<Element> elementFilter =
                new org.jdom2.filter.ElementFilter();
        /** The namespace str. */
        final String namespaceStr =
                "http://www.opensimulationsystems.org/cabsf/schemas/CabsfMessageExchange/0.1.0";

        /** The namespace. */

        final Document configFileDoc =
                MessagingUtilities
                        .createDocumentFromFileSystemPath(cabsfConfigurationDocumentStr);
        assert (configFileDoc != null);

        final List<Element> applyRssrParametersFixElements =
                (List<Element>) XMLUtilities
                        .executeXPath(
                                configFileDoc,
                                "/x:CabsfSimulationConfiguration/x:SimulationEngineSpecificConfigurations/x:AllSimulationRuns/x:SimulationEngineSpecific/x:ApplyRssrParametersFix",
                                namespaceStr, elementFilter);

        if (applyRssrParametersFixElements.size() >= 1) {
            return applyRssrParametersFixElements.get(0).getText()
                    .equalsIgnoreCase("true");
        }
        return false;
    }

    /**
     * Returns whether the random see fix should be applied, depending on the
     * presence of the flag in the configuration file, or the second RSSR
     * argument.
     *
     * @param cabsfConfigurationDocumentStr
     *            the cabsf configuration document str
     * @return true, if successful
     * @throws JDOMException
     *             the JDOM exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private boolean shouldApplyRandomSeedContextAddFix(
            final String cabsfConfigurationDocumentStr) throws JDOMException, IOException {
        if (cabsfConfigurationDocumentStr != null
                && cabsfConfigurationDocumentStr.toUpperCase().contains(
                        "ApplyRssrRandomSeedContextAddFix".toUpperCase())) {
            return true;
        }
        if (cabsfConfigurationDocumentStr != null
                && cabsfConfigurationDocumentStr.toUpperCase().contains(
                        "NoRssrRandomSeedContextAddFix".toUpperCase())) {
            return false;
        }
        if (cabsfConfigurationDocumentStr != null
                && cabsfConfigurationDocumentStr.toUpperCase().contains(
                        "RssrParametersFix".toUpperCase())) {
            return false;
        }

        // The checks above are only for RSSR non-CABSF-enabled runs. Below, we
        // have to check the configuration file.
        final Filter<Element> elementFilter =
                new org.jdom2.filter.ElementFilter();
        /** The namespace str. */
        final String namespaceStr =
                "http://www.opensimulationsystems.org/cabsf/schemas/CabsfMessageExchange/0.1.0";

        /** The namespace. */

        final Document configFileDoc =
                MessagingUtilities
                        .createDocumentFromFileSystemPath(cabsfConfigurationDocumentStr);
        assert (configFileDoc != null);

        final List<Element> applyRssrParametersFixElements =
                (List<Element>) XMLUtilities
                        .executeXPath(
                                configFileDoc,
                                "/x:CabsfSimulationConfiguration/x:SimulationEngineSpecificConfigurations/x:AllSimulationRuns/x:SimulationEngineSpecific/x:ApplyRssrRandomSeedContextAddFix",
                                namespaceStr, elementFilter);

        if (applyRssrParametersFixElements.size() >= 1) {
            return applyRssrParametersFixElements.get(0).getText()
                    .equalsIgnoreCase("true");
        }
        return false;
    }
}