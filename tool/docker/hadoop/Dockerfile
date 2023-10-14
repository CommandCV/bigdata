FROM 707509803/ubuntu:22.04
LABEL name=hadoop version=3.1.4 maintainer=sunsongyang

# 1.set work dir, prepare tools
WORKDIR /usr/local/
USER root
RUN apt-get -y install openjdk-8-jdk

# 2.copy jar file and link dir
ADD lib/hadoop-3.1.4.tar.gz ./
RUN ln -s /usr/local/hadoop-3.1.4 /usr/local/hadoop && \
    ln -s /usr/lib/jvm/java-8-openjdk-amd64 /usr/local/java && \
    chmod -R 755 /usr/local/hadoop

# 3.set java and hadoop env
ENV JAVA_HOME /usr/local/java
ENV CLASSPATH .:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
ENV HADOOP_HOME /usr/local/hadoop
ENV HADOOP_CLASSPATH $HADOOP_HOME/etc/hadoop
ENV PATH $PATH:$JAVA_HOME/bin:$HADOOP_HOME/bin:$HADOOP_HOME/sbin:$HADOOP_CLASSPATH

# 4.copy hadoop config files
RUN mkdir -p /usr/local/hadoop/tmp && \
    mkdir -p /usr/local/hadoop/hdfs/data && \
    mkdir -p /usr/local/hadoop/hdfs/name
COPY config/* /usr/local/hadoop/etc/hadoop/

# 5.start hadoop process
COPY bin/start-process.sh /usr/local/bin/
RUN chmod 755 /usr/local/bin/start-process.sh

CMD ["/bin/bash", "start-process.sh"]