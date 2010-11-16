i=1;
while [ $i -lt 10 ]
do 
#	echo "000"$i
	grep "^000"$i $1 | wc -l
	i=`expr $i + 1` 
done	

while [ $i -lt 100 ]
do
#        echo "00"$i
        grep "^00"$i $1 | wc -l
        i=`expr $i + 1`
done

while [ $i -lt 1000 ]
do
#   	echo "0"$i
        grep "^0"$i $1 | wc -l
        i=`expr $i + 1`  
done

