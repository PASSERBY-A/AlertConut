#!/bin/sh

cdir=`pwd`
cd ../..
export UOP_HOME=${UOP_HOME:-`pwd`}
echo UOP_HOME=$UOP_HOME
cd $cdir
JRE_HOME=$UOP_HOME/support/jre
JAVA_EXEC=$JRE_HOME/bin/java
if [ ! -d $JRE_HOME ]; then JAVA_EXEC=java;fi

#jars
JARS=$UOP_HOME/lib/activation-1.1.jar

JARS=$JARS:$UOP_HOME/bin/lib/ngecc-collector.jar
JARS=$JARS:$UOP_HOME/lib/core2.jar
JARS=$JARS:$UOP_HOME/lib/ngecc-alert.jar
JARS=$JARS:$UOP_HOME/lib/ngecc-cmdb.jar
JARS=$JARS:$UOP_HOME/lib/ngecc-platform.jar
JARS=$JARS:$UOP_HOME/lib/ngecc-kpi.jar
JARS=$JARS:$UOP_HOME/lib/ngecc-job.jar
JARS=$JARS:$UOP_HOME/lib/ngecc-view.jar
JARS=$JARS:$UOP_HOME/lib/ngecc-user.jar

 




#main class
MAIN_CLASS=com.hp.gdcc.tsportal.collector.Collector
#log4j configure key
LOG=log4j_collector.properties
export COLLECTOR_PID=`ps -ef | grep log4j_collector|grep -v grep|awk '{print $2}'`
kill -9 $COLLECTOR_PID
$JAVA_EXEC -Xms1024M -Xmx2048M -XX:PermSize=256M -XX:MaxPermSize=512M -Dlog4j.configuration=$LOG -classpath "$JARS:$UOP_HOME/conf" -DUOP_HOME=$UOP_HOME -Dfile.encoding=utf8 $MAIN_CLASS %*
