@echo off
title Erosion Mod Builder
:start
cls

echo Generating project version...
title Erosion Mod Builder: Generating project version...

g++ vermgr.cpp -o vermgr -v -static
call vermgr

echo Writing changes to the code...
title Writing changes to the code...

call _PUSHCHANGES

echo Generating data...
title Erosion Mod Builder: Generating data...

call gradlew clean build
call gradlew clean processResources
call gradlew runData -stacktrace --no-configuration-cache

title Erosion Mod Builder: Building...
echo Building...

call gradlew build -stacktrace --no-configuration-cache

echo Releasing...
title Erosion Mod Builder: Releasing...

echo - To release click on _RELEASE
echo - To push changes run _PUSHCHANGES
echo ====== COMPLETE PROCESS FINISHED! ======

title Erosion Mod Builder: Process finished.

pause
goto start