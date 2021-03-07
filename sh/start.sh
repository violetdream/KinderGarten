#!/usr/bin/env bash

#jarName
applicationName=KindergartenPushMessage
projectName=lxw-$applicationName
target=dev
apollo_meta=''
if [ -n "$2" ]; then
    apollo_meta=$2
fi

if [ -n "$1" ]; then
   target=$1
fi
echo "target = "$target

pid=`ps -ef |grep 'java' |grep $projectName |awk '{print $2}'`
if [ -n "$pid" ]; then
    kill -9 $pid
    sleep 3
fi
DATE=`date +%Y-%m-%d`
LogFileName=/lxw/lxw-KindergartenPushMessage/logs

if [ ! -d $LogFileName ]; then
    mkdir -p $LogFileName
fi
JAVA_OPTS="-Xms200m -Xmx200m -Xss256k -Xmn200m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=128m -XX:SurvivorRatio=8"
JAVA_OPTS="$JAVA_OPTS -XX:+UseG1GC -XX:+PrintGCDetails -Xloggc:$LogFileName/gc.log"
JAVA_OPTS="$JAVA_OPTS -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$LogFileName/dump"
nohup java -jar $JAVA_OPTS ../PushMessageService*.jar >> $LogFileName/start.$applicationName.log 2>&1 &

echo "Deploy Success"
exit 0
