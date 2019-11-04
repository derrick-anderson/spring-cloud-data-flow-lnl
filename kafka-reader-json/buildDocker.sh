#!/bin/bash

docker build . -t deanderson/kafka-reader-json:latest --no-cache
docker push deanderson/kafka-reader-json:latest