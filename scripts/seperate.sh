
for line in $(cat $1)
	do
	grep "qid:$line " $2 >> $3
	done

