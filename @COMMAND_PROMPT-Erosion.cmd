@echo off
title Erosion Mod Builder
:start
cls
echo Generating project version...
g++ vermgr.cpp -o vermgr
call vermgr
echo Writing changes to the code...
git add .
git commit -m update
git push
echo Generating data...
call gradlew clean build
call gradlew clean processResources
call gradlew runData -stacktrace --no-configuration-cache
echo Building...
call gradlew build -stacktrace --no-configuration-cache
echo PROCESS FINISHED.
pause
goto start