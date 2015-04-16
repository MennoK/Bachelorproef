list = system('ls *-gnuplot.dat')

do for [file in list] {
    set output sprintf('%s.png', file)

    plot file using 1:2
}
