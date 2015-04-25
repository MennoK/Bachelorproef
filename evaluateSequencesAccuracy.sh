#!/bin/bash

declare -a models=("all-features.randomforest")

# bestandsnaam van log-bestand en csv-bestand voor labels moet zelfde zijn, op extensie na!
declare -a sequenties=("sequentie1_a_l_20150210131904")

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

declare -a models=("all-features.vote-kstar-randomforest")

# bestandsnaam van log-bestand en csv-bestand voor labels moet zelfde zijn, op extensie na!
declare -a sequenties=("sequentie2_m_r_20150210132851")

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

declare -a models=("all-features.randomforest")

# bestandsnaam van log-bestand en csv-bestand voor labels moet zelfde zijn, op extensie na!
declare -a sequenties=("sequentie2_m_r_20150210132851")

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

declare -a models=("all-features.vote-kstar-randomforest")

# bestandsnaam van log-bestand en csv-bestand voor labels moet zelfde zijn, op extensie na!
declare -a sequenties=("sequentie3_a_r_20150210155328")

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

declare -a models=("all-features.randomforest")

# bestandsnaam van log-bestand en csv-bestand voor labels moet zelfde zijn, op extensie na!
declare -a sequenties=("sequentie3_a_r_20150210155328")

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

declare -a models=("all-features.vote-kstar-randomforest" "all-features.randomforest")

# bestandsnaam van log-bestand en csv-bestand voor labels moet zelfde zijn, op extensie na!
declare -a sequenties=("sequentie1_m_r_20150210131306" "sequentie1_a_l_20150210131904" "sequentie2_m_r_20150210132851" "sequentie3_a_r_20150210155328")

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