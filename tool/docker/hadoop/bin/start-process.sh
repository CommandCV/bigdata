#!/bin/bash
# enable ssh service
service ssh start

# format hdfs and start hadoop
if [[ ! -f "/usr/local/hadoop/format.lock" ]]
then
 hdfs namenode -format
 echo "1" > /usr/local/hadoop/format.lock
fi
start-all.sh

# tail a file, avoid container out
tail -f ~/.bashrc