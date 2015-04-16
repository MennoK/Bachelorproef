list = system('ls *-gnuplot.dat')
i = 1

do for [file in list] {
    set output sprintf('%s.png', file)
    set title sprintf("%s | %d fs", file, i)
    splot file
    i = i + 1
}
