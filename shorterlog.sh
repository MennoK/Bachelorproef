#!/bin/sh
#Script voor csv

for dir in ./Data/*/*/*.log
do

java -jar ./ActivityRecognition.jar shorterlog $dir

done
