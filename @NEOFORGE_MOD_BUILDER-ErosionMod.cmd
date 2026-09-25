:config
set "SCRIPT_TITLE=Erosion Mod Builder"
set bin="Erosion-neoforge.jar"

@echo off

title %SCRIPT_TITLE%
:start
cls

echo Generating project version...
title %SCRIPT_TITLE%: Generating project version...

g++ vermgr.cpp -o vermgr -v -static
call vermgr

echo Writing changes to the code...
title %SCRIPT_TITLE%: Writing changes to the code...

call _PUSHCHANGES

echo Generating data...
title %SCRIPT_TITLE%: Generating data...

call gradlew clean build
call gradlew clean processResources
call gradlew runData -stacktrace --no-configuration-cache

title %SCRIPT_TITLE%: Building...
echo Building...

call gradlew build -stacktrace --no-configuration-cache

echo Releasing...
title %SCRIPT_TITLE%: Releasing...
copy build\libs\%bin% .\%bin%
call _PUSHCHANGES
echo - Everything is ready for release, if you want to; run -> _RELEASE
echo ====== COMPLETE PROCESS FINISHED! ======

title %SCRIPT_TITLE%: Process finished.

pause
goto start