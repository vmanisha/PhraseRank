set term postscript eps enhanced "Helvetica" 10
set key right top outside spacing 1            
set output "test.eps"                          
set size .75,.75  
plot "graph_all_p" index 0 using 2:3 title 'M1' with points linestyle 2 ,\
"graph_all_p" index 1 using 2:3 title 'M2' with points linestyle 3 ,\
"graph_all_p" index 2 using 2:3 title 'M3' with points linestyle 4 ,\
"graph_all_p" index 3 using 2:3 title 'M4' with points linestyle 5 ,\
"graph_all_p" index 4 using 2:3 title 'M5' with points linestyle 6 ,\
"graph_all_p" index 5 using 2:3 title 'M6' with points linestyle 7 ,\
"graph_all_p" index 6 using 2:3 title 'M7' with points linestyle 1
