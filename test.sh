#!/bin/sh

java weka.classifiers.meta.AttributeSelectedClassifier -t Training-set.csv -x 10 -E "weka.attributeSelection.InfoGainAttributeEval" -S "weka.attributeSelection.Ranker -T 2 -N -1" -W weka.classifiers.trees.J48 -- -C 0.25 -M 2 > output.txt
