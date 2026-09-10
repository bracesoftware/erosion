@echo off
title Erosion Mod Builder
:start
cls
echo Writing changes to the code...
git add .
git commit -m update
git push
echo Generating data...
call gradlew clean processResources
call gradlew runData -stacktrace --no-configuration-cache
echo Building...
call gradlew build -stacktrace --no-configuration-cache
echo PROCESS FINISHED.
pause
goto start