mkdir $2
for file in $(ls $1)
do

grep "^at 0.20 " $1/$file | wc -l
grep "^at 0.20 " $1/$file | grep -v "^at 0.20.*0.0000" | wc -l
echo " " 
grep "^at 0.20 " $1/$file | grep -v "^at 0.20.*0.0000" | sort -r -k4 | cut -c23- >> $2/$file
#grep "^map " $1/$file | sort -r -k3 | cut -c24- >> $2/$file -- trec file
done
~                                                           
                                                                                                           
~           
