import sys
#removing those relevance judgements which are not present in the corpus
#file 1 --> file containing the list to be removed
#file 2 --> file from which the content has to be removed
#file 3 --> file to write the modified output
def removeData(file1,file2):#,file3):
	 f1 = open(file1, 'r')
	 f2 = open(file2, 'r')
	 #f3 = open(file3, 'w')

	 list1= f1.read().split("\n")
	 list2= f2.read().split("\n")
	
	 for element in list2:
		name=element.split("\t")
		#print "name is", element

		if name!=[''] and name[2] not in list1:
			#f3.write("\n"+element)
			print element
	 f1.close()
	 f2.close()
	 #f3.close()

def main(argv):
	removeData(argv[1],argv[2])#,argv[3])

if __name__== "__main__":
	main(sys.argv)
		
			
	 

	
	 

	
