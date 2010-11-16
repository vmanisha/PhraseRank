i=0;
j=0;
k=0;

#cat ~/result/groupMap | cut -d' ' -f2- > list2
j=`cat $1 | wc -l`
for line in $(cat $1)
do 
	k=`echo $line | bc`
	i=`echo $i + $k | bc`

done 

echo "total" $i
echo "count" $j

m=`echo $i / $j | bc`
echo $m
