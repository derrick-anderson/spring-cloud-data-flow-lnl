#!/bin/bash

docker build . -t deanderson/sale-record-router:latest --no-cache
docker push deanderson/sale-record-router:latest