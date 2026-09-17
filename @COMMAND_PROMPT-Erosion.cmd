@echo off
title Erosion Mod Builder
:start
cls
echo Generating project version...
g++ vermgr.cpp -o vermgr -v -static
call vermgr
echo Writing changes to the code...
call pushtogithub
echo Generating data...
call gradlew clean build
call gradlew clean processResources
call gradlew runData -stacktrace --no-configuration-cache
echo Building...
call gradlew build -stacktrace --no-configuration-cache
echo PROCESS FINISHED.
call release
echo RELEASED!!!
pause
goto start