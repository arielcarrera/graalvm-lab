#!/bin/bash

$JAVA_HOME/bin/native-image --no-server \
         --class-path /project/target/graalvmlab.jar \
	     -H:Name=undockerizer-ubuntu \
	     -H:Class=com.github.arielcarrera.graalvmlab.GraalVmLab \
	     -H:+ReportUnsupportedElementsAtRuntime \
	     -H:+AllowVMInspection \
		 -H:ReflectionConfigurationFiles=/project/docker-graalvm/reflect.json
