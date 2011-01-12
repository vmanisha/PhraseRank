set terminal postscript enhanced color "helvetica" 20
set output "outfile2.eps"
set boxwidth .75 absolute
set style data histogram
set style fill solid 0.5
set style histogram cluster gap 3
set key center bottom outside spacing 1 
set yrange [0:1]
set ylabel "Similarity"
set xlabel "Users"
plot 'graph_final' index 0 using 3 t "Personalized", '' using 2:xtic(1) t "Aggregate"

