mkdir $2
for dir in $(ls $1)
	do 
		c=`echo $dir | cut -c 12-`		
		for file in $(ls $1/$dir)
		do
			mv $1/$dir/$file $2/$file"_"$c
			
		done
	done

		
