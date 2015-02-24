#!/bin/sh

export CLASSPATH=./weka.jar:$CLASSPATH

# TRAININGSET="./Experimenten/Settings/Resultaten/TOTAAL/Training-set.csv"
TRAININGSET="./Experimenten/Settings-HMMs/10-200/Training-set.csv"

for num in 130 125 120 115 110 105 #100 95 90 85 80 75 70 65 60 55 50 45 40 35 30 25 20 15 14 13 12 11 10 9 8 7 6 5 4 3 2 1
do

java weka.classifiers.meta.AttributeSelectedClassifier -t ${TRAININGSET} -x 10 -E "weka.attributeSelection.InfoGainAttributeEval" -S "weka.attributeSelection.Ranker -N ${num}" -W weka.classifiers.trees.J48 -- -C 0.25 -M 2 > ./Experimenten/Features/j48_n_${num}.txt

java weka.classifiers.meta.AttributeSelectedClassifier -t ${TRAININGSET} -x 10 -E "weka.attributeSelection.InfoGainAttributeEval" -S "weka.attributeSelection.Ranker -N ${num}" -W weka.classifiers.trees.RandomForest -- -I 10 -K 0 -S 1 > ./Experimenten/Features/random_forest_n_${num}.txt

done

