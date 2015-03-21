#!/bin/sh

export CLASSPATH=./weka.jar:$CLASSPATH

java weka.core.converters.CSVLoader data-allfeatures.csv > data-allfeatures.arff

java weka.core.converters.CSVLoader features.csv > features.arff

java weka.filters.unsupervised.attribute.StringToWordVector -b -i input_training_set.arff -o output_training_set.arff -c last -r input_test_set.arff -s output_test_set.arff -R 1,2,3 -O -C -T -I -N 0 -M 1

java weka.filters.unsupervised.attribute.Standardize -b -i data-allfeatures.arff -o data-allfeatures-standardized.arff -r features.arff -s features-standardized.arff
