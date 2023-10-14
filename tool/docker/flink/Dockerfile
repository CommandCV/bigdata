FROM 707509803/ubuntu:22.04
LABEL name=flink version=1.13.6 maintainer=sunsongyang

# 1.set work dir, prepare tools
WORKDIR /usr/local/
USER root
RUN apt-get -y install openjdk-8-jdk

# 2.copy jar file and link them
ADD lib/hadoop-3.1.4.tar.gz ./
ADD lib/flink-1.13.6-bin-scala_2.12.tgz ./
RUN ln -s /usr/local/hadoop-3.1.4 /usr/local/hadoop && \
    ln -s /usr/local/flink-1.13.6 /usr/local/flink && \
    ln -s /usr/lib/jvm/java-8-openjdk-amd64 /usr/local/java && \
    mkdir -p /data/flink/rocksdb && \
    chmod -R 755 /usr/local/hadoop && \
    chmod -R 755 /usr/local/flink

# 3.set env and path
ENV JAVA_HOME /usr/local/java
ENV CLASSPATH .:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
ENV HADOOP_HOME /usr/local/hadoop
ENV HADOOP_CONF_DIR /usr/local/hadoop/etc/hadoop
ENV HADOOP_CLASSPATH `hadoop classpath`
ENV FLINK_HOME /usr/local/flink
ENV PATH $PATH:$JAVA_HOME/bin:$HADOOP_HOME/bin:$HADOOP_HOME/sbin:$HADOOP_CLASSPATH:$FLINK_HOME/bin

# 4.copy config files and lib jars
COPY config/flink/flink-conf.yaml /usr/local/flink/conf/
COPY config/hive/hive-site.xml /usr/local/flink/conf/
COPY config/hadoop/* /usr/local/hadoop/etc/hadoop/
COPY lib/hudi-flink1.13-bundle-0.12.0.jar /usr/local/flink/lib/
COPY lib/flink-shaded-hadoop-3-3.1.1.7.2.9.0-173-9.0.jar /usr/local/flink/lib/

# 5.start process
COPY bin/enter-sql-client.sh /usr/local/bin
COPY bin/start-yarn-session.sh /usr/local/bin
COPY bin/start-process.sh /usr/local/bin/
RUN chmod 755 /usr/local/bin/*

CMD ["/bin/bash", "start-process.sh"]