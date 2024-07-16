#!/bin/bash

JAR=HoneyBadger-0.0.1-SNAPSHOT.jar
LOG=/home/ubuntu/honeybadger/data/api/config/back.log

echo "BACK started."

nohup java -jar $JAR > $LOG 2>&1 &