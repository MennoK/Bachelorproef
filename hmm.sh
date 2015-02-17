#!/bin/sh

for it in 120 #140 160 180 200 220 240 260 280 300 340 380 450
do

#java -jar ActivityRecognition.jar makehmm Fietsen 10 $it
java -jar ActivityRecognition.jar makehmm LiftAD 10 $it
# TODO: + andere activiteiten

# TODO: + settings bestand maken

done

java -jar ActivityRecognition.jar expsettings
