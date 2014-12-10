#!/bin/sh
#Script


for dir in ./Data2/Trapaf/*.log
do
 name=$(basename "$dir")	

# java -jar ./ActivityRecognition.jar --editlog "$dir"
java -jar ./MotionFingerprint.jar --plot "${dir%/*}/${name}.pdf" "$dir"

done