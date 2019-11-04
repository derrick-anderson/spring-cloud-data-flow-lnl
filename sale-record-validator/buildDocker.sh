#!/bin/bash

docker build . -t deanderson/sale-record-validator:latest --no-cache
docker push deanderson/sale-record-validator:latest