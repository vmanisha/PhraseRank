#mkdir $2
name=''
for file in $(cat $1)
do
	if [ $file -lt 10 ]	
	then name="000"$file
	elif [ $file -lt 100 ]
	then name="00"$file
	elif [ $file -lt 1000 ]
	then name="0"$file
	fi

	#cp $3/$name $2/$name
	echo $name
done
