cd %~dp0

@echo off
echo *****************************************************
echo    COMMON SIMULATION FRAMEWORK - Simulation Start Script
echo *****************************************************
echo.
echo This script actually kicks off the simulation runs, but before that the different systems used in the common simulation framework must already be executing and waiting for this command to start.  Make sure to first start 1)The Simulation Engine (Repast Simphony Simulation Runner), 2) The Distributed Systems/Agents (The JADE Multi-agent system), and 3) Redis (in memory-cache for messaging).
echo.
pause
echo.
echo *****************************************************
echo Sending the CSF Simulation Engine a message to start the simulation. The simulation engine will message the distributed systems/agents the same, after initialization
echo ****************************************************
echo.
@echo on
redis-cli flushall
redis-cli -x LPUSH csf.commands.adminToSim:19def3fa-a1d4-4996-a1ac-22c3a041e6ff <../../../CommonSimulationFramework_CommonLibrary/bin/org/simulationsystems/csf/common/resources/messageexchange/startSimulation.xml
@echo.
@echo off
pause