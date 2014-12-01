#!/bin/sh
#Script


for dir in ./Pre-processing/Data/Nietsdoen/*.log
do
 name=$(basename "$dir")	

# java -jar ./ActivityRecognition.jar --editlog "$dir"
java -jar ./MotionFingerprint.jar --plot "${dir%/*}/${name}.pdf" "$dir"

done
