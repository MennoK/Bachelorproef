# script uitvoeren met: gnuplot script.gp

# set terminal png transparent size 350,300 enhanced font "Arial,20"
# set output "FFTFeatures.png"

set terminal postscript eps size 2.5,2 enhanced color \
    font 'Computer Modern Roman,26' linewidth 2
set output "FFTFeatures.eps"


datafile = "../FFT/InfoGain/RandomForest/results.dat"
numFeatures = 84
xTics = 20
set xrange [1:numFeatures]
set yrange [0:100]

# set title "Titel" font "bold"
# set xlabel "Aantal features"
# set ylabel "Accuraatheid (%)"

set xtics xTics
set ytics nomirror

samples(x) = $0 > 1 ? 2 : ($0+1)
avg2(x) = (shift2(x), (back1+back2)/samples($0))
shift2(x) = (back2 = back1, back1 = x)
init(x) = (back1 = back2 = sum = 0)

plot sum = init(0), \
     datafile title "" with points pt 7 ps 0.8 lc rgb "black", \
     "" using 1:(avg2($2)) title "" pt 7 ps 0.5 lw 3.5 lc rgb "orange" smooth sbezier
