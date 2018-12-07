#!/bin/sh
#compassmate server services defined as following:
#./compassmate start
#./compassmate stop
#./compassmate restart

SERVICE_NAME=compassmate
PATH_TO_JAR=target/compassmate-server.jar
PATH_TO_CONF=debug.xml
PID_PATH_NAME=/tmp/compassmate-pid
case $1 in
	start)
		echo "Starting $SERVICE_NAME ..."
		if [ ! -f $PID_PATH_NAME ]; then
			nohup java -jar $PATH_TO_JAR $PATH_TO_CONF /tmp 2>> /dev/null >> /dev/null &
				echo $! > $PID_PATH_NAME
			echo "$SERVICE_NAME started successfully ..."
		else
			echo "$SERVICE_NAME is already running"
		fi
	;;
	stop)
        if [ -f $PID_PATH_NAME ]; then
			PID=$(cat $PID_PATH_NAME);
			echo "$SERVICE_NAME stoping ..."
			kill $PID;
			echo "$SERVICE_NAME stopped :)..."
			rm $PID_PATH_NAME
		else
			echo "dude, $SERVICE_NAME is not running though..."
		fi
	;;
	restart)
        if [ -f $PID_PATH_NAME ]; then
			PID=$(cat $PID_PATH_NAME);
			echo "$SERVICE_NAME stopping ...";
			kill $PID;
			echo "$SERVICE_NAME stopped ...";
			rm $PID_PATH_NAME
			echo "$SERVICE_NAME starting ..."
			nohup java -jar $PATH_TO_JAR $PATH_TO_CONF /tmp 2>> /dev/null >> /dev/null &
			echo $! > $PID_PATH_NAME
			echo "$SERVICE_NAME started ..."
		else
			echo "dude, $SERVICE_NAME is not running ..."
		fi
	;;
esac
