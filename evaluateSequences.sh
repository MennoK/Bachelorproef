#!/bin/sh

java -jar ActivityRecognition.jar eval ./Sequenties/Models/RandomForest.all-features.model ./Sequenties/sequentie1_a_l_label.csv ./Sequenties/sequentie1_a_l_20150210131904.log 4.0 0.25 ./Settings/settingssettings_hmm_10_120.json 0.1
