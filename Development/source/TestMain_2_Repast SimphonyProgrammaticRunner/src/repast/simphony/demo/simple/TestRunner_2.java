package repast.simphony.demo.simple;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import repast.simphony.batch.BatchScenarioLoader;
import repast.simphony.engine.controller.Controller;
import repast.simphony.engine.controller.DefaultController;
import repast.simphony.engine.environment.AbstractRunner;
import repast.simphony.engine.environment.ControllerRegistry;
import repast.simphony.engine.environment.DefaultRunEnvironmentBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.environment.RunEnvironmentBuilder;
import repast.simphony.engine.environment.RunState;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.Schedule;
import repast.simphony.parameter.Parameters;
import repast.simphony.parameter.ParametersParser;
import repast.simphony.parameter.SweeperProducer;
import simphony.util.messages.MessageCenter;

public class TestRunner_2 extends AbstractRunner {

    private static MessageCenter msgCenter = MessageCenter
            .getMessageCenter(TestRunner_2.class);

    private RunEnvironmentBuilder runEnvironmentBuilder;
    protected Controller controller;
    protected boolean pause = false;
    protected Object monitor = new Object();
    protected SweeperProducer producer;
    private ISchedule schedule;

    private Parameters params;

    public TestRunner_2() {
        runEnvironmentBuilder = new DefaultRunEnvironmentBuilder(this, true);
        controller = new DefaultController(runEnvironmentBuilder);
        controller.setScheduleRunner(this);
    }

    public void cleanUpBatch() {
        controller.batchCleanup();
    }

    public void cleanUpRun() {
        controller.runCleanup();
    }

    @Override
    public void execute(RunState toExecuteOn) {
        // required AbstractRunner stub. We will control the
        // schedule directly.
    }

    // returns the number of non-model actions on the schedule
    public int getActionCount() {
        return schedule.getActionCount();
    }

    // returns the number of model actions on the schedule
    public int getModelActionCount() {
        return schedule.getModelActionCount();
    }

    // returns the tick count of the next scheduled item
    public double getNextScheduledTime() {
        return ((Schedule) RunEnvironment.getInstance().getCurrentSchedule())
                .peekNextAction().getNextTime();
    }

    public void load(File scenarioDir) throws Exception {
        if (scenarioDir.exists()) {
            BatchScenarioLoader loader = new BatchScenarioLoader(scenarioDir);
            ControllerRegistry registry = loader.load(runEnvironmentBuilder);
            controller.setControllerRegistry(registry);

        } else {
            msgCenter.error("Scenario not found", new IllegalArgumentException(
                    "Invalid scenario " + scenarioDir.getAbsolutePath()));
            return;
        }

        controller.batchInitialize();

        ParametersParser parser = null;
        try {
            parser = new ParametersParser(new File(scenarioDir.getAbsolutePath()
                    + "/parameters.xml"));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        params = parser.getParameters();
    }

    public void runInitialize() {
        controller.runParameterSetters(params);
        controller.runInitialize(params);
        schedule = RunState.getInstance().getScheduleRegistry().getModelSchedule();
    }

    public void setFinishing(boolean fin) {
        schedule.setFinishing(fin);
    }

    // Step the schedule
    @Override
    public void step() {
        schedule.execute();
    }

    // stop the schedule
    @Override
    public void stop() {
        if (schedule != null) {
            schedule.executeEndActions();
        }
    }
}
