#!/bin/sh
sum=0;
n=0;
one=1;
for num in $(cat $1)
	do
		sum=`expr $sum + $num` 
		n= `expr $n + $one`

	done	
echo $sum/$n
