#!/bin/sh

# Update submodules
git submodule update --init  

# Create directories
mkdir -p app/libs/

# Build AndroidSlidingUpPanel
echo sdk.dir=$ANDROID_HOME > AndroidSlidingUpPanel/local.properties
cd AndroidSlidingUpPanel
gradle clean build
cd ..
cp AndroidSlidingUpPanel/library/build/outputs/aar/library-release.aar app/libs/library-release.aar
rm AndroidSlidingUpPanel/local.properties

# Build QuasselDroidNG
echo sdk.dir=$ANDROID_HOME > local.properties
gradle clean assembleRelease -x lintVitalRelease
rm local.properties