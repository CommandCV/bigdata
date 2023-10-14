FROM mysql:5.7.38
LABEL name=mysql version=5.7.38 maintainer=sunsongyang

ENV LANG C.UTF-8
WORKDIR /usr/local
USER root

COPY config/init.sql /usr/local/src/
COPY bin/start-process.sh /usr/local/bin/

#ENTRYPOINT ["/bin/bash", "/usr/local/bin/start-process.sh"]


