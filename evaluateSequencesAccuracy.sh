#!/bin/bash

declare -a models=("all-features.randomforest" "all-features.ibk" "all-features.j48" "all-features.naivebayes" "all-features.libsvm")

# bestandsnaam van log-bestand en csv-bestand voor labels moet zelfde zijn, op extensie na!
declare -a sequenties=("sequentie4_a_l_20150420104158" "sequentie4_m_r_20150420103941" "sequentie5_a_l_20150421171955" "sequentie5_m_r_20150421171628")

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

                    java -jar ActivityRecognition.jar accuracy ./Sequenties/Resultaten/$sequenties-$model-$size-$overlap-$cutoff.predictioncsv.csv ./Sequenties/$sequentie.csv

                done
            done
        done
    done

done
