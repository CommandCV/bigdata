#!/usr/bin/env bash

docker network create \
  --subnet=192.168.0.0/16 \
  --gateway=192.168.0.1 \
  --driver bridge \
  demo

docker run \
  -p 8087:8087 \
  --network demo \
  --name trino \
  --hostname trino \
  -d 707509803/trino:3.7.3