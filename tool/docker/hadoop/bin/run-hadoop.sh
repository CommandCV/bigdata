#!/usr/bin/env bash

docker network create \
  --subnet=192.168.0.0/16 \
  --gateway=192.168.0.1 \
  --driver bridge \
  demo

docker run \
  -p 9820:9820 -p 9000:9000 -p 8020:8020\
  -p 9870:9870 -p 9864:9864 \
  -p 50070:50070 \
  -p 8088:8088 -p 19888:19888 \
  -p 8030:8030 -p 8031:8031 -p 8032:8032 \
  --network demo \
  --name hadoop \
  --hostname hadoop \
  -d 707509803/hadoop:3.1.4