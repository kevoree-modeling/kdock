#!/usr/bin/env bash

HOST=192.168.25.38:4243

echo "=== BUILD kdock-server image ==="

cd kdock-server
docker -H tcp://${HOST}  build -t kdock-server .
cd ..