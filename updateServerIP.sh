#!/bin/bash
if [ "$#" -ne 1 ]; then
echo "Usage: " $0 " <server IP address>"
exit 1
fi
echo $1 > ./src/main/resources/serverIP.txt
