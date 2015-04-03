#!/bin/bash

declare -a models=("all-features.vote-kstar-randomforest") # zonder extensies

# bestandsnaam van log-bestand en csv-bestand voor labels moet zelfde zijn, op extensie na!
declare -a sequenties=("sequentie1_m_r_20150210131306") # zonder extensies

for sequentie in "${sequenties[@]}"
do

    for size in 4.0 6.0 8.0
    do
        for overlap in 0.0 0.25 0.5 0.75
        do
            for cutoff in 0.5 0.4 0.3 0.2 0.1 0.0
            do
                for model in "${models[@]}"
                do

                    java -jar ActivityRecognition.jar eval ./Sequenties/Models/$model.model ./Sequenties/$sequentie.csv ./Sequenties/$sequentie.log $size $overlap ./Settings/settings.json $cutoff > ./Sequenties/Resultaten/$sequenties-$model-$size-$overlap-$cutoff.output.txt

                    cp ./predictioncsv.csv ./Sequenties/Resultaten/$sequenties-$model-$size-$overlap-$cutoff.predictioncsv.csv

                done
            done
        done
    done

done
