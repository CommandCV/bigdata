#!/usr/bin/env bash

docker network create \
  --subnet=192.168.0.0/16 \
  --gateway=192.168.0.1 \
  --driver bridge \
  demo

docker run \
  --name test \
  --hostname test \
  --network demo \
  707509803/ubuntu:22.04