#!/bin/sh
#Script voor csv

for dir in ./Data2/*/*/*.log
do

java -jar ./ActivityRecognition.jar shorterlog $dir

done
