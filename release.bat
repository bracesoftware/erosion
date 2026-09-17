set repo="bracesoftware/erosion"
set buildver=6
gh release delete build%buildver% -y -R %repo% --cleanup-tag
gh release create build%buildver% "./build/libs/Erosion-neoforge.jar" -t "build %buildver%" -R %repo% --generate-notes