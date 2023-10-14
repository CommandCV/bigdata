#!/bin/bash

nohup yarn-session.sh > /usr/local/flink/log/yarn-session.log 2>&1 &

sleep 2s

tail -f /usr/local/flink/log/yarn-session.log