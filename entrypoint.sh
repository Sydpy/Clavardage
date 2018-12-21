#!/bin/bash

service mongodb start
java -cp '/app/classes:/app/dependency/*' org.etudinsa.clavardage.CLI

