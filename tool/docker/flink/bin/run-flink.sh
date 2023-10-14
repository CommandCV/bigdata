#!/usr/bin/env bash

docker network create \
  --subnet=192.168.0.0/16 \
  --gateway=192.168.0.1 \
  --driver bridge \
  demo

docker run \
  -p 8081:8081 \
  --name flink \
  --hostname flink \
  --network demo \
  -d 707509803/flink:1.13.6