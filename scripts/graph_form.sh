newline=" "
count=0
check=0;
for file in $(ls $1)
do
	#echo $file
	#c=`echo $file | cut -c 7-`
	c=`echo $file | cut -c 8-`
	newline=$c
	count=0
	for line in $(grep "^map  " $1/$file)
	do 
		
		check=`expr $count % 3`
		#echo $check
		if [ $check -gt 0 ] 
		then
			newline=$newline" "$line 
		fi
		count=`expr $count + 1`
		if [ $check -eq 0 -a $count -gt 1 ]
		then
			echo $newline >> $2
			newline=$c;
		fi
	done
	echo $newline >> $2
	echo " "  >> $2	
	echo " "  >> $2	
done
 
					
