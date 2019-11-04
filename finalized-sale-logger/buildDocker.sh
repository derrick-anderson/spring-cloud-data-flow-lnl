#!/bin/bash

docker build . -t deanderson/finalized-sale-record-logger:latest --no-cache
docker push deanderson/finalized-sale-record-logger:latest