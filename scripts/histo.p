set terminal postscript enhanced color "helvetica" 20
set output "missing.eps"
set boxwidth .75 absolute
set style data histogram
set style fill solid 0.5
set style histogram cluster gap 3
set key center bottom outside spacing 1 
set ylabel "Number of Patents in Solution"
set xlabel "Patent number"
plot 'missedFile' using 1 t "Total", '' using 2:xtic(1) t "Actual"

