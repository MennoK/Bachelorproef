#!/bin/sh

export CLASSPATH=./weka.jar:$CLASSPATH

TRAININGSET="./Experimenten/Settings/Resultaten/TOTAAL/Training-set.csv"

java weka.classifiers.meta.CVParameterSelection -x 10 -t $TRAININGSET -P "I 5 20 16" -W weka.classifiers.trees.RandomForest
