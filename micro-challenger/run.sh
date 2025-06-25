#!/bin/bash
docker image load -i gc25cdocker.tar
# change "/home/alessandro/SABD/GC25_challenger/data" with the absolute data path
docker run -p 8866:8866 -v /home/alessandro/SABD/SABD_lpbf_monitoring/micro-challenger/data:/data micro-challenger:latest 0.0.0.0:8866 /data