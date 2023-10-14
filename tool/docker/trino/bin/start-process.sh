#!/bin/bash
# enable ssh service
service ssh start

# start launcher
launcher start

# tail a file, avoid container out
tail -f ~/.bashrc