#!/bin/bash

docker build . -t deanderson/voided-sale-record-logger:latest --no-cache
docker push deanderson/voided-sale-record-logger:latest