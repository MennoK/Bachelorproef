set terminal png size 2000,1500 enhanced font "Arial,35"
set output 'plot.png'

datafile = 'FeatureSet/AllFeatures/InfoGain/RandomForest/results.dat'
set xrange [1:134]
set yrange [0:100]

set title "Titel"
set key invert box center right reverse Left
set xtics nomirror
set ytics nomirror

samples(x) = $0 > 1 ? 2 : ($0+1)
avg2(x) = (shift2(x), (back1+back2)/samples($0))
shift2(x) = (back2 = back1, back1 = x)
init(x) = (back1 = back2 = sum = 0)

plot sum = init(0), \
     datafile title 'data' with points pt 7 ps 1.2 lc rgb "black", \
     '' using 1:(avg2($2)) title "running mean over previous 2 points" pt 7 ps 0.5 lw 4 lc rgb "orange" smooth sbezier
