#!/bin/sh
#Script voor csv

for dir in ./Pre-processing/Data/*/*.log
do
 name=$(basename "$dir")	
java -jar ./MotionFingerprint.jar --features "./Pre-processing/CSV/${name}.csv" "$dir"

done