#!/usr/bin/env bash
skip=$2

if [[ $skip != "y" ]];
then
  ./gradlew clean build
  mkdir -p build/package/libs

  cp ./cassandra/build/libs/cassandra*uber.jar build/package/libs/cassandra-uber.jar
fi;

CLASSPATH="."

for x in $(ls build/package/libs);
do
  CLASSPATH="build/package/libs/$x:$CLASSPATH";
done;
SPEC_FILE="$1"

java -cp $CLASSPATH com.typeboot.ExecKt ${SPEC_FILE}
