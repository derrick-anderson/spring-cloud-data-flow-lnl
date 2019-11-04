#!/bin/bash

docker build . -t deanderson/kafka-reader-xml:latest --no-cache
docker push deanderson/kafka-reader-xml:latest