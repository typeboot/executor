#!/usr/bin/env bash

./gradlew clean build
mkdir -p build/package/libs

cp ./jdbc/build/libs/jdbc*uber.jar build/package/libs/jdbc-uber.jar
cp ./core/build/libs/core*uber.jar build/package/libs/core-uber.jar
cp ./cassandra/build/libs/cassandra*uber.jar build/package/libs/cassandra-uber.jar

CLASSPATH="."

for x in $(ls build/package/libs);
do
  CLASSPATH="build/package/libs/$x:$CLASSPATH";
done;
SPEC_FILE="$1"
java -cp $CLASSPATH com.typeboot.ExecKt ${SPEC_FILE}
