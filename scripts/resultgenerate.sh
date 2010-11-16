mkdir ~/result/$2
for file in $(ls $1)
do 
	./trec_eval -q -c -m all_trec ~/lib/sample_rels/rel.a ~/result/$file > ~/result/$2/$file
done
	
