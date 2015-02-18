#!/bin/sh

export CLASSPATH=./weka.jar:$CLASSPATH

TRAININGSET="./Experimenten/Settings/Resultaten/Settings_fft_12/Training-set.csv"

java weka.classifiers.meta.CVParameterSelection -x 10 -t $TRAININGSET -P "C 0.1 0.5 5" -W weka.classifiers.trees.J48

java weka.classifiers.meta.CVParameterSelection -x 10 -t $TRAININGSET -P "I 5 20 16" -W weka.classifiers.trees.RandomForest
