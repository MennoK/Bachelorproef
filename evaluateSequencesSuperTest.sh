#!/bin/bash

declare -a models=("all-features.randomforest" ) #"all-features.ibk" "all-features.j48" "all-features.naivebayes" "all-features.libsvm")

# bestandsnaam van log-bestand en csv-bestand voor labels moet zelfde zijn, op extensie na!
declare -a sequenties=("sequentie5_m_r_20150421171628") # "sequentie4_a_l_20150420104158" "sequentie5_a_l_20150421171955" "sequentie4_m_r_20150420103941")


for sequentie in "${sequenties[@]}"
do

    for size in 1.0 1.5 2 # 2.5 3 3.5 4 4.5 5 5.5 6 6.5 7 7.5
    do
        for overlap in 0.0 0.2 0.25 0.30 0.35 0.40 0.45 0.5 0.55 0.6 0.65 0.7 0.75 0.8 0.85 # 0.9 0.95
        do
            for cutoff in 0.5 # 0.4 0.3 0.2 0.1 0.0
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
