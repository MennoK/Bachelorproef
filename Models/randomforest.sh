#!/bin/sh

export CLASSPATH=../weka.jar:$CLASSPATH

java weka.classifiers.trees.RandomForest -l randomforest.model -T features.arff
