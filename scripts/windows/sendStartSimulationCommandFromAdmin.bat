cd %~dp0

@echo off
echo .
echo *****************************************************
echo *   COMMON AGENT-BASED SIMULATION FRAMEWORK (CABSF) *
echo *           Simulation Start Script                 *
echo *****************************************************
echo.
echo This script kicks off the CABSF simulation runs.  Before continuing, ensure that
echo the following are running: 1) the simulation (e.g. Repast Simphony using either
echo the RSSR or the Repast Simphony GUI), 2) the distributed system
echo (e.g. the JADE MAS), and 3) the common interface (e.g. Redis).
echo You will know that both sides are ready when you see "+" being printed by the
echo simulation engine, and "." being printed by the distributed system.
echo.
echo Once the simulation runs finish executing, the simulation process will terminate, if using the RSS.
echo Otherwise, the Repast Simphony GUI will continue to run.  The distributed system will continue to execute
echo and will be ready for additional simulation runs.
echo.
pause
echo.
echo *****************************************************
echo Sending the CABSF Simulation Engine a message to start the simulation.  The simulation engine and the agents in the distributed system will now begin communicating with each other.
echo This script process can be closed/terminated at any time.
echo ****************************************************
echo.
@echo on
redis-cli flushall
redis-cli -x LPUSH cabsf.commands.adminToSim:19def3fa-a1d4-4996-a1ac-22c3a041e6ff <../../../../CommonAgentBasedSimulationFramework/bin/org/opensimulationsystems/cabsf/common/resources/messageexchange/startSimulation.xml
@echo.
@echo off
pause