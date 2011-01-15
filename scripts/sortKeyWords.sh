mkdir $2
mkdir $2/"R200"
mkdir $2/"R100"
mkdir $2/"Map"
for file in $(ls $1)
do 

#grep "^map " $1/$file | wc -l
#grep "^map " $1/$file | grep -v "^map\\s*.*0.0000" | wc -l
#echo " " 
grep "^map " $1/$file | sort -r -k3 | cut -c27- >> $2/"Map"/$file
#R200
#grep "^R200 " $1/$file | grep -v "^R200\\s*.*0.0000" | sort -r -k3 | cut -c27- >> $2/$file

grep "^R100 " $1/$file | sort -r -k3 | cut -c27- >> $2/"R100"/$file
grep "^R200 " $1/$file | sort -r -k3 | cut -c27- >> $2/"R200"/$file
#grep "^map " $1/$file | grep -v "^map\\s*.*0.0000" | sort -r -k3 | cut -c27- >> $2/$file
#grep "^map " $1/$file | sort -r -k3 | cut -c24- >> $2/$file -- trec file

done
