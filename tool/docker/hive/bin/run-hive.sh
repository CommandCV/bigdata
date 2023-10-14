#!/usr/bin/env bash

docker network create \
  --subnet=192.168.0.0/16 \
  --gateway=192.168.0.1 \
  --driver bridge \
  demo

docker run \
  -p 10000:10000 \
  -p 10002:10002 \
  -p 9083:9083 \
  --name hive \
  --hostname hive \
  --network demo \
  -d 707509803/hive:3.1.2