#!/bin/bash

# init hive metadata
if [[ `hdfs dfs -ls /tmp/hive | wc -l` -eq 0 ]]
then
 schematool -dbType mysql -initSchema
fi

# start hive metastore and hiveserver2
nohup /usr/local/hive/bin/hive --service metastore > /usr/local/hive/logs/hivemetastore.log 2>&1 &
nohup /usr/local/hive/bin/hive --service hiveserver2 > /usr/local/hive/logs/hiveserver2.log 2>&1 &

# tail a file, avoid container out
tail -f ~/.bashrc