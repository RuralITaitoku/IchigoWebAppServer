#!/bin/bash
set -x
export EXEC_CLASS=com.taitoku.App
function springProject () {
	mvn archetype:generate -B\
	 -DarchetypeGroupId=am.ik.archetype\
	 -DarchetypeArtifactId=spring-boot-docker-blank-archetype\
	 -DarchetypeVersion=1.0.2\
	 -DgroupId=com.taitoku\
	 -DartifactId=$1\
	 -Dversion=1.0.0-SNAPSHOT
}

function build() {
	mvn compile
	mvn package
}

function lib() {
	mvn dependency:copy-dependencies -DoutputDirectory=lib
}
function run() {
	set +x
	echo ------------------------
	java -classpath 'lib/*:target/*' $EXEC_CLASS
}
if   [ "$1" = "cp" ]; then
	cp $2
elif   [ "$1" = "springProject" ]; then
  springProject $2
elif   [ "$1" = "webAppProject" ]; then
  webAppProject $2
elif   [ "$1" = "lib" ]; then
	lib
elif [ "$1" = "build" ]; then
	build
elif [ "$1" = "archive" ]; then
	archive
elif [ "$1" = "run" ]; then
	run
elif [ "$1" = "automata" ]; then
	automata
elif [ "$1" = "udptest" ]; then
	udptest
elif [ "$1" = "upload" ]; then
	upload
else
	build	
fi
