FROM ubuntu:22.04
LABEL name=ubuntu version=22.04 maintainer=sunsongyang

WORKDIR /usr/local
USER root
ENV LANG C.UTF-8

RUN apt-get update && \
    apt-get -y install net-tools iputils-ping telnet telnetd curl vim openssh-server openssh-client && \
    ssh-keygen -t rsa -f /root/.ssh/id_rsa && cat ~/.ssh/id_rsa.pub > ~/.ssh/authorized_keys

COPY bin/start-process.sh /usr/local/bin/
RUN chmod 755 /usr/local/bin/start-process.sh

CMD ["/bin/bash", "start-process.sh"]