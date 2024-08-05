#!/bin/bash

mkdir -p /home/ubuntu/honeybadger/data/api/

cd /frontend
./run.sh

cd /backend
./run.sh

echo "loop start"
while :
do
    sleep 1
done