package org.opensimulationsystems.cabsf.distsys.mas.runners.jade;

import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.opensimulationsystems.cabsf.common.model.AgentMapping;
import org.opensimulationsystems.cabsf.common.model.SYSTEM_TYPE;
import org.opensimulationsystems.cabsf.common.model.cabsfexceptions.CabsfCheckedException;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FRAMEWORK_COMMAND;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FrameworkMessage;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FrameworkMessageImpl;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.JADE_MAS_AdapterAPI;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.JADE_MAS_RunContext;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.JADE_MAS_RunGroupContext;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.nativeagents.CabsfDistributedJADEagentWrapper;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.nativeagents.NativeDistributedAutonomousAgent;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.nativeagents.NativeJADEMockContext;

/**
 * The JADE agent implementing the CABSF JADE MAS Adapter API to enable JADE
 * agents to communicate with ABMS systems such as Repast Simphony.
 *
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class JADE_Controller_Agent extends jade.core.Agent {

    /**
     * The Behaviour class that specifies the specific actions that the JADE
     * Controller Agent takes when receiving a new message from another agent.
     * This functionality is described in the JADE documentation.
     *
     * @author Jorge Calderon
     * @version 0.1
     * @since 0.1
     */
    private class JadeControllerAgentServer extends CyclicBehaviour {

        /*
         * (non-Javadoc)
         *
         * @see jade.core.behaviours.Behaviour#action()
         */
        @Override
        public void action() {
            final MessageTemplate mt =
                    MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            final ACLMessage aclMsg =
                    myAgent.receive(mt);
            if (aclMsg != null) {
                // Message matching the template has come in.
                final String msgStr =
                        aclMsg.getContent();

                FrameworkMessage message =
                        null;
                try {
                    message =
                            new FrameworkMessageImpl(SYSTEM_TYPE.DISTRIBUTED_SYSTEM,
                                    SYSTEM_TYPE.SIMULATION_ENGINE, msgStr);
                } catch (final CabsfCheckedException e) {
                    // FIXME: Remove all of the agents/shutdown the MAS?
                    System.out
                    .println("[JADE Controller Agent] Error converting message from a distributed JADE agent to the JADE Controller Agent: "
                            + msgStr + commonFatalStr);
                    doDelete();
                    e.printStackTrace();
                    System.exit(1);
                }

                System.out
                .println("[JADE Controller Agent] Received the distributed autonomous agent (model) decision. Forwarding to the simulation engine (agent)");

                jade_MAS_RunContext.sendMessageToSimulationEngine(message,
                        jade_MAS_RunContext.getDistSysRunContext());

                final FRAMEWORK_COMMAND fc =
                        getJade_MAS_RunContext()
                        .waitForAndProcessSimulationEngineMessageAfterHandshake();
                if (fc == FRAMEWORK_COMMAND.STOP_SIMULATION) {
                    System.out
                    .println("[JADE Controller Agent] Received Stop Simulation Command from Simulation Engine.  Listening for new simulation run");
                    setupNewSimulationRun(simulationRunsLeft == 0);
                }

            } else {
                block(); // Save CPU cycles by not executing this thread until
                // new messages come in.
            }

        }
    } // End of inner class

    // All of these fields below are available to the inner class

    /** The setJade_MAS_RunContext group context. */
    private static JADE_MAS_RunGroupContext jade_MAS_RunGroupContext;

    /** The jade_ MAS_ adapter api. */
    static private JADE_MAS_AdapterAPI jade_MAS_AdapterAPI;

    /** The setJade_MAS_RunContext context. */
    private JADE_MAS_RunContext jade_MAS_RunContext;

    /** The framework configuration file name. */
    String frameworkConfigurationFileName =
            null;

    /** The new simulation run. */
    boolean newSimulationRun =
            true;

    /** The jade agents. */
    private final Set<NativeDistributedAutonomousAgent> mappedJadeAgents =
            new HashSet<NativeDistributedAutonomousAgent>();

    /** The number of agents. */
    private long expectedNumOfAgentsFromConfig;

    /** The simulation runs left. */
    private long simulationRunsLeft;

    /** The expected num of simulation runs. */
    private Long expectedNumOfSimulationRuns;
    private final String commonFatalStr =
            "\r\n[JADE Controller Agent] All other JADE agents in the JADE platform should be terminating, unless the JADE platform"
                    + " was started in a separate process.  In that case, the JADE agents may need to be terminated manually."
                    + "  In Repast Simphony/Eclipse, manually terminate by clicking the red square for each process.";

    /**
     * Agent i ds to string.
     *
     * @param agentSet
     *            the agent set
     * @return the string
     */
    private String agentIDsToString(final HashSet<String> agentSet) {
        final StringBuilder sb =
                new StringBuilder();
        for (final String str : agentSet) {
            sb.append(str + " ");
        }
        return sb.toString();
    }

    /**
     * Find agents.
     *
     * @param template
     *            the template
     */
    private void findAgents(final DFAgentDescription template) {
        DFAgentDescription[] jadeAgentsFound =
                null;
        try {
            jadeAgentsFound =
                    DFService.search(this, template);
            if (jadeAgentsFound.length > expectedNumOfAgentsFromConfig) {
                System.out
                .println("[JADE Controller Agent] Expected "
                        + String.valueOf(expectedNumOfAgentsFromConfig)
                        + " agents based on the configuration, but there were actually "
                        + String.valueOf(jadeAgentsFound.length)
                        + " JADE agents found. JADE agents with no representations in the simulation engine is not yet supported.  Terminating."
                        + commonFatalStr);
                // FIXME: Remove all of the agents/shutdown the MAS?
                doDelete();
                System.exit(1);
            }

            System.out.println("[JADE Controller Agent] Found "
                    + String.valueOf(mappedJadeAgents.size())
                    + " total agents so far out of "
                    + String.valueOf(expectedNumOfAgentsFromConfig) + " expected.");
            if (expectedNumOfAgentsFromConfig != mappedJadeAgents.size()) {
                System.out
                .println("[JADE Controller Agent] Will retry looking for all expected agents.");
            }
            Thread.sleep(1000);
        } catch (final FIPAException fe) {
            // FIXME: Remove all of the agents/shutdown the MAS?
            System.out
            .println("[JADE Controller Agent] FIPA error in finding the distributed JADE agents.  Terminating."
                    + commonFatalStr);
            doDelete();
            fe.printStackTrace();
            System.exit(1);
        } catch (final InterruptedException e) {
            // FIXME: Remove all of the agents/shutdown the MAS?
            System.out.println("[JADE Controller Agent] Thread interrupted. Terminating."
                    + commonFatalStr);
            doDelete();
            e.printStackTrace();
            System.exit(1);
        }

        // Create a CabsfDistributedJADEagentWrapper concrete object for
        // each JADE agent. The purpose is to hold both the JADE AID and
        // CABSF-specific identifiers in a single object.
        // TODO: Dynamically handle naming
        // :DistributedSystemAutonomousAgent1,
        // DistributedSystemAutonomousAgent1MODEL
        // FIXME: Number of JADE agents exceed expected number.
        // FIXME: Agent names don't match
        System.out
        .println("[JADE Controller Agent] Validating JADE agents against the agents defined in the configuration file.  Agent models are not validated by the JADE Controller Agent.");
        final HashMap validationResults =
                validateDistributedAutonomousAgents(jadeAgentsFound);

        if (validationResults.size() != 0) {
            final Map.Entry entr =
                    (Map.Entry) validationResults.entrySet().iterator().next();
            System.out.println("[JADE Controller Agent] "
                    + (String) validationResults.keySet().iterator().next() + ": "
                    + agentIDsToString((HashSet<String>) entr.getValue())
                    + commonFatalStr);
            // FIXME: Remove all of the other agents/shutdown the MAS?
            doDelete();
            System.exit(1);
        } else {
            System.out
            .println("[JADE Controller Agent] Successfully validated that the JADE agents are in the configuration and vice versa.");
        }
        final StringBuilder strb =
                new StringBuilder();
        strb.append("[JADE Controller Agent] All agents found: "
                + String.valueOf(jadeAgentsFound.length)
                + " agents.  Local name up to the the '@', complete name printed: "
                + "\r\n");

        for (int i =
                0; i < jadeAgentsFound.length; ++i) {
            String.valueOf(i);
            // TODO: Get Model Name from the configuration file?
            final CabsfDistributedJADEagentWrapper agent =
                    new CabsfDistributedJADEagentWrapper(jadeAgentsFound[i].getName(),
                            "DistSys1", jadeAgentsFound[i].getName().getLocalName(),
                            jadeAgentsFound[i].getName().getLocalName() + "MODEL",
                            "MODELNAME_PLACEHOLDER", this);
            mappedJadeAgents.add(agent);
            strb.append(jadeAgentsFound[i].getName().getName() + "\r\n");
        }
        System.out.println(strb);

    }

    /**
     * Gets the jade_ MAS_ adapter api.
     *
     * @return the jade_ MAS_ adapter api
     */
    public final JADE_MAS_AdapterAPI getJade_MAS_AdapterAPI() {
        return jade_MAS_AdapterAPI;
    }

    /**
     * Gets the setJade_MAS_RunContext context.
     *
     * @return the setJade_MAS_RunContext context
     */
    public final JADE_MAS_RunContext getJade_MAS_RunContext() {
        return jade_MAS_RunContext;
    }

    /**
     * Sets the setJade_MAS_RunContext context.
     *
     * @param jade_MAS_RunContext
     *            the new setJade_MAS_RunContext context
     */
    public void setJade_MAS_RunContext(final JADE_MAS_RunContext jade_MAS_RunContext) {
        this.jade_MAS_RunContext =
                jade_MAS_RunContext;
    }

    /*
     * Setup tasks run once
     *
     * @see jade.core.Agent#setup()
     */
    /*
     * (non-Javadoc)
     *
     * @see jade.core.Agent#setup()
     */
    @Override
    protected void setup() {

        // Printout a welcome message
        System.out.println("JADE Controller Agent " + getAID().getName() + " is ready.");

        final Object[] args =
                getArguments();
        if (args != null && args.length > 0) {
            frameworkConfigurationFileName =
                    (String) args[0];
            System.out
            .println("[JADE Controller Agent] CABSF JADE Controller Agent Configuration file "
                    + frameworkConfigurationFileName);

            jade_MAS_AdapterAPI =
                    JADE_MAS_AdapterAPI.getInstance();
            jade_MAS_RunGroupContext =
                    null;
            try {
                jade_MAS_RunGroupContext =
                        jade_MAS_AdapterAPI.initializeAPI(frameworkConfigurationFileName);
            } catch (final IOException e) {
                // FIXME: Remove all of the agents/shutdown the MAS?
                System.out
                .println("[JADE Controller Agent] Error in initializing the JADE Controller Agent"
                        + commonFatalStr);
                doDelete();
                e.printStackTrace();
                System.exit(1);
            }

            expectedNumOfAgentsFromConfig =
                    jade_MAS_RunGroupContext.getDistSysRunGroupContext()
                    .getRunGroupConfiguration()
                    .getNumberOfDistributedAutonomousAgents();

            expectedNumOfSimulationRuns =
                    jade_MAS_RunGroupContext.getDistSysRunGroupContext()
                    .getRunGroupConfiguration().getNumberOfSimulationRuns();

            // Register the JADE Controller agent in the yellow pages for the
            // distributed JADE agents to find
            final DFAgentDescription dfd =
                    new DFAgentDescription();
            dfd.setName(getAID());
            final ServiceDescription sd =
                    new ServiceDescription();
            sd.setType("jade-CABSF-agents");
            sd.setName("jade-controller-agent");
            dfd.addServices(sd);
            try {
                DFService.register(this, dfd);
            } catch (final FIPAException fe) {
                // FIXME: Remove all of the agents/shutdown the MAS?
                System.out.println("[JADE Controller Agent]"
                        + " Error registering this JADE agent in the yellow pages."
                        + commonFatalStr);
                doDelete(); // Cleanup and remove this agent from the MAS.
                fe.printStackTrace();
                System.exit(1);
            }

            addBehaviour(new JadeControllerAgentServer());

            setupNewSimulationRun(true);

        } else {
            // Make the agent terminate
            System.out
            .println("[JADE Controller Agent] The JADE Controller Agent configuration file must be provided as an argument"
                    + commonFatalStr);
            doDelete();
            System.exit(1);
        }

    }

    /**
     * Setup new simulation run.
     *
     * @param resetRunGroup
     *            the new up new simulation run
     */
    private void setupNewSimulationRun(boolean resetRunGroup) {
        final DFAgentDescription template =
                new DFAgentDescription();
        final ServiceDescription sd =
                new ServiceDescription();
        sd.setType("jade-CABSF-agents");
        sd.setName("non-admin-agents");
        template.addServices(sd);

        while (true) {
            if (resetRunGroup) {
                simulationRunsLeft =
                        expectedNumOfSimulationRuns;
            }
            simulationRunsLeft--;

            // FIXME: Handle JADE agents that do not have representations in
            // Repast,
            // based on
            // the configuration.
            // Update the list of JADE agents
            System.out.println("[JADE Controller Agent] Looking for "
                    + expectedNumOfAgentsFromConfig + " total JADE agents.");
            while (expectedNumOfAgentsFromConfig != mappedJadeAgents.size()) {
                findAgents(template);
            }

            final JADE_MAS_RunContext jade_MAS_RunContext =
                    jade_MAS_AdapterAPI.initializeSimulationRun(
                            new NativeJADEMockContext(), jade_MAS_RunGroupContext, null,
                            mappedJadeAgents, resetRunGroup);
            setJade_MAS_RunContext(jade_MAS_RunContext);
            resetRunGroup =
                    false;

            // Get the message from the simulation engine, send it on to the
            // appropriate distributed JADE agent.
            final FRAMEWORK_COMMAND fc =
                    getJade_MAS_RunContext()
                    .waitForAndProcessSimulationEngineMessageAfterHandshake();
            if (fc == FRAMEWORK_COMMAND.STOP_SIMULATION) {
                System.out
                .println("[JADE Controller Agent] Received Stop Simulation Command from Simulation Engine.  Listening for new simulation run");
                // Reset back to listen for handshake.
                resetRunGroup =
                        true;
                // Continue loop to get ready to listen for a new simulation
                // run.
            } else {
                // Exit this method. Now switch from listing to the simulation
                // engine to listening from the JADE agents (through the action
                // method.)
                break;
            }
        }
    }

    /**
     * Validates that all of the actual distributed autonomous agents/JADE
     * agents started are in the configuration and vice versa. This method does
     * not validate that the agent models within the distributed autonomous
     * agents are active. That is the responsibility of the the individual
     * distributed autonomous agent/JADE agent.
     *
     * @param result
     *            the result
     * @return a hash map containing one entry where the key describes the error
     *         and the value is a hashset containing the distributed autonomous
     *         agent IDs that apply to the error (such as IDs that are in the
     *         configuration but not started in JADE, or vice versa).
     *
     *         Returns an empty HashMap if everything validates.
     */
    private HashMap<String, HashSet<String>> validateDistributedAutonomousAgents(
            final DFAgentDescription[] result) {
        final HashSet<AgentMapping> configurationAgentMappings =
                new HashSet<AgentMapping>(jade_MAS_RunGroupContext
                        .getDistSysRunGroupContext().getRunGroupConfiguration()
                        .createAgentMappingObjects());

        final HashSet<String> actualDistributedAutonomousAgentIDs1 =
                new HashSet<String>();
        final HashSet<String> configDistributedAutonomousAgentIDs =
                new HashSet<String>();
        final HashSet<String> actualDistributedAutonomousAgentIDs2 =
                new HashSet<String>();

        final HashMap<String, HashSet<String>> returnMap =
                new HashMap<String, HashSet<String>>();

        // LOW: Look for options to handle arrays/collections larger than
        // Integer.MAX_VALUE
        for (int i =
                0; i < result.length; i++) {
            actualDistributedAutonomousAgentIDs1.add(result[i].getName().getLocalName());
            actualDistributedAutonomousAgentIDs2.add(result[i].getName().getLocalName());
        }

        for (final AgentMapping am : configurationAgentMappings) {
            configDistributedAutonomousAgentIDs.add(am.getSoftwareAgentID());
        }

        actualDistributedAutonomousAgentIDs1
        .removeAll(configDistributedAutonomousAgentIDs);
        if (actualDistributedAutonomousAgentIDs1.size() > 0) {
            returnMap
            .put("Distributed Autonomous Agents were started but were not specified in the configuration.",
                    actualDistributedAutonomousAgentIDs1);
        }

        configDistributedAutonomousAgentIDs
        .removeAll(actualDistributedAutonomousAgentIDs2);
        if (configDistributedAutonomousAgentIDs.size() > 0) {
            returnMap
            .put("Distributed Autonomous Agents were in the configuration but were not started.",
                    configDistributedAutonomousAgentIDs);
        }

        return returnMap;

    }
}
