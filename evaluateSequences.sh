#!/bin/sh

java -jar ActivityRecognition.jar eval ./Sequenties/Models/hmms.vote-decisionstump-ibk-naivebayes.model ./Sequenties/sequentie1_a_l_label.csv ./Sequenties/sequentie1_a_l_20150210131904.log 5.0 0.5 ./Settings/settings.json 0.4 hmm
