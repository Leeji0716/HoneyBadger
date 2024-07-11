#!/bin/bash

cd /frontend
./run.sh

cd /backend
./run.sh

echo "loop start"
while :
do
    sleep 1
done