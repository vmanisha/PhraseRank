#process each file with uspto.prl
i=1993;
for file in $(ls $1)
	do 
	perl uspto_tag.prl $1/$file > $1/$i.txt
	i=$i+1
	done

