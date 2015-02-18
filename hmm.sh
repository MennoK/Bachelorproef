#!/bin/sh

for it in 100 120 140 160 180 200 220 240 260 280 300 340 380 450
do

# hmm's berekenen:
java -jar ActivityRecognition.jar makehmm Fietsen 10 $it
java -jar ActivityRecognition.jar makehmm LiftAD 10 $it
java -jar ActivityRecognition.jar makehmm LiftAU 10 $it
java -jar ActivityRecognition.jar makehmm Lopen 10 $it
java -jar ActivityRecognition.jar makehmm Nietsdoen 10 $it
java -jar ActivityRecognition.jar makehmm Springen 10 $it
java -jar ActivityRecognition.jar makehmm Tandenpoetsen 10 $it
java -jar ActivityRecognition.jar makehmm Trapaf 10 $it
java -jar ActivityRecognition.jar makehmm Trapop 10 $it
java -jar ActivityRecognition.jar makehmm Wandelen 10 $it

# settings bestand maken:
java -jar ActivityRecognition.jar makehmmsettings HMMs/10-$it 10 $it

done

# evaluatie
java -jar ActivityRecognition.jar expsettingshmms
