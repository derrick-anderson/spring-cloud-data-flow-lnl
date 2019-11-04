#!/bin/bash

docker build . -t deanderson/kafka-writer-json:latest --no-cache
docker push deanderson/kafka-writer-json:latest