#!/bin/sh

for dir in ./Data/LiftAD/*.cut.log
do
name=$(basename "$dir")	
java -jar MotionFingerprint.jar --Hmm Data/LiftAD/*.cut.log
done

for dir in ./Data/LiftAU/*.cut.log
do
name=$(basename "$dir")	
java -jar MotionFingerprint.jar --Hmm Data/LiftAU/*.cut.log
done
