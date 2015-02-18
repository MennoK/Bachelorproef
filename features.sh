#!/bin/sh

export CLASSPATH=./weka.jar:$CLASSPATH

TRAININGSET="./Experimenten/Settings/Resultaten/TOTAAL/Training-set.csv"

for gain in 0 0.1 0.2 0.3 0.4 0.5 0.6 0.7 0.8 0.9 1 1.1 1.2 1.3 1.4 1.5 1.6 1.7 1.8 1.9 2 2.1 2.2 2.3
do

#java weka.classifiers.meta.AttributeSelectedClassifier -t ${TRAININGSET} -x 10 -E "weka.attributeSelection.InfoGainAttributeEval" -S "weka.attributeSelection.Ranker -T ${gain} -N -1" -W weka.classifiers.trees.J48 -- -C 0.25 -M 2 > ./Experimenten/Features/j48_${gain}.txt

java weka.classifiers.meta.AttributeSelectedClassifier -t ${TRAININGSET} -x 10 -E "weka.attributeSelection.InfoGainAttributeEval" -S "weka.attributeSelection.Ranker -T ${gain} -N -1" -W weka.classifiers.trees.RandomForest -- -I 10 -K 0 -S 1 > ./Experimenten/Features/random_forest_${gain}.txt
done

