package org.opensimulationsystems.cabsf.distsys.mas.adapter.runners.jade;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.opensimulationsystems.cabsf.common.model.cabsfexceptions.CabsfInitializationRuntimeException;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FRAMEWORK_COMMAND;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FrameworkMessage;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.Jade_AdapterAPI;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.Jade_RunContext;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.Jade_RunGroupContext;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.JadeControllerInterface;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.nativeagents.NativeDistributedAutonomousAgent;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.nativeagents.NativeJADEMockContext;
import org.opensimulationsystems.cabsf.distsys.mas.mocks.MockHumanJADE_Agent;

public class Mock_JADE_Controller_Agent implements JadeControllerInterface {
    private static Jade_RunGroupContext jade_MAS_RunGroupContext;

    static private Jade_AdapterAPI jade_MAS_AdapterAPI;

    private static String frameworkConfigurationFileName;

    private static Set<NativeDistributedAutonomousAgent> getInitialSetOfNativeJADEagents() {
        final Set<NativeDistributedAutonomousAgent> st = new HashSet<NativeDistributedAutonomousAgent>();

        NativeDistributedAutonomousAgent nativeDistributedAutonomousAgent = new MockHumanJADE_Agent(
                "DistSys1", "DistributedSystemAutonomousAgent1",
                "DistributedSystemAutonomousAgent1MODEL", "Human",
                frameworkConfigurationFileName);
        st.add(nativeDistributedAutonomousAgent);
        nativeDistributedAutonomousAgent = new MockHumanJADE_Agent("DistSys1",
                "DistributedSystemAutonomousAgent2",
                "DistributedSystemAutonomousAgent2MODEL", "Human",
                frameworkConfigurationFileName);
        st.add(nativeDistributedAutonomousAgent);
        nativeDistributedAutonomousAgent = new MockHumanJADE_Agent("DistSys1",
                "DistributedSystemAutonomousAgent3",
                "DistributedSystemAutonomousAgent3MODEL", "Human",
                frameworkConfigurationFileName);
        st.add(nativeDistributedAutonomousAgent);
        nativeDistributedAutonomousAgent = new MockHumanJADE_Agent("DistSys1",
                "DistributedSystemAutonomousAgent4",
                "DistributedSystemAutonomousAgent4MODEL", "Human",
                frameworkConfigurationFileName);
        st.add(nativeDistributedAutonomousAgent);
        nativeDistributedAutonomousAgent = new MockHumanJADE_Agent("DistSys1",
                "DistributedSystemAutonomousAgent5",
                "DistributedSystemAutonomousAgent5MODEL", "Human",
                frameworkConfigurationFileName);
        st.add(nativeDistributedAutonomousAgent);
        nativeDistributedAutonomousAgent = new MockHumanJADE_Agent("DistSys1",
                "DistributedSystemAutonomousAgent6",
                "DistributedSystemAutonomousAgent6MODEL", "Human",
                frameworkConfigurationFileName);
        st.add(nativeDistributedAutonomousAgent);
        return st;
    }

    public static void main(final String[] args) {
        String frameworkConfigurationFileName = null;
        if (args.length >= 1) {
            frameworkConfigurationFileName = args[0];
        }
        // TODO: Add Validation of CABSF configuration file
        if (frameworkConfigurationFileName == null) {
            throw new CabsfInitializationRuntimeException(
                    "The configuration directory must be provided as a program argument.");
        }

        jade_MAS_AdapterAPI = Jade_AdapterAPI.getInstance();
        jade_MAS_RunGroupContext = null;

        try {
            jade_MAS_RunGroupContext = jade_MAS_AdapterAPI
                    .initializeAPI(frameworkConfigurationFileName);
        } catch (final IOException e) {
            throw new CabsfInitializationRuntimeException(
                    "Error initializing the JADE Controller Agent", e);
        }

        // Initialize simulation run
        // TODO: Fix the native JADE context
        final Mock_JADE_Controller_Agent jade_Controller_Agent = new Mock_JADE_Controller_Agent(
                frameworkConfigurationFileName);
        final Set<NativeDistributedAutonomousAgent> st = getInitialSetOfNativeJADEagents();

        jade_Controller_Agent.listenLoop(st);

    }

    private Jade_RunContext jade_MAS_RunContext;

    private boolean resetRunGroup = true;

    public Mock_JADE_Controller_Agent(final String frameworkConfigurationFileName) {
        super();
        this.frameworkConfigurationFileName = frameworkConfigurationFileName;
    }

    public Jade_AdapterAPI getJade_MAS_AdapterAPI() {
        return jade_MAS_AdapterAPI;
    }

    public Jade_RunContext getJade_MAS_RunContext() {
        return jade_MAS_RunContext;
    }

    private void listenLoop(final Set<NativeDistributedAutonomousAgent> st) {
        while (true) {
            try {
                // Handshake with the simulation system for a new simulation
                // run.
                final Jade_RunContext jade_MAS_RunContext = jade_MAS_AdapterAPI
                        .initializeSimulationRun(new NativeJADEMockContext(),
                                jade_MAS_RunGroupContext, this, st, resetRunGroup);
                setJade_MAS_RunContext(jade_MAS_RunContext);
            } catch (final Exception e) {
                e.printStackTrace();
            }
            resetRunGroup = false;

            final FRAMEWORK_COMMAND fc = getJade_MAS_RunContext()
                    .waitForAndProcessSimulationEngineMessageAfterHandshake();
            if (fc == FRAMEWORK_COMMAND.STOP_SIMULATION) {
                System.out
                .println("[JADE Controller Agent] Received command to end simulation run. Listening for new simulation run");
            }
        }
    }

    @Override
    public void receiveMessage(final FrameworkMessage message, final String messageID,
            final String inReplyToMessageID) {
        final List<String> location = message.getSelfLocationFromFirstAgentModel(message
                .getNextMsgForDistributedSoftwareAgentElement(message.getDocument(), null),
                message);
        // TODO: Remove or add other validation.
        assert (location.size() == 2);

        System.out
        .println("[JADE Controller Agent] Received the distributed autonomous agent (model) decision. Forwarding to the simulation engine (agent)");

        jade_MAS_RunContext.sendMessageToSimulationEngine(message,
                jade_MAS_RunContext.getDistSysRunContext());

        final FRAMEWORK_COMMAND fc = getJade_MAS_RunContext()
                .waitForAndProcessSimulationEngineMessageAfterHandshake();
        if (fc == FRAMEWORK_COMMAND.STOP_SIMULATION) {
            System.out
            .println("[JADE Controller Agent] Simulation Run Ended. Listening for new simulation run");
            final Set<NativeDistributedAutonomousAgent> st = getInitialSetOfNativeJADEagents();
            listenLoop(st);
        }
    }

    public void setJade_MAS_RunContext(final Jade_RunContext jade_MAS_RunContext) {
        this.jade_MAS_RunContext = jade_MAS_RunContext;
    }

}
