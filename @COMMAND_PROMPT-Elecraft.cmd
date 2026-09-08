@echo off
title ELECRAFT Mod Builder
:start
git add .
git commit -m update
git push
cls
echo Generating data...
call gradlew clean processResources
call gradlew runData -stacktrace --no-configuration-cache
echo Building...
call gradlew build -stacktrace --no-configuration-cache
echo PROCESS FINISHED.
pause
goto start