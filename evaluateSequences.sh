#!/bin/sh

java -jar ActivityRecognition.jar eval ./Sequenties/Models/hmms.randomforest.model ./Sequenties/sequentie1_a_l_label.csv ./Sequenties/sequentie1_a_l_20150210131904.log 4.0 0.75 ./Settings/settings.json 0.1 hmm
