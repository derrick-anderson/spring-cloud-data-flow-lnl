#!/bin/bash

docker build . -t deanderson/kafka-writer-xml:latest --no-cache
docker push deanderson/kafka-writer-xml:latest