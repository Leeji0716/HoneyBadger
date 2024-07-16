#!/bin/bash

LOG=/home/ubuntu/honeybadger/data/api/config/front.log

echo "FRONT started."

nohup npm run start > $LOG 2>&1 &