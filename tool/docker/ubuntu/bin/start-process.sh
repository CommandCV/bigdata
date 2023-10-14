#!/bin/bash
# enable ssh service
service ssh start

# tail a file, avoid container out
tail -f ~/.bashrc