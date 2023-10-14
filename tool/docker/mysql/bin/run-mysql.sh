#!/usr/bin/env bash

docker network create \
  --subnet=192.168.0.0/16 \
  --gateway=192.168.0.1 \
  --driver bridge \
  demo

docker run \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=root \
  --name mysql \
  --hostname mysql \
  --network demo \
  -d 707509803/mysql:5.7.38