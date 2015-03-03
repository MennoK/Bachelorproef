# script uitvoeren met: gnuplot script.gp

set terminal png transparent size 950,400 enhanced font "Arial,20"
set output "plot.png"

set yrange [0:100]
unset key

set boxwidth 0.5
set style fill solid
plot "../Results/methodstraining.dat" using 2:xtic(1) with boxes lc rgb "orange"
