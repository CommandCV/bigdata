FROM 707509803/ubuntu:22.04
LABEL name=trino version=3.7.3 maintainer=sunsongyang

# 1.set work dir, prepare tools
WORKDIR /usr/local/
USER root
RUN apt-get -y install python3 openjdk-11-jdk

# 2.copy jar file and link dir
ADD lib/trino-server-373.tar.gz ./
RUN ln -s /usr/local/trino-server-373 /usr/local/trino && \
    ln -s /usr/lib/jvm/java-11-openjdk-amd64 /usr/local/java && \
    ln -s /usr/bin/python3 /usr/bin/python

# 3.set java and trino env
ENV JAVA_HOME /usr/local/java
ENV CLASSPATH .:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
ENV TRINO_HOME /usr/local/trino
ENV PATH $PATH:$JAVA_HOME/bin:$TRINO_HOME/bin

# 4.copy trino config files
RUN mkdir -p /usr/local/trino/etc/catalog /data
COPY lib/hudi-hadoop-mr-bundle-0.12.0.jar /usr/local/trino/plugin/hive/
COPY lib/trino-cli-373-executable.jar /usr/local/trino/bin/
COPY config/config.properties /usr/local/trino/etc/
COPY config/jvm.config /usr/local/trino/etc/
COPY config/log.properties /usr/local/trino/etc/
COPY config/node.properties /usr/local/trino/etc/
COPY config/jmx.properties /usr/local/trino/etc/catalog/
COPY config/hive.properties /usr/local/trino/etc/catalog/
RUN chown -R 755 /usr/local/trino

# 5.start trino process
COPY bin/enter-trino.sh /usr/local/bin/
COPY bin/start-process.sh /usr/local/bin/
RUN chmod 755 /usr/local/bin/*

CMD ["/bin/bash", "start-process.sh"]