# GraalVm Lab
Samples with GraalVm.


# Build centos native image

from project root: 
	
	mvn clean install

from docker-graalvm/centos:

	docker-build.bat 1.0.0-SNAPSHOT 

	docker-run.bat 1.0.0-SNAPSHOT C:\Desarrollo\Github\arielcarrera\graalvm-lab