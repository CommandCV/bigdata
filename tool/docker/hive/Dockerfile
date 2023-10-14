FROM 707509803/ubuntu:22.04
LABEL name=hive version=3.1.2 maintainer=sunsongyang

# 1.set workdir and username
WORKDIR /usr/local
USER root

# 2.copy jar file and link them
RUN apt-get -y install openjdk-8-jdk
ADD lib/hadoop-3.1.4.tar.gz ./
ADD lib/apache-hive-3.1.2-bin.tar.gz ./
RUN ln -s /usr/local/hadoop-3.1.4 /usr/local/hadoop && \
    ln -s /usr/local/apache-hive-3.1.2-bin /usr/local/hive && \
    ln -s /usr/lib/jvm/java-8-openjdk-amd64 /usr/local/java && \
    chmod -R 755 /usr/local/hadoop && \
    chmod -R 755 /usr/local/hive

# 3.set env and path
ENV JAVA_HOME /usr/local/java
ENV CLASSPATH .:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
ENV HADOOP_HOME /usr/local/hadoop
ENV HADOOP_CLASSPATH $HADOOP_HOME/etc/hadoop
ENV HIVE_HOME /usr/local/hive
ENV PATH $PATH:$JAVA_HOME/bin:$HADOOP_HOME/bin:$HADOOP_HOME/sbin:$HADOOP_CLASSPATH:$HIVE_HOME/bin

# 4.copy config files and lib jars
RUN mkdir -p /usr/local/hadoop/tmp && \
    mkdir -p /usr/local/hadoop/hdfs/data && \
    mkdir -p /usr/local/hadoop/hdfs/name && \
    mkdir -p /usr/local/hive/logs && \
    rm -rf /usr/local/hive/lib/guava-19.0.jar && \
    cp /usr/local/hadoop/share/hadoop/common/lib/guava-27.0-jre.jar /usr/local/hive/lib/

COPY config/hadoop/* /usr/local/hadoop/etc/hadoop/
COPY config/hive/* /usr/local/hive/conf/
COPY lib/common/mysql-connector-java-5.1.47.jar /usr/local/hive/lib/
COPY lib/aux/* /usr/local/hive/aux_jars/

# 5.start process
COPY bin/start-process.sh /usr/local/bin/
RUN chmod 755 /usr/local/bin/start-process.sh

CMD ["/bin/bash", "start-process.sh"]
