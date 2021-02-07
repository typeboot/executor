#!/usr/bin/env bash

args="$@"
if [[ -z $TYPEBOOT_EXECUTOR_FILE ]];
then
    args="$@"
else
  args="$TYPEBOOT_EXECUTOR_FILE"
fi;

CLASSPATH="."

for x in $(ls /opt/app/lib);
do
  CLASSPATH="/opt/app/lib/$x:$CLASSPATH";
done;

for x in $(ls /opt/app/plugins);
do
  CLASSPATH="/opt/app/plugins/$x:$CLASSPATH";
done;

CMD="java -cp $CLASSPATH com.typeboot.ExecKt ${args}"
exec ${CMD}
