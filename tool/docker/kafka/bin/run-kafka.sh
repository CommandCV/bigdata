#!/usr/bin/env bash

docker network create \
  --subnet=192.168.0.0/16 \
  --gateway=192.168.0.1 \
  --driver bridge \
  demo

docker run \
  -p 9092:9092 \
  -e ALLOW_PLAINTEXT_LISTENER=yes \
  -e KAFKA_CFG_ZOOKEEPER_CONNECT=zookeeper:2181 \
  --name kafka \
  --hostname kafka \
  --network demo \
  -d bitnami/kafka:2.8.1