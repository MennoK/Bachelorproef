#!/bin/sh

export CLASSPATH=./weka.jar:$CLASSPATH

TRAININGSET="./Experimenten/Settings-HMMs/10-200/Training-set.csv"

# K = num Features
# I = num Trees
# java weka.classifiers.meta.GridSearch -x 10 -t $TRAININGSET -y-property K -y-min 1 -y-max 5 -x-property I -x-min 8 -x-max 14 -W weka.classifiers.trees.RandomForest

# java weka.classifiers.meta.CVParameterSelection -x 10 -t $TRAININGSET -P "depth 1 30 30" -W weka.classifiers.trees.RandomForest > Experimenten/random_forest_param_depth_1_30.txt

# java weka.classifiers.meta.CVParameterSelection -x 10 -t $TRAININGSET -P "I 1 30 30" -W weka.classifiers.trees.RandomForest > Experimenten/random_forest_param_numTrees_1_30.txt

java weka.classifiers.meta.CVParameterSelection -x 10 -t $TRAININGSET -P "K 25 100 76" -W weka.classifiers.trees.RandomForest > Experimenten/random_forest_param_numFeatures_25_100.txt
