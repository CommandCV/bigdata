#!/usr/bin/env bash

docker network create \
  --subnet=192.168.0.0/16 \
  --gateway=192.168.0.1 \
  --driver bridge \
  demo

docker run \
  -p 2181:2181 \
  --name zookeeper \
  --hostname zookeeper \
  --network demo \
  -d zookeeper:3.6.3