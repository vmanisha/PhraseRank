#copy the files to make test database
mkdir $3
for file in $(cat $1)
        do
        echo $file;
	cp $2/$file $3/$file;
        done

