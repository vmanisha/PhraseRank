#bash script to list files of directory 
for dir in $(ls $1) 
do 
	echo $dir
	echo $1/$dir
	for file in $(ls $1/$dir)
	do
	echo $file
	echo $1/$dir/$file
	gunzip $1/$dir/$file
	done
done


