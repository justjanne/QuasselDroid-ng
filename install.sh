#!/bin/sh
cd AndroidSlidingUpPanel
gradle build
cd ..
cp AndroidSlidingUpPanel/library/build/outputs/aar/library-release.aar app/libs/

cd aspm
gradle build jar shadowJar
cd ..
cp aspm/annotations/build/libs/annotations.jar app/libs/
cp aspm/compiler/build/libs/compiler-all.jar app/libs/

gradle assembleRelease -x lintVitalRelease

rm app/libs/*