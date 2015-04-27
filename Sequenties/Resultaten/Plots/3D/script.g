set terminal pngcairo transparent size 1200,1000 enhanced font 'Arial,18'
set output '3dplot.png'
set dgrid3d 15,15
set pm3d at b
set ticslevel 0.8
set isosample 40,40
set nokey
set ytics ("0" 0, "20" 0.2, "40" 0.4, "60" 0.6, "80" 0.80)
splot "combi_seq.dat" using 1:2:($3*100) with lines
