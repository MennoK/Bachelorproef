#!/bin/sh

java -jar ActivityRecognition.jar eval ./Sequenties/Models/all-features.vote-kstar-randomforest.model ./Sequenties/sequentie1_m_r_labels.csv ./Sequenties/sequentie1_m_r_20150210131306.log 4.0 0.75 ./Settings/settings.json 0.3
