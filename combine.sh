#!/bin/sh
#Script voor csv

#java -jar ActivityRecognition.jar combineTraining ./Data2/Fietsen/Training-set/ ./TrainingSets/fietsen.csv
#java -jar ActivityRecognition.jar combineTraining ./Data2/LiftAU/Training-set/ ./TrainingSets/liftau.csv
#java -jar ActivityRecognition.jar combineTraining ./Data2/LiftAD/Training-set/ ./TrainingSets/liftad.csv
#java -jar ActivityRecognition.jar combineTraining ./Data2/Nietsdoen/Training-set/ ./TrainingSets/nietsdoen.csv
#java -jar ActivityRecognition.jar combineTraining ./Data2/Wandelen/Training-set/ ./TrainingSets/wandelen.csv
#java -jar ActivityRecognition.jar combineTraining ./Data2/Trapop/Training-set/ ./TrainingSets/trapop.csv
#java -jar ActivityRecognition.jar combineTraining ./Data2/Lopen/Training-set/ ./TrainingSets/lopen.csv
#java -jar ActivityRecognition.jar combineTraining ./Data2/Trapaf/Training-set/ ./TrainingSets/trapaf.csv
#java -jar ActivityRecognition.jar combineTraining ./Data2/Tandenpoetsen/Training-set/ ./TrainingSets/tandenpoetsen.csv
#java -jar ActivityRecognition.jar combineTraining ./Data2/Springen/Training-set/ ./TrainingSets/springen.csv


java -jar ActivityRecognition.jar combineTraining ./TrainingSets/ ./TrainingSets/trainingset.csv
